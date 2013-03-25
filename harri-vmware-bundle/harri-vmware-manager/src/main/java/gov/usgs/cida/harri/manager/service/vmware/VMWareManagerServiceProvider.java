package gov.usgs.cida.harri.manager.service.vmware;

import gov.usgs.cida.harri.commons.interfaces.manager.IHarriExternalServiceProvider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;

public class VMWareManagerServiceProvider implements IHarriExternalServiceProvider {
	static Logger LOG = LoggerFactory.getLogger(VMWareManagerServiceProvider.class);

	private static String vmwareVcoUrl;
	private static String vmwareVcoUserName;
	private static String vmwareVcoPassword;
	
	public VMWareManagerServiceProvider() {
		LOG.debug("Constructor called for VMWareManagerServiceProvider");
		//TODO move these properties into a module
		vmwareVcoUrl = getVmwareVcoUrl();
	    vmwareVcoUserName = getVmwareVcoUserName();
	    vmwareVcoPassword = getVmwareVcoPassword();
	}

	public static void getVirtualMachines(final String vmwareVcoUrl, final String vmwareVcoUserName, final String vmwareVcoPassword) {
		LOG.debug("Retrieving list of VMs from " + vmwareVcoUrl);
		List<String> result = null;
		try {
			result = VMClient.getVirtualMachines(vmwareVcoUrl, vmwareVcoUserName, vmwareVcoPassword);
		} catch (Exception e1) {
			LOG.error("Error calling VMWare VCO:" + e1.getMessage());
		}
		if(result != null) {
			for (String s: result){
				LOG.debug(s);
			}
		}
	}

	public void doServiceCalls(UpnpService harriManagerUpnpService) {
		getVirtualMachines(vmwareVcoUrl, vmwareVcoUserName, vmwareVcoPassword);
	}
	
	private static String getVmwareVcoUrl() {
		//TODO pull from config/props file
		return "https://cida-eros-vco.er.usgs.gov/sdk/vimService";
	}

	private static String getVmwareVcoUserName() {
		//TODO pull from config/props file
		return "harri";
	}
    
	private static String getVmwareVcoPassword() {
		//TODO pull from config/props file
		return "XXXXXX";
	}
}
