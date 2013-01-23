package gov.usgs.cida.harri.service.instance;

import gov.usgs.cida.harri.service.discovery.ProcessDiscovery;
import gov.usgs.cida.harri.service.discovery.ProcessMD;
import gov.usgs.cida.harri.service.discovery.ProcessType;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

@UpnpService(
        serviceId =
@UpnpServiceId("InstanceDiscoveryService"),
serviceType =
@UpnpServiceType(value = "InstanceDiscoveryService", version = 1))
/**
 * @author thongsav
 */
public class InstanceDiscoveryService {

    private static Logger LOG = LoggerFactory.getLogger(InstanceDiscoveryService.class);
    @UpnpStateVariable(defaultValue = "no_id_provided")
    private String harriManagerId = "no_id_provided";
    @UpnpStateVariable(defaultValue = "")
    private String getAllTomcatInstancesResponse = "";
    @UpnpStateVariable(defaultValue = "")
    private String getAllDjangoInstancesResponse = "";
    @UpnpStateVariable(defaultValue = "")
    private String getAllDjangoAppsResponse = "";
    @UpnpStateVariable(defaultValue = "")
    private String getAllTomcatAppsResponse = "";
	
    @UpnpAction(out = @UpnpOutputArgument(name = "GetAllTomcatInstancesResponse"))
    public String getAllTomcatInstances(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	getAllTomcatInstancesResponse="";
    	this.harriManagerId = harriManagerId;
        LOG.info("GetAllTomcatInstances action called by HARRI Manager with ID: " + this.harriManagerId);

        List<ProcessMD> ps;
        try {
            ps = ProcessDiscovery.getProcesses();
        } catch (IOException e) {
            return getAllTomcatInstancesResponse;
        }

        for (ProcessMD p : ps) {
            if (p.getType().equals(ProcessType.TOMCAT)) {
                Tomcat tc = (Tomcat) p.createInstance();
                tc.populate();
                getAllTomcatInstancesResponse +=
                        p.getPid() + ":"
                        + p.getStartupOptions().get("catalina.home") + ":"
                        + tc.getHttpPort() + ":"
                        + tc.getManagerUsername()
                        + "\n";
            }
        }

        return getAllTomcatInstancesResponse;
    }
    @UpnpAction(out =
    @UpnpOutputArgument(name = "GetAllTomcatAppsResponse"))
    public String getAllTomcatApps(@UpnpInputArgument(name = "HarriManagerId") String harriManagerId) {
    	getAllTomcatAppsResponse = "";
        this.harriManagerId = harriManagerId;
        LOG.info("GetAllTomcatApps action called by HARRI Manager with ID: " + this.harriManagerId);

        List<ProcessMD> ps;
        try {
            ps = ProcessDiscovery.getProcesses();
        } catch (IOException e) {
            return getAllTomcatInstancesResponse;
        }

        for (ProcessMD p : ps) {
            if (p.getType().equals(ProcessType.TOMCAT)) {
                Tomcat tc = (Tomcat) p.createInstance();
                tc.populate();
                
                StringBuilder sb = new StringBuilder();
                
                for (String app : tc.getAppList()) {
                    ApplicationInfo appInfo = tc.getApplicationMap().get("/" + app);
                    sb.append("/" + app)
                            .append(" - Application is: ")
                            .append(appInfo.getRunning() ? "UP" : "DOWN")
                            .append(" - Application start time: ")
                            .append(appInfo.getStartTime())
                            .append("\n");
                }
                
                getAllTomcatAppsResponse += sb.toString();
            }
        }

        
        return getAllTomcatAppsResponse;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "GetAllDjangoInstancesResponse"))
    public String getAllDjangoInstances(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	getAllDjangoInstancesResponse = "";
    	this.harriManagerId = harriManagerId;
        LOG.info("GetAllDjangoInstances action called by HARRI Manager with ID: " + this.harriManagerId);

        List<ProcessMD> ps;
        try {
            ps = ProcessDiscovery.getProcesses();
        } catch (IOException e) {
            return getAllDjangoInstancesResponse;
        }

        for (ProcessMD p : ps) {
            if (p.getType().equals(ProcessType.DJANGO)) {
                Django dj = (Django) p.createInstance();
                dj.populate();
                getAllDjangoInstancesResponse +=
                        p.getPid() + ":"
                        + p.getName()
                        + "\n";
            }
        }

        return getAllDjangoInstancesResponse;
    }
    
    @UpnpAction(out = @UpnpOutputArgument(name = "GetAllDjangoAppsResponse"))
    public String getAllDjangoApps(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	getAllDjangoAppsResponse = "";
    	this.harriManagerId = harriManagerId;
        LOG.info("GetAllDjangoApps action called by HARRI Manager with ID: " + this.harriManagerId);

        List<ProcessMD> ps;
        try {
            ps = ProcessDiscovery.getProcesses();
        } catch (IOException e) {
            return getAllDjangoInstancesResponse;
        }

        for (ProcessMD p : ps) {
            if (p.getType().equals(ProcessType.DJANGO)) {
                Django dj = (Django) p.createInstance();

                dj.populate();
                for (String app : dj.getAppList()) {
                    getAllDjangoAppsResponse +=
                            app
                            + "\n";
                }
            }
        }
        return getAllDjangoAppsResponse;
    }
}