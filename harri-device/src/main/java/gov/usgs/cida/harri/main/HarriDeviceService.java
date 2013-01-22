package gov.usgs.cida.harri.main;

import gov.usgs.cida.harri.service.ExampleHarriService;
import gov.usgs.cida.harri.service.discovery.ProcessDiscoveryService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
	Logger LOG = LoggerFactory.getLogger(HarriDeviceService.class);
	
	public static final String DEVICE_TYPE = "HARRI_Device";
	public static final String DEVICE_MANUFACTURER = "CIDA";

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new HarriDeviceService());
        serverThread.setDaemon(false);
        serverThread.start();
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
                    	getSystemHostName(), 
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
                        UDN.uniqueSystemIdentifier(DEVICE_TYPE + "_" + hostName)
                );

        DeviceType type =
                new UDADeviceType(DEVICE_TYPE, version);

        DeviceDetails details =
                new DeviceDetails(
                		DEVICE_TYPE,
                        new ManufacturerDetails(DEVICE_MANUFACTURER),
                        new ModelDetails(
                        		DEVICE_TYPE + "_" + hostName,
                                "This is a harry device installed on " + hostName,
                                "v" + version
                        )
                );


        LocalService[] deviceServices = bindServicesToDevice();
        
        LOG.info("HARRI Device created: " + DEVICE_TYPE + "_" + hostName);
        return new LocalDevice(identity, type, details, deviceServices);
    }
    
    private LocalService[] bindServicesToDevice() {
    	/* TODO Several services can be bound to the same device. At this point we might want to do an inspection
         * of machine the device is installed on and determine what kind of device/client this will become 
         * (tomcat, django, oracle), eg:
         * return new LocalDevice(
         *         identity, type, details, icon,
         *        new LocalService[] {tomcatServices, djangoServies, oracle}
         * );
         */
        
        @SuppressWarnings("unchecked")
		LocalService<ExampleHarriService> exampleHarriActionService =
                new AnnotationLocalServiceBinder().read(ExampleHarriService.class);
        exampleHarriActionService.setManager(
                new DefaultServiceManager<ExampleHarriService>(exampleHarriActionService, ExampleHarriService.class)
        );
        
        //bind process query
        @SuppressWarnings("unchecked")
		LocalService<ProcessDiscoveryService> pds =
                new AnnotationLocalServiceBinder().read(ProcessDiscoveryService.class);
        pds.setManager(
                new DefaultServiceManager<ProcessDiscoveryService>(pds, ProcessDiscoveryService.class)
        );
        
    	return new LocalService[] {exampleHarriActionService, pds};
    }
    
    private String getSystemHostName() {
    	String hostName = "NoHostNameFound";
    	try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return hostName;
    }
    
    private Integer getDeviceVersion() {
    	//TODO get this from somewhere useful
    	return 1;
    }
}
