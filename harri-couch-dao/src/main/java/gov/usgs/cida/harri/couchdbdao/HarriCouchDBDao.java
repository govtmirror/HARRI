package gov.usgs.cida.harri.couchdbdao;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.util.HarriUtils;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author isuftin
 */
public class HarriCouchDBDao implements IHarriDAO {
	private static Logger LOG = LoggerFactory.getLogger(HarriCouchDBDao.class);

	private String username = "";
	private String password = "";
	private String url = "";
	
	public HarriCouchDBDao() {
		LOG.info("HarriCouchDBDao constructor");
		Properties harriConfigs = HarriUtils.getHarriConfigs();
		username = harriConfigs.getProperty("couchdb.user", "harri");
		password = harriConfigs.getProperty("couchdb.password", "set.password.here");
		url = harriConfigs.getProperty("couchdb.url", "https://localhost:6984");
		boolean checkCouchStatus = checkCouchStatus();
	}
	
	public boolean checkCouchStatus() {
		RestTemplate rt = new RestTemplate();
		String response = rt.getForObject(this.url, String.class);
		return true;
	}
}
