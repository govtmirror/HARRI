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
public class ProxyToBalancer {
    
    private final String balancerURL;
    private final List<String> memberURLList;
    
    private ProxyToBalancer(String balancerURLList, List<String> memberURLList) {
        this.balancerURL = balancerURLList;
        this.memberURLList = Collections.unmodifiableList(memberURLList);
    }
    
    public String getBalancerURL() {
        return balancerURL;
    }
    
    public List<String> getMemberURLList() {
        return memberURLList;
    }
    
    public static class Builder {
        
        private final String balancerURL;
        private final List<String> memberURLList;
        
        public Builder(String balancerURL) {
            this.balancerURL = balancerURL;
            this.memberURLList = new ArrayList<String>();
        }
        
        public Builder addMember(String url) {
            this.memberURLList.add(url);
            return this;
        }
        
        public ProxyToBalancer build() {
            return new ProxyToBalancer(balancerURL, memberURLList);
        }
    } 
    
}
