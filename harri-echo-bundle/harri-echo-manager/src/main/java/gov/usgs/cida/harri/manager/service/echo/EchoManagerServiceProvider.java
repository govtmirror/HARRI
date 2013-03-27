package gov.usgs.cida.harri.manager.service.echo;

import gov.usgs.cida.harri.commons.datamodel.Echo;
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

public class EchoManagerServiceProvider implements IHarriManagerServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(EchoManagerServiceProvider.class);

	@Override
	public void doServiceCalls(final UpnpService upnpService, final RemoteDevice device, final IHarriDAO dao) {
		String serviceName = "EchoService";
		HarriServiceExecutor pds = new HarriServiceExecutor(upnpService, device, serviceName);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("HarriManagerId", HarriUtils.getSystemHostName());

		upnpService.getControlPoint().execute(new ActionCallback(pds.prepareActionInvocation("EchoHostname", params)) {
			@Override
			public void success(ActionInvocation invocation) {
				assert invocation.getOutput().length == 0;
				String deviceName = invocation.getAction().getService().getDevice().getDetails().getModelDetails().getModelName();
				String responseMessage = "Service \"EchoHostname\" successfully called on " + deviceName;
				responseMessage += "\n" + invocation.getOutput("EchoHostnameResponse").toString();
				LOG.debug(responseMessage);
				
				Echo echo = new Echo();
				echo.setIdentifier(deviceName);
				echo.setManagerId(HarriUtils.getSystemHostName());
				echo.setHost(invocation.getOutput("EchoHostnameResponse").toString());
				
				if(dao.read(echo) == null) {
					dao.create(echo);
				} else {
					dao.update(echo);
				}
			}

			@Override
			public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
				LOG.error(defaultMsg);
			}
		});
	}
}
