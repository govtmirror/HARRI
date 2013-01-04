package gov.usgs.cida.harri.device.service;

import org.teleal.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId("ExampleHarriService"),
        serviceType = @UpnpServiceType(value = "ExampleHarriService", version = 1)
)

/**
 * Example service that can be called via UPnP. This can probably be deleted after stubbing is done.
 * 
 * @author thongsav
 *
 */
public class ExampleHarriService {
    @UpnpStateVariable(defaultValue = "no_id_provided")
    private String harriManagerId = "no_id_provided";
	
    @UpnpAction
    public void doExampleAction(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	this.harriManagerId = harriManagerId;
        System.out.println("This example action was called by the HARRI Manager with ID: " + this.harriManagerId);
    }
}