package gov.usgs.cida.harri.manager.service.django;

import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class DjangoManagerServiceProvider  implements IHarriManagerServiceProvider {
	static Logger LOG = LoggerFactory.getLogger(DjangoManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device){
		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, "InstanceDiscoveryService");
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());
		pds.executeAction("GetAllDjangoInstances", params, "GetAllDjangoInstancesResponse");
		pds.executeAction("GetAllDjangoApps", params, "GetAllDjangoAppsResponse");
	}
}
