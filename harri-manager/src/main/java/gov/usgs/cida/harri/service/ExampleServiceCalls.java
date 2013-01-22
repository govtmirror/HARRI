package gov.usgs.cida.harri.service;

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
		params.put("HarriManagerId", "EXAMPLE_HARRI_MANAGER_ID");
		exampleCall.executeAction("DoExampleAction", params);
	}
}
