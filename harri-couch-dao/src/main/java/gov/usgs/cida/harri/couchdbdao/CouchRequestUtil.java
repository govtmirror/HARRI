package gov.usgs.cida.harri.couchdbdao;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class CouchRequestUtil {
	private static Logger LOG = LoggerFactory.getLogger(CouchRequestUtil.class);
	
	public static boolean isServerAvailable(String couchUrl) {
		RestTemplate rt = new RestTemplate();
		String response = rt.getForObject(couchUrl, String.class);
		if (response != null && !"".equals(response)) {
			return true;
		}
		return false;
	}
	
	public static String readJsonDocument(String couchUrl, String uri, String authCookie) {
		RestTemplate rt = new RestTemplate();
		String response = null;
		try {
			response = rt.getForObject(couchUrl + uri, String.class);
		} catch (HttpClientErrorException e) {
			if(!e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw e;
			}
		}
		return response;
	}
	
	public static void checkAndCreateDB(String couchUrl, String uri, String authCookie) {
		RestTemplate rest = new RestTemplate();
		try {
			rest.getForObject(couchUrl + uri, String.class);
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				doJsonPut("", couchUrl, uri, authCookie);
			}
		}
	}
	
	public static String doUpdate(String json, String couchUrl, String uri, String authCookie) {
		String newJson = updateJsonWithRevisionInfo(json, couchUrl, uri);
		return doJsonPut(newJson, couchUrl, uri, authCookie);
	}
	
	public static String doCreate(String json, String couchUrl, String uri, String authCookie) {
		return doJsonPut(json, couchUrl, uri, authCookie);
	}

	public static String doJsonPut(String json, String couchUrl, String uri, String authCookie) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		if ( authCookie != null ) {
			requestHeaders.add("Cookie", authCookie);
		}

		// Note the body object as first parameter!
		HttpEntity<String> entity = new HttpEntity<String>(json, requestHeaders);
		ResponseEntity<String> model = restTemplate.exchange(couchUrl + uri, HttpMethod.PUT, entity, String.class);
	
		return model.getBody();
	}
	
	public static String doCouchDelete(String couchUrl, String uri, String authCookie) {
		RestTemplate restTemplate = new RestTemplate();

		String s = restTemplate.getForObject(couchUrl + uri, String.class);
		CouchDocumentId id = (new Gson()).fromJson(s, CouchDocumentId.class);
		
		HttpHeaders requestHeaders = new HttpHeaders();
		if ( authCookie != null ) {
			requestHeaders.add("Cookie", authCookie);
		}
		
		// Note the body object as first parameter!
		HttpEntity<String> entity = new HttpEntity<String>(null, requestHeaders);
		ResponseEntity<String> model = restTemplate.exchange(couchUrl + uri + "?rev=" + id.get_rev(), HttpMethod.DELETE, entity, String.class);
	
		return model.getBody();
	}

	public static String updateJsonWithRevisionInfo(String json, String couchUrl, String uri) {
		String newJson = json;

		RestTemplate rest = new RestTemplate();
		String s = rest.getForObject(couchUrl + uri, String.class);
		CouchDocumentId id = (new Gson()).fromJson(s, CouchDocumentId.class);

		return "{" + 
				"\"_id\" : \"" + id.get_id() + 
				"\", \"_rev\" : \"" + id.get_rev() + "\", " + 
				newJson.substring(newJson.indexOf("{")+1);
	}

	public static String doLogin(String username, String password, String couchUrl) {
		LOG.info("Logging into CouchDB instance: " + username + "@" + couchUrl);
		RestTemplate restTemplate = new RestTemplate();

		// Create the request body as a MultiValueMap
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     

		HttpHeaders requestHeaders = new HttpHeaders();

		body.add("name", username);
		body.add("password", password);

		// Note the body object as first parameter!
		HttpEntity<Object> entity = new HttpEntity<Object>(body, requestHeaders);

		ResponseEntity<String> model = restTemplate.exchange(couchUrl + "/_session", HttpMethod.POST, entity, String.class);

		String cookie = null;
		
		String rawCookie = model.getHeaders().get("Set-Cookie").get(0);
		rawCookie = rawCookie.replace("[", "").replace("]", ""); 
		for(String s : rawCookie.split(";")) {
			if(s.startsWith("AuthSession=")) {
				cookie = s;
			}
		}

		LOG.info("Logged in, and session cookie stored.");
		return cookie;
	}
}
