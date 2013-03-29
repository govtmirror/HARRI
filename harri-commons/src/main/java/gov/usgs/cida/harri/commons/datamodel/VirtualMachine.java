package gov.usgs.cida.harri.commons.datamodel;

public class VirtualMachine {

	private String vmName;
	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hoseName) {
		this.hostName = hoseName;
	}

	private String hostName;
	
	public VirtualMachine() {
	}

}
