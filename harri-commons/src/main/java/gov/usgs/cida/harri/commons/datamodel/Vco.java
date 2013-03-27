package gov.usgs.cida.harri.commons.datamodel;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author isuftin
 */
public class Vco extends HarriBean {
	private static final long serialVersionUID = 1L;

	private List<String> vmHosts;
	private String reportingManager;
	private String timestamp;

	public List<String> getVmHosts() {
		return Collections.unmodifiableList(vmHosts);
	}

	public void setVmHosts(List<String> vmHosts) {
		this.vmHosts = vmHosts;
	}

	public String getReportingManager() {
		return reportingManager;
	}

	public void setReportingManager(String reportingManager) {
		this.reportingManager = reportingManager;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
