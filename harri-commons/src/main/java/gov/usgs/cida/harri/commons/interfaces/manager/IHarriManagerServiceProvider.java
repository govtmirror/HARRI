package gov.usgs.cida.harri.commons.interfaces.manager;

import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public interface IHarriManagerServiceProvider {
	/**
	 *
	 * @param harriManagerUpnpService
	 * @param d
	 */
	public void doServiceCalls(UpnpService harriManagerUpnpService, RemoteDevice d);
}
