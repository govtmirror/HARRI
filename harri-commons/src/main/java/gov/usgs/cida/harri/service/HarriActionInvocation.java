package gov.usgs.cida.harri.service;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.InvalidValueException;

/**
 *
 * @author isuftin
 */
public class HarriActionInvocation extends ActionInvocation {

	Logger LOG = LoggerFactory.getLogger(HarriActionInvocation.class);

	HarriActionInvocation(Service service, String actionName, Map<String, String> params) {
		super(service.getAction(actionName));
		Action action = service.getAction(actionName);
		if (action == null) {
			String errMsg = "Action " + actionName + " not found on HARRI service named " + service.getServiceId().toString();
			LOG.error(errMsg);
			throw new RuntimeException(errMsg);
		}

		try {
			for (String k : params.keySet()) {
				setInput(k, params.get(k));
			}
		} catch (InvalidValueException ex) {
			LOG.error(ex.getMessage());
		}
	}
}
