package gov.usgs.cida.harri.couchdbdao;

import gov.usgs.cida.harri.commons.datamodel.HarriBean;
import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.util.HarriUtils;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author isuftin
 */
public class HarriCouchDBDao implements IHarriDAO {

	public static final String[] BASE_DB_NAMES = new String[] { "harri" };

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
		
		initialize();

		if(!CouchRequestUtil.isServerAvailable(this.url)) {
			throw new RuntimeException("Couch DB instance is not responding");
		}
	}
	
	private void setAuthCookie() {
		if(authSessionId != null) {
			return;
		}
		
		authSessionId = CouchRequestUtil.doLogin(this.username, this.password, this.url);
	}
	
	@Override
	public void initialize() {
		setAuthCookie();
		for (int i = 0; i < BASE_DB_NAMES.length; i++) {
			try {
				CouchRequestUtil.checkAndCreateDB(this.url, BASE_DB_NAMES[i], this.authSessionId);
			} catch (Exception e) {
				LOG.error("Could not create CouchDB Database \"" + BASE_DB_NAMES[i] + "\": " + e.getMessage());
			}
		}
	}

	@Override
	public HarriBean create(HarriBean o) {
		CouchRequestUtil.doCreate(o.serialize(), this.url, constructCouchIdentifier(o), authSessionId);
		return this.read(o);
	}

	@Override
	public HarriBean read(HarriBean o) {
		String json = CouchRequestUtil.readJsonDocument(this.url, constructCouchIdentifier(o), authSessionId);
		if(json==null) {
			return null;
		}
		return o.deserialize(json);
	}

	@Override
	public HarriBean update(HarriBean o) {
		CouchRequestUtil.doUpdate(o.serialize(), this.url, constructCouchIdentifier(o), authSessionId);
		return this.read(o);
	}

	@Override
	public void delete(HarriBean o) {
		o.deserialize(CouchRequestUtil.doCouchDelete(this.url, constructCouchIdentifier(o), authSessionId));;
	}
	
	private String constructCouchIdentifier(HarriBean o) {
		return "/" + BASE_DB_NAMES[0] + "/" + o.getType() + "-" + o.getIdentifier(); //TODO expand if more than 1 DB supported
	}
}
