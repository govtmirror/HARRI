package gov.usgs.cida.harri.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.model.action.ActionArgumentValue;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;

public class HarriServiceExecutor {
	Logger LOG = LoggerFactory.getLogger(HarriServiceExecutor.class);

	private Device harriDevice;
	private ServiceId serviceId;
	private Service service;
	private UpnpService upnpService;
	
	HarriServiceExecutor(UpnpService inUpnpService, Device device, String inServiceName) {
		upnpService = inUpnpService;
		serviceId = new UDAServiceId(inServiceName);
		service = device.findService(serviceId);
		if (serviceId != null &&
			service == null
				) {
			String errMsg = "HARRI serive named " + inServiceName + 
					" not found on HARRI device " + device.getDetails().getModelDetails().getModelName();
			LOG.error(errMsg);
			throw new RuntimeException(errMsg);
		}
	}
	
	public void executeAction(final String actionName, final Map<String,String> params, final String expectResponseVariable) {
		ActionInvocation setTargetInvocation = new HarriActionInvocation(service, actionName, params);

		// Executes asynchronous in the background
		upnpService.getControlPoint().execute(
				new ActionCallback(setTargetInvocation) {

					@Override
					public void success(ActionInvocation invocation) {
						assert invocation.getOutput().length == 0;
						LOG.info("Service " + actionName + " successfully called.");
						if(expectResponseVariable != null) {
							//TODO how do we get this response back up to the manager (or somewhere useful)
							LOG.info("MANAGER received following response from device service call: " + invocation.getOutput(expectResponseVariable).toString());
						}
					}

					@Override
					public void failure(ActionInvocation invocation,
							UpnpResponse operation,
							String defaultMsg) {
						LOG.error(defaultMsg);
					}
				}
				);
	}

	private class HarriActionInvocation extends ActionInvocation {
		HarriActionInvocation(Service service, String actionName, Map<String, String> params) {
			super(service.getAction(actionName)); 
			Action action = service.getAction(actionName);
			if(action == null) {
				String errMsg = "Action " + actionName + " not found on HARRI service named " + serviceId.toString();
				LOG.error(errMsg);
				throw new RuntimeException(errMsg);
			}
			
			try {
				for(String k : params.keySet()) {
					setInput(k, params.get(k));
				}
			} catch (InvalidValueException ex) {
				LOG.error(ex.getMessage());
			}
		}
	}
}
