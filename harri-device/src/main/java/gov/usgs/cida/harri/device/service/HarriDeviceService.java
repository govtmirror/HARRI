package gov.usgs.cida.harri.device.service;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.*;
import org.teleal.cling.binding.annotations.*;
import org.teleal.cling.model.*;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.*;

import java.io.IOException;

public class HarriDeviceService implements Runnable {

    public static void main(String[] args) throws Exception {
        // Start a user thread that runs the UPnP stack
        Thread serverThread = new Thread(new HarriDeviceService());
        serverThread.setDaemon(false);
        serverThread.start();
    }

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
                    createDevice("cida-example-host1", 1) //TODO get this hostname string and version number from somewhere useful
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
                        UDN.uniqueSystemIdentifier("HARRI_Device_" + hostName)
                );

        DeviceType type =
                new UDADeviceType("HARRI_Device", version);

        DeviceDetails details =
                new DeviceDetails(
                        "HARRI_Device",
                        new ManufacturerDetails("CIDA"),
                        new ModelDetails(
                                "HARRI_Device_" + hostName,
                                "This is a harry device installed on " + hostName,
                                "v" + version
                        )
                );


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

        return new LocalDevice(identity, type, details, exampleHarriActionService);
    }
}
