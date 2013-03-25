package gov.usgs.cida.harri.device.service.httpd;

import gov.usgs.cida.harri.httpd.ParseHTTPdConf;
import gov.usgs.cida.harri.httpd.ProxyMapping;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

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
public class HttpdDeviceServiceProvider {
	Logger LOG = LoggerFactory.getLogger(HttpdDeviceServiceProvider.class);
    
//    @UpnpStateVariable(defaultValue = "no_id_provided")
//    private String harriManagerId = "no_id_provided";
    
    @UpnpStateVariable(defaultValue = "default response value")
    private String listProxyMappingsResponse = "default response value";
    
    @UpnpAction(out = @UpnpOutputArgument(name = "ListProxyMappingsResponse")) //example of how to get a response, could do by convention
    public String listProxyMappings() {
    	StringBuilder sb = new StringBuilder();
                
//        sb.append(harriManagerId).append('\n');
        try {
            List<ProxyMapping> proxyMappingList = ParseHTTPdConf.getProxyMappingList();
            for (ProxyMapping proxyMapping : proxyMappingList) {
                for (String toURL : proxyMapping.getToURLList()) {
                    sb.append(proxyMapping.getFromHost()).append(proxyMapping.getFromPath()).append(" -> ").append(toURL).append('\n');
                }
                
            }
        } catch (IOException e) {
            
        }
        listProxyMappingsResponse = sb.length() > 0 ? sb.toString() : "[No Mappings Found]";
        return listProxyMappingsResponse;
    }
}