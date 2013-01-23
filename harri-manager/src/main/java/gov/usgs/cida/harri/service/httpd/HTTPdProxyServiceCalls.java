package gov.usgs.cida.harri.service.httpd;

import gov.usgs.cida.harri.service.echo.*;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.model.meta.RemoteDevice;

public class HTTPdProxyServiceCalls {
	static Logger LOG = LoggerFactory.getLogger(HTTPdProxyServiceCalls.class);

	public static void listProxyMappings(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor exampleCall = new HarriServiceExecutor(upnpService, device, "HTTPdProxyService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		exampleCall.executeAction("ListProxyMappings", params, "ListProxyMappingsResponse");
	}
	

}
