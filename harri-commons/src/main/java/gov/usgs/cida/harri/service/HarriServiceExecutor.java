package gov.usgs.cida.harri.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;

public class HarriServiceExecutor {

	private static Logger LOG = LoggerFactory.getLogger(HarriServiceExecutor.class);
	private ServiceId serviceId;
	private Service service;
	private UpnpService upnpService;

	public HarriServiceExecutor(UpnpService inUpnpService, Device device, String inServiceName) {
		upnpService = inUpnpService;
		serviceId = new UDAServiceId(inServiceName);
		service = device.findService(serviceId);
		if (serviceId != null
				&& service == null) {
			String errMsg = "HARRI service named " + inServiceName
					+ " not found on HARRI device " + device.getDetails().getModelDetails().getModelName();
			LOG.error(errMsg);
			throw new RuntimeException(errMsg);
		}
	}

	public ActionInvocation prepareActionInvocation(final String actionName, final Map<String, String> params) {
		return new HarriActionInvocation(this.service, actionName, params);
	}
}
