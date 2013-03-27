package gov.usgs.cida.harri.manager.service.django;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.commons.interfaces.manager.IHarriManagerServiceProvider;
import gov.usgs.cida.harri.service.HarriServiceExecutor;
import gov.usgs.cida.harri.util.HarriUtils;
import java.util.HashMap;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteDevice;

public class DjangoManagerServiceProvider implements IHarriManagerServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(DjangoManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device, IHarriDAO dao) {
		String serviceName = "DjangoDiscoveryService";
		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, serviceName);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());

		upnpService.getControlPoint().execute(new ActionCallback(pds.prepareActionInvocation("GetAllDjangoInstances", params)) {
			@Override
			public void success(ActionInvocation invocation) {
				assert invocation.getOutput().length == 0;
				String deviceName = invocation.getAction().getService().getDevice().getDetails().getModelDetails().getModelName();
				String responseMessage = "Service \"GetAllDjangoInstances\" successfully called on " + deviceName;
				responseMessage += "\n" + invocation.getOutput("GetAllDjangoInstancesResponse").toString();
				LOG.info(responseMessage);
			}

			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
				LOG.error(defaultMsg);
			}
		});
		
		upnpService.getControlPoint().execute(new ActionCallback(pds.prepareActionInvocation("GetAllDjangoApps", params)) {
			@Override
			public void success(ActionInvocation invocation) {
				assert invocation.getOutput().length == 0;
				String deviceName = invocation.getAction().getService().getDevice().getDetails().getModelDetails().getModelName();
				String responseMessage = "Service \"GetAllDjangoApps\" successfully called on " + deviceName;
				responseMessage += "\n" + invocation.getOutput("GetAllDjangoAppsResponse").toString();
				LOG.info(responseMessage);
			}

			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
				LOG.error(defaultMsg);
			}
		});
	}
}
