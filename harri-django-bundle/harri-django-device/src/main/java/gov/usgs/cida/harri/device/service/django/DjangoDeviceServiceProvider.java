package gov.usgs.cida.harri.device.service.django;

import gov.usgs.cida.harri.commons.datamodel.Django;
import gov.usgs.cida.harri.commons.datamodel.ProcessMD;
import gov.usgs.cida.harri.commons.datamodel.ProcessType;
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
		@UpnpServiceId("DjangoDiscoveryService"),
		serviceType =
		@UpnpServiceType(value = "DjangoDiscoveryService", version = 1))
public class DjangoDeviceServiceProvider implements IHarriDeviceServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(DjangoDeviceServiceProvider.class);
	@UpnpStateVariable(defaultValue = "no_id_provided")
	private String harriManagerId = "no_id_provided";
	@UpnpStateVariable(defaultValue = "")
	private String getAllDjangoInstancesResponse = "";
	@UpnpStateVariable(defaultValue = "")
	private String getAllDjangoAppsResponse = "";

	@UpnpAction(out =
			@UpnpOutputArgument(name = "GetAllDjangoInstancesResponse"))
	public String getAllDjangoInstances(@UpnpInputArgument(name = "HarriManagerId") String harriManagerId) {
		this.getAllDjangoInstancesResponse = "";
		this.harriManagerId = harriManagerId;
		LOG.info("GetAllDjangoInstances action called by HARRI Manager with ID: " + this.harriManagerId);

		List<ProcessMD> ps;
		try {
			ps = ProcessDiscovery.getProcesses();
		} catch (IOException e) {
			return this.getAllDjangoInstancesResponse;
		}

		for (ProcessMD p : ps) {
			if (p.getType().equals(ProcessType.DJANGO)) {
				Django dj = (Django) p.createInstance();
				dj.populate();
				this.getAllDjangoInstancesResponse += p.getPid() + ":" + p.getName() + "\n";
			}
		}

		return this.getAllDjangoInstancesResponse;
	}

	@UpnpAction(out =
			@UpnpOutputArgument(name = "GetAllDjangoAppsResponse"))
	public String getAllDjangoApps(@UpnpInputArgument(name = "HarriManagerId") String harriManagerId) {
		this.getAllDjangoAppsResponse = "";
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
					this.getAllDjangoAppsResponse += app + "\n";
				}
			}
		}
		return this.getAllDjangoAppsResponse;
	}
}