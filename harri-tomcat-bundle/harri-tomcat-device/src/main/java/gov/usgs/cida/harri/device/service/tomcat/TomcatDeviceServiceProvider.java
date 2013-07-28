package gov.usgs.cida.harri.device.service.tomcat;

import gov.usgs.cida.harri.commons.datamodel.ProcessMD;
import gov.usgs.cida.harri.commons.datamodel.ProcessType;
import gov.usgs.cida.harri.commons.datamodel.Tomcat;
import gov.usgs.cida.harri.commons.datamodel.TomcatHost;
import gov.usgs.cida.harri.commons.interfaces.device.IHarriDeviceServiceProvider;
import gov.usgs.cida.harri.service.ProcessDiscovery;
import gov.usgs.cida.harri.util.HarriUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

import com.google.gson.Gson;

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
	private String getTomcatHostResponse = "";

	@UpnpAction(out =
			@UpnpOutputArgument(name = "GetTomcatHostResponse"))
	public String getTomcatHost(@UpnpInputArgument(name = "HarriManagerId") String harriManagerId) {
		LOG.debug("GetTomcatHost action called by HARRI Manager with ID: " + this.harriManagerId);
		
		TomcatHost tomcatHost = new TomcatHost();
		
		getTomcatHostResponse = "";
		
		this.harriManagerId = harriManagerId;
		tomcatHost.setManagerId(this.harriManagerId);
		tomcatHost.setIdentifier(HarriUtils.getSystemHostName());

		List<ProcessMD> ps;
		try {
			ps = ProcessDiscovery.getProcesses();
		} catch (IOException e) {
			return null;
		}

		tomcatHost.setTomcatInstances(new ArrayList<Tomcat>());
		for (ProcessMD p : ps) {
			if (p.getType().equals(ProcessType.TOMCAT)) {
				Tomcat tc = (Tomcat) p.createInstance();
				tc.populate();
				tomcatHost.addTomcatInstance(tc);
			}
		}

		getTomcatHostResponse = new Gson().toJson(tomcatHost);
		return getTomcatHostResponse;
	}

}