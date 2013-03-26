package gov.usgs.cida.harri.manager.service.echo;

import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class EchoManagerServiceProvider implements IHarriManagerServiceProvider {
	private static Logger LOG = LoggerFactory.getLogger(EchoManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor exampleCall = new HarriServiceExecutor(upnpService, device, "EchoService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());
		exampleCall.executeAction("EchoHostname", params, "EchoHostnameResponse");
	}
}
