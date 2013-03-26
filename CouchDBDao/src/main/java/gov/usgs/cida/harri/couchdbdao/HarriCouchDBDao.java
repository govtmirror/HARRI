package gov.usgs.cida.harri.couchdbdao;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author isuftin
 */
public class HarriCouchDBDao implements IHarriDAO {
	private static Logger LOG = LoggerFactory.getLogger(HarriCouchDBDao.class);

	public HarriCouchDBDao() {
		LOG.info("HarriCouchDBDao constructor");
	}
	
	
}
