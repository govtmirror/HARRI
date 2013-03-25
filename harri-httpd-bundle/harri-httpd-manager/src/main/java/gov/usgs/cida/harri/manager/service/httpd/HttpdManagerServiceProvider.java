package gov.usgs.cida.harri.manager.service.httpd;

import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class HttpdManagerServiceProvider  implements IHarriManagerServiceProvider {
	static private Logger LOG = LoggerFactory.getLogger(HttpdManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor exampleCall = new HarriServiceExecutor(upnpService, device, "HTTPdProxyService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		exampleCall.executeAction("ListProxyMappings", params, "ListProxyMappingsResponse");
	}
	

}
