package gov.usgs.cida.harri.couchdbdao;

import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.util.HarriUtils;

import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

	private String authSessionId;

	public HarriCouchDBDao() {
		LOG.debug("HarriCouchDBDao constructor");
		Properties harriConfigs = HarriUtils.getHarriConfigs();
		username = harriConfigs.getProperty("couchdb.user", "harri");
		password = harriConfigs.getProperty("couchdb.password", "set.password.here");
		url = harriConfigs.getProperty("couchdb.url", "https://localhost:6984");

		if(!isAvailable()) {
			throw new RuntimeException("Couch DB instance is not responding");
		}
	}

	@Override
	public boolean isAvailable() {
		RestTemplate rt = new RestTemplate();
		String response = rt.getForObject(this.url, String.class);
		if(response != null && !"".equals(response)) {
			return true;
		}
		return false;
	}

	/**
	 */
	@Override
	public void persistVmList(String managerId, List<String> data) {
		RestTemplate rest = new RestTemplate();

		checkAndCreateDB("/vco");
		
		String json = new Gson().toJson(data);
		json = "{" +
				" \"manager\" : \"" + managerId + "\", " +
				" \"data\" : " + (data==null ? "[]" : json) +
				"}";
		doJsonPut(json, "/vco/hosts");
	}

	public void checkAndCreateDB(String uri) {
		RestTemplate rest = new RestTemplate();
		try {
			rest.getForObject(this.url + uri, String.class);
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				doJsonPut("", uri);
			}
		}
	}

	public void doJsonPut(String json, String uri) {
		RestTemplate restTemplate = new RestTemplate();

		json = updateCheckForUpdate(json, uri);

		// Create the request body as a MultiValueMap
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.add("Cookie", doLogin());

		// Note the body object as first parameter!
		HttpEntity<String> entity = new HttpEntity<String>(json, requestHeaders);
		ResponseEntity<String> model = restTemplate.exchange(this.url + uri, HttpMethod.PUT, entity, String.class);
	}

	public String updateCheckForUpdate(String json, String uri) {
		try {
		RestTemplate rest = new RestTemplate();
		String s = rest.getForObject(this.url + uri, String.class);
		CouchDocumentId id = (new Gson()).fromJson(s, CouchDocumentId.class);

		return "{" + "\"_id\" : \"" + id.get_id() + 
				"\", \"_rev\" : \"" + id.get_rev() + "\", " + 
				json.substring(json.indexOf("{")+1);
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				return json;
			}
		}
		return json;
	}

	public String doLogin() {
		if(authSessionId != null) {
			return authSessionId;
		}

		RestTemplate restTemplate = new RestTemplate();

		// Create the request body as a MultiValueMap
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     

		HttpHeaders requestHeaders = new HttpHeaders();

		body.add("name", this.username);
		body.add("password", this.password);

		// Note the body object as first parameter!
		HttpEntity<Object> entity = new HttpEntity<Object>(body, requestHeaders);

		ResponseEntity<String> model = restTemplate.exchange(this.url + "/_session", HttpMethod.POST, entity, String.class);

		String rawCookie = model.getHeaders().get("Set-Cookie").get(0);
		rawCookie = rawCookie.replace("[", "").replace("]", ""); 
		for(String s : rawCookie.split(";")) {
			if(s.startsWith("AuthSession=")) {
				authSessionId = s;
			}
		}

		return authSessionId;
	}

	private class CouchDocumentId {
		private String _id;
		private String _rev;
		public String get_rev() {
			return _rev;
		}
		public void set_rev(String _rev) {
			this._rev = _rev;
		}
		public String get_id() {
			return _id;
		}
		public void set_id(String _id) {
			this._id = _id;
		}
	}
}
