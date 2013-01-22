package gov.usgs.cida.harri.main;

import java.util.Collection;

import gov.usgs.cida.harri.service.ExampleServiceCalls;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.*;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.registry.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarriManagerService implements Runnable {
	Logger LOG = LoggerFactory.getLogger(HarriManagerService.class);
	
	private UpnpService harriManagerUpnpService;
	
	public static final String DEVICE_TYPE = "HARRI_Device";
	public static final String DEVICE_MANUFACTURER = "CIDA";
	
	/** 
	 * Default refresh rate in minutes.
	 */
	private static final double DEFAULT_REFRESH_RATE = .5;
	
	/** 
	 * user specified refresh rate in minutes.
	 */
	private static double specifiedRefreshRate;

	public static void main(String[] args) throws Exception {
		if(args.length>0) {
			try {
				specifiedRefreshRate = Double.parseDouble(args[0]);
			} catch (Exception e) {}
		}
		// Start a user thread that runs the UPnP stack
		Thread clientThread = new Thread(new HarriManagerService());
		clientThread.setDaemon(false);
		clientThread.start();
	}

	@Override
	public void run() {
		try {
			LOG.info("HARRI Manager Service starting");
			harriManagerUpnpService = new UpnpServiceImpl();

			// Add a listener for device registration events
			harriManagerUpnpService.getRegistry().addListener(
					createRegistryListener(harriManagerUpnpService)
					);

			LOG.info("Broadcasting a search message for all known devices");
			harriManagerUpnpService.getControlPoint().search(
			        new UDADeviceTypeHeader(new UDADeviceType(DEVICE_TYPE))
					);
			LOG.info("HARRI Manager Service started successfully");
			
			//refresh (use all devices) at regular intervals
			double refreshRate = DEFAULT_REFRESH_RATE;
			if(specifiedRefreshRate > 0) {
				refreshRate = specifiedRefreshRate;
			}
			long longRate = (long) (refreshRate * 60000);
			LOG.info("Refresh rate for HARRI devices is " + longRate + "ms");
			while(true) {
				runHarriProcesses(harriManagerUpnpService);
				Thread.sleep(longRate);
			}
		} catch (Exception ex) {
			System.err.println("Exception occured: " + ex);
			System.exit(1);
		}
	}

	private RegistryListener createRegistryListener(final UpnpService upnpService) {
		return new DefaultRegistryListener() {
			private boolean isHarriDevice(final RemoteDevice device) {
				return device.getDetails().getManufacturerDetails()!=null &&
						device.getDetails().getManufacturerDetails().getManufacturer().equals(DEVICE_MANUFACTURER) && 
						device.getDetails().getModelDetails().getModelName().contains(DEVICE_TYPE);
			}

			@Override
			public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
				//if not a HARRI device, do nothing
				if(!isHarriDevice(device)){
					return;
				}
				LOG.info("HARRI Device has been added: " + device.getDetails().getModelDetails().getModelName());
			}

			@Override
			public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
				if(!isHarriDevice(device)){
					return;
				}
				LOG.info("HARRI Device " + device.getDetails().getModelDetails().getModelName() + " has been removed");
			}

			@Override
			public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
				if(!isHarriDevice(device)){
					return;
				}
				LOG.info("HARRI Device " + device.getDetails().getModelDetails().getModelName() + " has been updated");
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
	
	
	private void runHarriProcesses(final UpnpService harriManagerUpnpService) {
		LOG.info("Refreshing data (running all known HARRI Services)");
		Collection<Device> allDevices = harriManagerUpnpService.getRegistry().getDevices();
		for(Device d : allDevices) {
			ExampleServiceCalls.doExampleServiceCall(harriManagerUpnpService, (RemoteDevice) d); //TODO delete when not needed
		}
	}
}
