package gov.usgs.cida.harri.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class ExampleServiceCalls {
	static Logger LOG = LoggerFactory.getLogger(ExampleServiceCalls.class);

	public static void doExampleServiceCall(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor exampleCall = new HarriServiceExecutor(upnpService, device, "ExampleHarriService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", getSystemHostName());
		exampleCall.executeAction("DoExampleAction", params, "ExampleActionResponse");
	}
	
    private static String getSystemHostName() {
    	String hostName = "NoHostNameFound";
    	try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return hostName;
    }
}
