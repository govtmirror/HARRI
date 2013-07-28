package gov.usgs.cida.harri.commons.datamodel;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author isuftin
 */
public class Vco extends HarriBean {
	private static final long serialVersionUID = 34234L;

	private List<VirtualMachine> vmHosts;
	private String timestamp;

	public List<VirtualMachine> getVmHosts() {
		return Collections.unmodifiableList(vmHosts);
	}

	public void setVmHosts(List<VirtualMachine> vmHosts) {
		this.vmHosts = vmHosts;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
