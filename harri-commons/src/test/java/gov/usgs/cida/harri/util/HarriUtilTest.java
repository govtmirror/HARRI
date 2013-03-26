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
        String vcoUsername = config.getProperty("vcoUsername");
        String vcoPassword = config.getProperty("vcoPassword");
        String vcoUrl = config.getProperty("vcoUrl");
        
        String apacheHttpd = config.getProperty("apache.httpd.conf.dir");
        
        assertEquals(vcoUsername, "harri");
        assertEquals(vcoPassword, "password");
        assertEquals(vcoUrl, "https://vco-server.er.usgs.gov");

        assertEquals(apacheHttpd, "/etc/opt/httpd/conf");
    }
}
