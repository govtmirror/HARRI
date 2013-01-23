package gov.usgs.cida.harri.service.instance;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import gov.usgs.cida.harri.service.discovery.ProcessMD;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author isuftin
 */
public class Tomcat extends Instance {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Tomcat.class);

    public static org.slf4j.Logger getLOG() {
        return LOG;
    }
    private String managerUsername = "";
    private String managerPassword = "";
    private Integer httpPort = 0;
    private Integer httpsPort = 0;

    public Tomcat(ProcessMD md) {
        this.md = md;
    }

    @Override
    public void populate() {
        String catalinaHomeLocation = this.md.getStartupOptions().get("catalina.home");
        File catalinaHome = new File(catalinaHomeLocation);
        File usersXML = FileUtils.getFile(catalinaHome, "conf", "tomcat-users.xml");
        File serverXML = FileUtils.getFile(catalinaHome, "conf", "server.xml");

        if (!catalinaHome.exists()) {
            getLOG().warn("The directory specified by catalina.home (" + catalinaHomeLocation + ") does not exist");
            return;
        } else if (!catalinaHome.canRead()) {
            getLOG().warn("The directory specified by catalina.home (" + catalinaHomeLocation + ") cannot be read");
            return;
        }

        if (!usersXML.exists()) {
            getLOG().warn("The catalina users file (" + usersXML.getPath() + ") does not exist");
        } else if (!catalinaHome.canRead()) {
            getLOG().warn("The catalina users file (" + usersXML.getPath() + ") cannot be read");
        } else {
            try {
                JXPathContext ctx = JXPathContext.newContext(parseXMLFile(usersXML).getDocumentElement());

                // Find the user that contains the manager role
                Node userNode = (Node) ctx.selectSingleNode("/user[contains(@roles, manager)]");
                this.managerUsername = userNode.getAttributes().getNamedItem("username").getTextContent();
                this.managerPassword = userNode.getAttributes().getNamedItem("password").getTextContent();
            } catch (SAXException ex) {
                getLOG().warn(ex.getMessage());
            } catch (IOException ex) {
                getLOG().warn(ex.getMessage());
            } catch (ParserConfigurationException ex) {
                getLOG().warn(ex.getMessage());
            }
        }

        if (!serverXML.exists()) {
            getLOG().warn("The catalina server config file (" + serverXML.getPath() + ") does not exist");
        } else if (!catalinaHome.canRead()) {
            getLOG().warn("The catalina users file (" + serverXML.getPath() + ") cannot be read");
        } else {
            try {
                JXPathContext ctx = JXPathContext.newContext(parseXMLFile(serverXML).getDocumentElement());
                Node httpPortNode = (Node) ctx.selectSingleNode("/Service[@name='Catalina']/Connector[@protocol='HTTP/1.1' and not(@SSLEnabled='true')]");
                Node httpsPortNode = (Node) ctx.selectSingleNode("/Service[@name='Catalina']/Connector[@protocol='HTTP/1.1' and @SSLEnabled='true']");

                if (httpPortNode != null) {
                    this.httpPort = Integer.parseInt(httpPortNode.getAttributes().getNamedItem("port").getTextContent());
                }
                if (httpsPortNode != null) {
                    this.httpsPort = Integer.parseInt(httpsPortNode.getAttributes().getNamedItem("port").getTextContent());
                }
            } catch (NumberFormatException ex) {
                getLOG().warn(ex.getMessage());
            } catch (SAXException ex) {
                getLOG().warn(ex.getMessage());
            } catch (IOException ex) {
                getLOG().warn(ex.getMessage());
            } catch (ParserConfigurationException ex) {
                getLOG().warn(ex.getMessage());
            }
        }

        if (getHttpPort() != 0) {
            getRemoteInfo();
        }
    }

    private Document parseXMLFile(File xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xml);
        doc.getDocumentElement().normalize();
        return doc;
    }

    @Override
    void getRemoteInfo() {
//        DefaultHttpClient client = new DefaultHttpClient();
//        HttpHost host = new HttpHost("localhost", this.httpPort, "http");
//        client.getCredentialsProvider().setCredentials(
//                new AuthScope(host),
//                new UsernamePasswordCredentials(this.managerUsername, this.managerPassword));
//        try {
//            HttpURLConnection get = new HttpURLConnection(new URL("http://127.0.0.1:"+this.getHttpPort()+"/manager/status/all").toURI());
//            
//            AuthCache authCache = new BasicAuthCache();
//            BasicScheme basicAuth = new BasicScheme();
//            BasicHttpContext context = new BasicHttpContext();
//            authCache.put(host, basicAuth);
//            context.setAttribute(ClientContext.AUTH_CACHE, authCache);
//            
//            HttpResponse response = client.execute(get);
//            HttpEntity entity = response.getEntity();
////            InputStream content = entity.getContent();
//            String test = EntityUtils.toString(entity);
//            String a = "";
//        } catch (IOException ex) {
//            Logger.getLogger(Tomcat.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(Tomcat.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            client.getConnectionManager().shutdown();
//        }
        
        
        
        
        try {
            URL url = new URL ("http://127.0.0.1:"+this.getHttpPort()+"/manager/status/all");
            String encoding = Base64.encode((this.managerUsername +":" + this.managerPassword).getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty  ("Authorization", "Basic " + encoding);
            InputStream content = (InputStream)connection.getInputStream();
            List<String> readLines = IOUtils.readLines(content);
            String a = "";
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    public String getManagerUsername() {
        return managerUsername;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    @Override
    public Integer getHttpPort() {
        return httpPort;
    }

    @Override
    public Integer getHttpsPort() {
        return httpsPort;
    }
}
