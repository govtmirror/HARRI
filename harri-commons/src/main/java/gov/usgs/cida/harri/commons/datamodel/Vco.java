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
	
	

	@Override
	String serialize() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	HarriBean deserialize(String input) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
