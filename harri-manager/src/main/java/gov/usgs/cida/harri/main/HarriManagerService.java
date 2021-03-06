package gov.usgs.cida.harri.main;

import gov.usgs.cida.harri.commons.cling.ApacheUpnpServiceConfiguration;
import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAOFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import gov.usgs.cida.harri.commons.interfaces.manager.IHarriExternalServiceProvider;
import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.util.HarriUtils;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.*;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.registry.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarriManagerService implements Runnable {
	private static Logger LOG = LoggerFactory.getLogger(HarriManagerService.class);
	
	private UpnpService harriManagerUpnpService;
	private static IHarriDAOFactory daoFactory;
	
    private static List<IHarriManagerServiceProvider> harriManagerServices;
    private static List<IHarriExternalServiceProvider> harriExternalServices;
    
	/** 
	 * Default refresh rate in milliseconds.
	 */
	private static final double DEFAULT_REFRESH_RATE = 30000;

	public static void main(String[] args) throws Exception {
	    loadHarriManagerServices();
	    
		// Start a user thread that runs the UPnP stack
		Thread clientThread = new Thread(new HarriManagerService());
		clientThread.setDaemon(false); //TODO provide graceful shutdown mechanism
		clientThread.start();
	}

	@Override
	public void run() {
		try {
			LOG.info("HARRI Manager Service starting");
			harriManagerUpnpService = new UpnpServiceImpl(new ApacheUpnpServiceConfiguration());

			// Add a listener for device registration events
			harriManagerUpnpService.getRegistry().addListener(
					createRegistryListener(harriManagerUpnpService));

			LOG.debug("Broadcasting a search message for all known devices");
			harriManagerUpnpService.getControlPoint().search(
			        new UDADeviceTypeHeader(new UDADeviceType(HarriUtils.DEVICE_TYPE)));
			
			//refresh (use all devices) at regular intervals
			double refreshRate = DEFAULT_REFRESH_RATE;
			try {
				refreshRate = Double.parseDouble(HarriUtils.getHarriConfigs().getProperty("refresh.rate.ms"));
			} catch (Exception e) {
				LOG.warn("Failed to parse or find value in refresh.rate.ms property of configs");
			}
			
			LOG.info("Refresh rate for HARRI devices is " + refreshRate + "ms");

			LOG.info("HARRI Manager Service started successfully");
			while(true) { //TODO provide graceful shutdown mechanism
				runHarriProcesses(harriManagerUpnpService);
				Thread.sleep((long)refreshRate);
			}

		} catch (Exception ex) {
			LOG.error("Exception occured: " + ex);
			System.exit(1);
		}
	}

	private RegistryListener createRegistryListener(final UpnpService upnpService) {
		return new DefaultRegistryListener() {
			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				if(!HarriUtils.isHarriDevice(device)){
					return;
				}
				LOG.debug("HARRI Device has been added: " + device.getDetails().getModelDetails().getModelName());
			}

			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				if(!HarriUtils.isHarriDevice(device)){
					return;
				}
				LOG.debug("HARRI Device " + device.getDetails().getModelDetails().getModelName() + " has been removed");
			}

			@Override
			public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
				if(!HarriUtils.isHarriDevice(device)){
					return;
				}
			}

			@Override
			public void beforeShutdown(Registry registry) {
				LOG.info("HARRI Manager is shutting sown. Devices in registry: " + registry.getDevices().size());
			}

			@Override
			public void afterShutdown() {
				LOG.info("HARRI Manager Registry has been shut down");
			}
		};
	}
	
	private static void loadHarriManagerServices() {
		LOG.debug("ServiceLoaders loading harri manager services");
		
		LOG.debug("loading IHarriExternalServices");
		harriExternalServices = new ArrayList<IHarriExternalServiceProvider>();
		ServiceLoader<IHarriExternalServiceProvider> externalSL = ServiceLoader.load(IHarriExternalServiceProvider.class);
		for (Iterator<IHarriExternalServiceProvider> exslIter = externalSL.iterator(); exslIter.hasNext(); ) {
			harriExternalServices.add(exslIter.next());
	    }
		
		LOG.debug("loading IHarriManagerServices");
		harriManagerServices = new ArrayList<IHarriManagerServiceProvider>();
		ServiceLoader<IHarriManagerServiceProvider> managerSL = ServiceLoader.load(IHarriManagerServiceProvider.class);
		for (Iterator<IHarriManagerServiceProvider> mslIter = managerSL.iterator(); mslIter.hasNext(); ) {
			harriManagerServices.add(mslIter.next());
	    }
		
		LOG.debug("loading IHarriDAOFactory");
		ServiceLoader<IHarriDAOFactory> daoFactorySL = ServiceLoader.load(IHarriDAOFactory.class);
		daoFactory = daoFactorySL.iterator().next();
	}
	
	private void runHarriProcesses(final UpnpService harriManagerUpnpService) {
		LOG.debug("Refreshing data (running all known HARRI Services)");
		
		//EXTERNAL SERVICE CALLS
		for(IHarriExternalServiceProvider es : harriExternalServices) {
			String name = "unknown";
			try {
				name = es.getClass().getName();
				es.doServiceCalls(HarriUtils.getSystemHostName(), daoFactory.getDAO());
			} catch (Exception  e) {
				LOG.error("Runtime exception while calling external service (" + name + "): " + e.getMessage());
			}
		}
		
		//REMOTE HARRI DEVICE CALLS
		Collection<Device> allDevices = harriManagerUpnpService.getRegistry().getDevices();
		for(Device d : allDevices) {
			if(!HarriUtils.isHarriDevice(d)){
				continue;
			}
			LOG.debug("Calling all services on " + d.getDetails().getModelDetails().getModelName());
			for(IHarriManagerServiceProvider ms : harriManagerServices) {
				String name = "unknown";
				try {
					name = ms.getClass().getName();
					ms.doServiceCalls(harriManagerUpnpService, (RemoteDevice) d, daoFactory.getDAO());
				} catch (RuntimeException e) {
					LOG.error("Runtime exception while calling remote device services (" + name + "): " + e.getMessage());
				}
			}
		}
	}
}
