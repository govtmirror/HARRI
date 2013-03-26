package gov.usgs.cida.harri.couchdbdao;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAOFactory;

/**
 *
 * @author isuftin
 */
public class HarriCouchDBDaoFactory implements IHarriDAOFactory{

	@Override
	public IHarriDAO getDAO() {
		return new HarriCouchDBDao();
	}
	
}
