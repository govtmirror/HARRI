package gov.usgs.cida.harri.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	Logger LOG = LoggerFactory.getLogger(ExampleHarriService.class);
	
    @UpnpStateVariable(defaultValue = "no_id_provided")
    private String harriManagerId = "no_id_provided";
	
    @UpnpAction
    public void doExampleAction(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	this.harriManagerId = harriManagerId;
        LOG.info("This example action was called by the HARRI Manager with ID: " + this.harriManagerId);
    }
}