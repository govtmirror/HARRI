/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.harri.httpd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author tkunicki
 */
public class ParseHTTPdConfTest {
    
//    @Test
    @Ignore
    public void testGetProxy() throws IOException {
        List<ProxyMapping> pml = ParseHTTPdConf.getProxyMappingList(new File("src/test/resources/sample/cida-eros-apaprod1"));
        for (ProxyMapping pm : pml) {
            for (String toURL : pm.getToURLList()) {
                System.out.println(pm.getFromHost() + pm.getFromPath() + " -> " + toURL);
            }
        }
    }
    
    @Test
    public void testProxyBalancerRegEx() {
        Matcher matcher;
        
        matcher = ParseHTTPdConf.PATTERN_proxyBalancerOpen.matcher("<Proxy balancer://test>");
        assertTrue(matcher.matches());
        assertEquals("balancer://test", matcher.group(1));
        
        matcher = ParseHTTPdConf.PATTERN_proxyBalancerMember.matcher(" BalancerMember http://130.11.165.241:8081 keepalive=on");
        assertTrue(matcher.matches());
        assertEquals("http://130.11.165.241:8081", matcher.group(1));
        
        matcher = ParseHTTPdConf.PATTERN_proxyBalancerClose.matcher("</Proxy>");
        assertTrue(matcher.matches());
    }
    
    @Test
    public void testRewriteRuleToURLRegex() {
        Matcher matcher;
        
        matcher = ParseHTTPdConf.PATTERN_rewriteRuleToBalancer.matcher("RewriteRule ^/gdp/geonetwork/(.*)$ balancer://wiwsc-gdp-geonetwork-prodssl/geonetwork/$1 [P,L]");
        assertTrue(matcher.matches());
        assertEquals("/gdp/geonetwork/(.*)", matcher.group(1));
        assertEquals("balancer://wiwsc-gdp-geonetwork-prodssl/geonetwork/$1", matcher.group(2));
        assertEquals("balancer", matcher.group(3));
        
        matcher = ParseHTTPdConf.PATTERN_rewriteRuleToBalancer.matcher("RewriteRule ^/gdp/geonetwork/(.*)$ http://wiwsc-gdp-geonetwork-prodssl/geonetwork/$1 [P,L]");
        assertTrue(matcher.matches());
        assertEquals("/gdp/geonetwork/(.*)", matcher.group(1));
        assertEquals("http://wiwsc-gdp-geonetwork-prodssl/geonetwork/$1", matcher.group(2));
        assertEquals("http", matcher.group(3));
        
        matcher = ParseHTTPdConf.PATTERN_rewriteRuleToBalancer.matcher("RewriteRule ^/gdp/geonetwork/(.*)$ https://wiwsc-gdp-geonetwork-prodssl/geonetwork/$1 [P,L]");
        assertTrue(matcher.matches());
        assertEquals("/gdp/geonetwork/(.*)", matcher.group(1));
        assertEquals("https://wiwsc-gdp-geonetwork-prodssl/geonetwork/$1", matcher.group(2));
        assertEquals("https", matcher.group(3));
    }
}
