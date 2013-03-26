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
	 * This function will post or put a json string to couch db, the json will have two
	 * properties: data and timestamp
	 * 
	 * @param identifier for the couchdao, identifier will be a relative uri
	 */
	@Override
	public void writeList(String identifier, List<String> data) {
		RestTemplate rest = new RestTemplate();
		
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
	   map.add("data", new Gson().toJson(data));
	   checkAndCreateDB("/vco");
	   checkAndCreateDB("/vco/hosts");
//		rest.put(this.url + "/test/hosts", map, String.class);
	}
	
	private void checkAndCreateDB(String uri) {
		try {
			rest.getForObject(this.url + uri, String.class);
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				rest.put(this.url + uri, "test");
			}
		}
	}
	
	private void doPut() {
		RestTemplate restTemplate = new RestTemplate();
		
		// Create the request body as a MultiValueMap
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     

		HttpHeaders requestHeaders = new HttpHeaders();
		
		body.add("field", "stuff");

		// Note the body object as first parameter!
		HttpEntity<?> httpEntity = new HttpEntity<Object>(body, requestHeaders);

		ResponseEntity<String> model = restTemplate.exchange("/api/url", HttpMethod.PUT, httpEntity, String.class);
	}
}
