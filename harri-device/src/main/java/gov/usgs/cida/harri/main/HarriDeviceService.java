package gov.usgs.cida.harri.main;

import gov.usgs.cida.harri.commons.interfaces.device.IHarriDeviceServiceProvider;
import gov.usgs.cida.harri.util.HarriUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.*;
import org.teleal.cling.binding.annotations.*;
import org.teleal.cling.model.*;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.*;

public class HarriDeviceService implements Runnable {
	static Logger LOG = LoggerFactory.getLogger(HarriDeviceService.class);

    private static List<IHarriDeviceServiceProvider> harriDeviceServiceProviders;
	
    public static void main(String[] args) throws Exception {
    	LOG.info("Starting HARRI device");
    	loadHarriDeviceServiceProviders();
    	
    	//TODO dependency check and fatal error on start up
    	
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new HarriDeviceService());
        serverThread.setDaemon(false);
        serverThread.start();
    	LOG.info("HARRI device started");
    }

    @Override
    public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice(
                    	HarriUtils.getSystemHostName(), 
                    	getDeviceVersion()
                    ) 
            );
            
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private LocalDevice createDevice(final String hostName, final Integer version)
            throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity =
                new DeviceIdentity(
                        UDN.uniqueSystemIdentifier(HarriUtils.DEVICE_TYPE + "_" + hostName)
                );

        DeviceType type =
                new UDADeviceType(HarriUtils.DEVICE_TYPE, version);

        DeviceDetails details =
                new DeviceDetails(
                		HarriUtils.DEVICE_TYPE,
                        new ManufacturerDetails(HarriUtils.DEVICE_MANUFACTURER),
                        new ModelDetails(
                        		HarriUtils.DEVICE_TYPE + "_" + hostName,
                                "This is a harry device installed on " + hostName,
                                "v" + version
                        )
                );

        LocalService[] deviceServices = bindServicesToDevices();
        
        LOG.info("HARRI Device created: " + HarriUtils.DEVICE_TYPE + "_" + hostName);
        return new LocalDevice(identity, type, details, deviceServices);
    }
    
    private LocalService[] bindServicesToDevices() {
    	List<LocalService<IHarriDeviceServiceProvider>> serviceProviders = 
    			new ArrayList<LocalService<IHarriDeviceServiceProvider>>();
    	for(IHarriDeviceServiceProvider dsp : harriDeviceServiceProviders) {
    		Class clazz = dsp.getClass();
    		@SuppressWarnings("unchecked")
    		LocalService<IHarriDeviceServiceProvider> service =
                    new AnnotationLocalServiceBinder().read(clazz);
			service.setManager(
                new DefaultServiceManager<IHarriDeviceServiceProvider>(service, clazz)
            );
    		serviceProviders.add(service);
    	}
        
    	return serviceProviders.toArray(new LocalService[]{});
    }
    
	private static void loadHarriDeviceServiceProviders() {
		LOG.debug("ServiceLoaders loading harri device service providers");
		
		LOG.debug("loading IHarriDeviceServiceProviders");
		harriDeviceServiceProviders = new ArrayList<IHarriDeviceServiceProvider>();
		ServiceLoader<IHarriDeviceServiceProvider> hdsp = ServiceLoader.load(IHarriDeviceServiceProvider.class);
		for (Iterator<IHarriDeviceServiceProvider> hdspIter = hdsp.iterator(); hdspIter.hasNext(); ) {
			harriDeviceServiceProviders.add(hdspIter.next());
	    }
	}
    
    private Integer getDeviceVersion() {
    	//TODO get this from somewhere useful
    	return 1;
    }
}
