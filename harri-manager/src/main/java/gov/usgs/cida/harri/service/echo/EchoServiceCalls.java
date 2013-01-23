package gov.usgs.cida.harri.service.echo;

import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class EchoServiceCalls {
	static Logger LOG = LoggerFactory.getLogger(EchoServiceCalls.class);

	public static void echoHostname(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor exampleCall = new HarriServiceExecutor(upnpService, device, "EchoService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());
		exampleCall.executeAction("EchoHostname", params, "EchoHostnameResponse");
	}
	

}
