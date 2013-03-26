package gov.usgs.cida.harri.commons.interfaces.manager;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import org.teleal.cling.UpnpService;

public interface IHarriExternalServiceProvider {
	public void doServiceCalls(UpnpService harriManagerUpnpService, IHarriDAO dao);
}
