package gov.usgs.cida.harri.device.service.httpd;

import gov.usgs.cida.harri.commons.datamodel.ApacheConfiguration;
import gov.usgs.cida.harri.commons.datamodel.ProxyMapping;
import gov.usgs.cida.harri.commons.interfaces.device.IHarriDeviceServiceProvider;
import gov.usgs.cida.harri.httpd.ParseHTTPdConf;
import gov.usgs.cida.harri.util.HarriUtils;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

import com.google.gson.Gson;

@UpnpService(
        serviceId = @UpnpServiceId("HTTPdProxyService"),
        serviceType = @UpnpServiceType(value = "HTTPdProxyService", version = 1)
)

/**
 * This can probably be deleted after stubbing is done.
 * 
 * @author thongsav
 *
 */
public class HttpdDeviceServiceProvider implements IHarriDeviceServiceProvider {
    @UpnpStateVariable(defaultValue = "default response value")
    private String getProxyMappingResponse = null;
    
    @UpnpAction(out = @UpnpOutputArgument(name = "GetProxyMappingResponse")) //example of how to get a response, could do by convention
    public String getProxyMapping() {
    	ApacheConfiguration ac = new ApacheConfiguration();
    	ac.setIdentifier(HarriUtils.getSystemHostName());

    	try {
            List<ProxyMapping> proxyMappingList = ParseHTTPdConf.getProxyMappingList();
            ac.setProxyMappings(proxyMappingList);
        } catch (IOException e) {
        }
            
        if(ac.getProxyMappings() == null || ac.getProxyMappings().isEmpty()) {
        	return null;
        }
        
        getProxyMappingResponse = new Gson().toJson(ac);
        return getProxyMappingResponse;
    }
}