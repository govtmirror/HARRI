package gov.usgs.cida.harri.commons.interfaces.manager;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import org.teleal.cling.UpnpService;
import org.teleal.cling.model.meta.RemoteDevice;

public interface IHarriManagerServiceProvider {
	/**
	 *
	 * @param harriManagerUpnpService
	 * @param d
	 * @param dao  
	 */
	public void doServiceCalls(UpnpService harriManagerUpnpService, RemoteDevice d, IHarriDAO dao);
}
