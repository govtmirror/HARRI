package gov.usgs.cida.harri.instance;

import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class InstanceDiscoveryServiceCalls {
	static Logger LOG = LoggerFactory.getLogger(InstanceDiscoveryServiceCalls.class);

	public static void doServiceCalls(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, "InstanceDiscoveryService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());
		pds.executeAction("GetAllTomcatInstances", params, "GetAllTomcatInstancesResponse");
		pds.executeAction("GetAllDjangoInstances", params, "GetAllDjangoInstancesResponse");
	}
}
