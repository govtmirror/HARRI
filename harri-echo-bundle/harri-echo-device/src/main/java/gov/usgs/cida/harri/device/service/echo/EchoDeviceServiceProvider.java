package gov.usgs.cida.harri.device.service.echo;

import gov.usgs.cida.harri.commons.interfaces.device.IHarriDeviceServiceProvider;
import gov.usgs.cida.harri.util.HarriUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

/**
 *
 * @author isuftin
 */
@UpnpService(
        serviceId = @UpnpServiceId("EchoService"),
        serviceType = @UpnpServiceType(value = "EchoService", version = 1)
)

/**
 * This can probably be deleted after stubbing is done.
 * 
 * @author thongsav
 *
 */
public class EchoDeviceServiceProvider implements IHarriDeviceServiceProvider{
	private static final Logger LOG = LoggerFactory.getLogger(EchoDeviceServiceProvider.class);
	
    @UpnpStateVariable(defaultValue = "no_id_provided")
    private String harriManagerId = "no_id_provided";
    
    @UpnpStateVariable(defaultValue = "default response value")
    private String echoHostnameResponse = "default response value";
	
    @UpnpAction(out = @UpnpOutputArgument(name = "EchoHostnameResponse")) //example of how to get a response, could do by convention
    public String echoHostname(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	this.harriManagerId = harriManagerId;
        LOG.debug("EchoHostname action was called by HARRI Manager with ID: " + this.harriManagerId);
        
        echoHostnameResponse = HarriUtils.getSystemHostName();
        return echoHostnameResponse;
    }
}