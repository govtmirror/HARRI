/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.harri.commons.datamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author tkunicki
 */
public class ProxyMapping {
    
    private final String fromHost;
    private final String fromPath;
    private final List<String> toURLList;
    
    private ProxyMapping(String fromHost, String fromPath, List<String> toURLList) {
        this.fromHost = fromHost;
        this.fromPath = fromPath;
        this.toURLList = Collections.unmodifiableList(toURLList);
    }
    
    public String getFromHost() {
        return fromHost;
    }
    
    public String getFromPath() {
        return fromPath;
    }
    
    public List<String> getToURLList() {
        return toURLList;
    }
    
    public static class Builder {
        
        private final String fromHost;
        private final String fromPath;
        private final List<String> toURLList;
        
        public Builder(String fromHost, String fromPath) {
            this.fromHost = fromHost;
            this.fromPath = fromPath;
            this.toURLList = new ArrayList<String>();
        }
        
        public Builder addToURL(String url) {
            this.toURLList.add(url);
            return this;
        }
        
        public ProxyMapping build() {
            return new ProxyMapping(fromHost, fromPath, toURLList);
        }
    } 
    
}
