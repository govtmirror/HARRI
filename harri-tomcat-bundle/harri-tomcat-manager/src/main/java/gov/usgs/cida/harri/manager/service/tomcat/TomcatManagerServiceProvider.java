package gov.usgs.cida.harri.manager.service.tomcat;

import gov.usgs.cida.harri.commons.datamodel.TomcatHost;
import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteDevice;

import com.google.gson.Gson;

public class TomcatManagerServiceProvider implements IHarriManagerServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(TomcatManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device, final IHarriDAO dao) {
		String discoveryServiceName = "TomcatDiscoveryService";
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("HarriManagerId", HarriUtils.getSystemHostName());

		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, discoveryServiceName);

		upnpService.getControlPoint().execute(new ActionCallback(pds.prepareActionInvocation("GetTomcatHost", params)) {
			@Override
			public void success(ActionInvocation invocation) {
				String deviceName = invocation.getAction().getService().getDevice().getDetails().getModelDetails().getModelName();
				String responseMessage = "Service \"GetTomcatHost\" successfully called on " + deviceName;
				LOG.debug(responseMessage);
				
				String jsonResponse = invocation.getOutput("GetTomcatHostResponse").toString();
				
				TomcatHost tcHost = new Gson().fromJson(jsonResponse, TomcatHost.class);
				if(dao.read(tcHost) == null) {
					dao.create(tcHost);
				} else {
					dao.update(tcHost);
				}
			}

			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
				LOG.error(defaultMsg);
			}
		});
	}
}
