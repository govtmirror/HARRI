package gov.usgs.cida.harri.service.discovery;

import gov.usgs.cida.harri.service.HarriServiceExecutor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class ProcessDiscoveryServiceCalls {
	static Logger LOG = LoggerFactory.getLogger(ProcessDiscoveryServiceCalls.class);

	public static void doServiceCalls(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, "ProcessDiscoveryService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", getSystemHostName());
		pds.executeAction("GetAllProcesses", params, "GetAllProcessesResponse");
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
