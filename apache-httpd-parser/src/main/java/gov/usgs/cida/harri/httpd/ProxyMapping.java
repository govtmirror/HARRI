/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.harri.httpd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author tkunicki
 */
public class ProxyMapping {
    
        private final String fromPath;
    private final List<String> toURLList;
    
    private ProxyMapping(String fromPath, List<String> toURLList) {
        this.fromPath = fromPath;
        this.toURLList = Collections.unmodifiableList(toURLList);
    }
    
    public String getFromPath() {
        return fromPath;
    }
    
    public List<String> getToURLList() {
        return toURLList;
    }
    
    public static class Builder {
        
        private final String fromPath;
        private final List<String> toURLList;
        
        public Builder(String fromPath) {
            this.fromPath = fromPath;
            this.toURLList = new ArrayList<String>();
        }
        
        public Builder addToURL(String url) {
            this.toURLList.add(url);
            return this;
        }
        
        public ProxyMapping build() {
            return new ProxyMapping(fromPath, toURLList);
        }
    } 
    
}
