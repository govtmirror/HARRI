package gov.usgs.cida.harri.couchdbdao;

import gov.usgs.cida.harri.commons.datamodel.HarriBean;
import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.util.HarriUtils;

import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 *
 * @author isuftin
 */
public class HarriCouchDBDao implements IHarriDAO {

	private static Logger LOG = LoggerFactory.getLogger(HarriCouchDBDao.class);
	private String username = "";
	private String password = "";
	private String url = "";
	
	private String authSessionId = null;

	public HarriCouchDBDao() {
		LOG.debug("HarriCouchDBDao constructor");
		Properties harriConfigs = HarriUtils.getHarriConfigs();
		username = harriConfigs.getProperty("couchdb.user", "harri");
		password = harriConfigs.getProperty("couchdb.password", "set.password.here");
		url = harriConfigs.getProperty("couchdb.url", "https://localhost:6984");
		
		setAuthCookie();

		if(!CouchRequestUtil.isServerAvailable(this.url)) {
			throw new RuntimeException("Couch DB instance is not responding");
		}
	}

	/**
	 * This function will post or put a json string to couch db, the json will
	 * have two properties: data and timestamp
	 *
	 * @param identifier for the couchdao, identifier will be a relative uri
	 */
	public void persistList(String managerId, List<String> data) {
		CouchRequestUtil.checkAndCreateDB(this.url, "/vco", authSessionId);
		
		String json = new Gson().toJson(data);
		json = "{" +
				" \"manager\" : \"" + managerId + "\", " +
				" \"data\" : " + (data==null ? "[]" : json) +
				"}";
		
		CouchRequestUtil.doJsonPut(json, this.url, "/vco/hosts", authSessionId);
	}
	
	private void setAuthCookie() {
		if(authSessionId != null) {
			return;
		}
		
		authSessionId = CouchRequestUtil.doLogin(this.username, this.password, this.url);
	}

	@Override
	public HarriBean create(HarriBean o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HarriBean read(HarriBean o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HarriBean update(HarriBean o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(HarriBean o) {
		// TODO Auto-generated method stub
		return false;
	}
}
