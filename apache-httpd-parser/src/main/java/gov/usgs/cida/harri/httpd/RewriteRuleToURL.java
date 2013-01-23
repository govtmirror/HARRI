/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.harri.httpd;

/**
 *
 * @author tkunicki
 */
public class RewriteRuleToURL {
    
    private final String fromHost;
    private final String fromPath;
    private final String toURL;
    
    private RewriteRuleToURL(String fromHost, String fromPath, String toURL) {
        this.fromHost = fromHost;
        this.fromPath = fromPath;
        this.toURL = toURL;
    }
    
    public String getFromHost() {
        return fromHost;
    }
    
    public String getFromPath() {
        return fromPath;
    }
    
    public String getToURL() {
        return toURL;
    }
    
    public static class Builder {
        
        private final String fromHost;
        private final String fromPath;
        private final String toURL;
        
        public Builder(String fromHost, String fromPath, String toURL) {
            this.fromHost = fromHost;
            this.fromPath = fromPath;
            this.toURL = toURL;
        }
        
        public RewriteRuleToURL build() {
            return new RewriteRuleToURL(fromHost, fromPath, toURL);
        }
    } 
    
}
