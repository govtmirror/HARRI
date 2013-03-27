/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.harri.httpd;

import gov.usgs.cida.harri.commons.datamodel.ProxyMapping;
import gov.usgs.cida.harri.commons.datamodel.ProxyMapping.Builder;
import gov.usgs.cida.harri.util.HarriUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author tkunicki
 */
public class ParseHTTPdConf {
    
    public final static Pattern PATTERN_proxyBalancerOpen = Pattern.compile("\\s*<Proxy\\s+(balancer://[^\\s]+)\\s*>\\s*");
    public final static Pattern PATTERN_proxyBalancerClose = Pattern.compile("\\s*</Proxy>\\s*");
    public final static Pattern PATTERN_proxyBalancerMember = Pattern.compile("\\s*BalancerMember\\s+([^\\s]+).*");
    public final static Pattern PATTERN_rewriteRuleToBalancer = Pattern.compile("RewriteRule\\s+\\^([^\\s]+)\\$\\s+(((?:balancer)|(?:https?))://[^\\s]+)\\s+.*");

    public static List<ProxyToBalancer> getProxyToBalancerList(File file) throws IOException {
        List<ProxyToBalancer> proxyBalancerList = new ArrayList<ProxyToBalancer>();
        
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                proxyBalancerList.addAll(getProxyToBalancerList(child));
            }
        } else {
            System.out.println("parsing " + file.getName());
            BufferedReader reader = null;
            try {
                ProxyToBalancer.Builder currentBuilder = null;

                reader = new BufferedReader(new FileReader(file));

                String line;
                while ( (line = reader.readLine()) != null) {
                    if (currentBuilder  != null) {
                        Matcher closeMatcher = PATTERN_proxyBalancerClose.matcher(line);
                        if (closeMatcher.matches()) {
                            proxyBalancerList.add(currentBuilder.build());
                            currentBuilder = null;
                        } else {
                            Matcher memberMatcher = PATTERN_proxyBalancerMember.matcher(line);
                            if (memberMatcher.matches()) {
                                currentBuilder.addMember(memberMatcher.group(1));
                            }
                        }
                    } else {
                        Matcher openMatcher = PATTERN_proxyBalancerOpen.matcher(line);
                        if (openMatcher.matches()) {
                            currentBuilder = new ProxyToBalancer.Builder(openMatcher.group(1));
                        }
                    }
                }

            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignore) { } 
                }
            }
        }
        return proxyBalancerList;
    }
    
    public static List<RewriteRuleToURL> getRewriteRuleToURL(File file) throws IOException {
        List<RewriteRuleToURL> rewriteToURLList = new ArrayList<RewriteRuleToURL>();
        
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                rewriteToURLList.addAll(getRewriteRuleToURL(child));
            }
        } else {
            String fileName = file.getName();
            String fromHost = fileName.replace(".conf$", "");
            BufferedReader reader = null;
            try {

                reader = new BufferedReader(new FileReader(file));

                String line;
                while ( (line = reader.readLine()) != null) {
                    Matcher matcher = PATTERN_rewriteRuleToBalancer.matcher(line);
                    if (matcher.matches()) {
                        rewriteToURLList.add(new RewriteRuleToURL.Builder(fromHost, matcher.group(1), matcher.group(2)).build());
                    }
                }

            } finally {
                if (reader != null) {
                    try { reader.close(); } catch (IOException ignore) { } 
                }
            }
        }
        return rewriteToURLList;
    }
    
    public static List<ProxyMapping> getProxyMappingList(File file) throws IOException {
        List<ProxyToBalancer> proxyToBalancerList = getProxyToBalancerList(file);
        List<RewriteRuleToURL> rewriteRuleToURLList = getRewriteRuleToURL(file);
        
        List<ProxyMapping> proxyMappingList = new ArrayList<ProxyMapping>();
        
        // 0(n^2) ouch...
        for (RewriteRuleToURL rewrite : rewriteRuleToURLList) {
            Builder builder = new Builder(rewrite.getFromHost(), rewrite.getFromPath());
            String mapToURL = rewrite.getToURL();
            if (mapToURL.startsWith("balancer")) {
                ProxyToBalancer balancer = null;
                Iterator<ProxyToBalancer> balancerIterator = proxyToBalancerList.iterator();
                while (balancerIterator.hasNext() && balancer == null) {
                    ProxyToBalancer next = balancerIterator.next();
                    if (mapToURL.startsWith(next.getBalancerURL() + "/")) {
                        balancer = next;
                    }
                }
                if (balancer != null) {
                    String balancerURL = balancer.getBalancerURL();
                    for (String memberURL : balancer.getMemberURLList()) {
                        builder.addToURL(mapToURL.replaceFirst(balancerURL, memberURL));
                    }
                } else {
                    
                }
            } else {
                builder.addToURL(mapToURL);
            }
            proxyMappingList.add(builder.build());
        }
        
        return proxyMappingList;
    }
    
    public static List<ProxyMapping> getProxyMappingList() throws IOException {
        List<ProxyMapping> proxyMappingList = new ArrayList<ProxyMapping>();
        Properties harriConfigProps = HarriUtils.getHarriConfigs();
        String directoryPath = harriConfigProps.getProperty("apache.httpd.conf.dir", "/etc/opt/httpd/conf");
        File directory = new File(directoryPath);
        if (directory.exists()) {
            proxyMappingList.addAll(getProxyMappingList(directory));
        }
        return proxyMappingList;
    }
}
