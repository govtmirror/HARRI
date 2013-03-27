package gov.usgs.cida.harri.util;


import java.io.IOException;
import java.util.Properties;
import org.junit.Test;

import static org.junit.Assert.*;

public class HarriUtilTest {

	@Test
	public void testGetProxy() throws IOException {
		String testConfig = "src/test/resources/harri-config.properties";
		System.setProperty("harri.config.file", testConfig);
		
        Properties config = HarriUtils.getHarriConfigs();
        
        assertEquals(config.getProperty("vco.user"), "harri");
        assertEquals(config.getProperty("vco.password"), "password");
        assertEquals(config.getProperty("vco.url"), "https://vco-server.er.usgs.gov");

        assertEquals(config.getProperty("apache.httpd.conf.dir"), "/etc/opt/httpd/conf");
        

        assertEquals(config.getProperty("refresh.rate.ms"), "30000");
        
        assertEquals(config.getProperty("couchdb.user"), "harri");
        assertEquals(config.getProperty("couchdb.password"), "password");
        assertEquals(config.getProperty("couchdb.url"), "http://localhost:5984");
    }
}
