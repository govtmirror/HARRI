package gov.usgs.cida.harri.device.service.tomcat;

import gov.usgs.cida.harri.commons.datamodel.ApplicationInfo;
import gov.usgs.cida.harri.commons.datamodel.ProcessMD;
import gov.usgs.cida.harri.commons.datamodel.ProcessType;
import gov.usgs.cida.harri.commons.datamodel.Tomcat;
import gov.usgs.cida.harri.commons.interfaces.device.IHarriDeviceServiceProvider;
import gov.usgs.cida.harri.service.ProcessDiscovery;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

/**
 *
 * @author isuftin
 */
@UpnpService(
		serviceId =
		@UpnpServiceId("TomcatDiscoveryService"),
		serviceType =
		@UpnpServiceType(value = "TomcatDiscoveryService", version = 1))

public class TomcatDeviceServiceProvider implements IHarriDeviceServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(TomcatDeviceServiceProvider.class);
	
	@UpnpStateVariable(defaultValue = "no_id_provided")
	private String harriManagerId = "no_id_provided";
	@UpnpStateVariable(defaultValue = "")
	private String getAllTomcatInstancesResponse = "";
	@UpnpStateVariable(defaultValue = "")
	private String getAllTomcatAppsResponse = "";

	@UpnpAction(out =
			@UpnpOutputArgument(name = "GetAllTomcatInstancesResponse"))
	public String getAllTomcatInstances(@UpnpInputArgument(name = "HarriManagerId") String harriManagerId) {
		getAllTomcatInstancesResponse = "";
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
}