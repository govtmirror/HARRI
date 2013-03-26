package gov.usgs.cida.harri.manager.service.tomcat;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public class TomcatManagerServiceProvider implements IHarriManagerServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(TomcatManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device, IHarriDAO dao) {
		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, "TomcatDiscoveryService");

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());
		pds.executeAction("GetAllTomcatInstances", params, "GetAllTomcatInstancesResponse");
		pds.executeAction("GetAllTomcatApps", params, "GetAllTomcatAppsResponse");
	}
}
