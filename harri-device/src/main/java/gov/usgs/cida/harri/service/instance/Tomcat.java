package gov.usgs.cida.harri.service.instance;

import gov.usgs.cida.harri.service.discovery.ProcessMD;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.jxpath.JXPathContext;
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
                Node portNode = (Node) ctx.selectSingleNode("/Service[@name='Catalina']/Connector[@protocol='HTTP/1.1' and not(@SSLEnabled='true')]");
                this.httpPort = Integer.parseInt(portNode.getAttributes().getNamedItem("port").getTextContent());
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



        String text = "a";
    }

    private Document parseXMLFile(File xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xml);
        doc.getDocumentElement().normalize();
        return doc;
    }

    @Override
    public String getManagerUsername() {
        return managerUsername;
    }

    @Override
    public String getManagerPassword() {
        return managerPassword;
    }

    public Integer getHttpPort() {
        return httpPort;
    }
}
