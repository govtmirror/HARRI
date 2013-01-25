package gov.usgs.cida.harri.service.vmware;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VMWareService {
	static Logger LOG = LoggerFactory.getLogger(VMWareService.class);
	
	public static void getVirtualMachines(final String vmwareVcoUrl, final String vmwareVcoUserName, final String vmwareVcoPassword) {
		LOG.info("Retrieving list of VMs from " + vmwareVcoUrl);
		List<String> result = null;
		try {
			result = VMClient.getVirtualMachines(vmwareVcoUrl, vmwareVcoUserName, vmwareVcoPassword);
		} catch (Exception e1) {
			LOG.info("Error calling VMWare VCO:" + e1.getMessage());
		}
		if(result != null) {
	        for (String s: result){
	        	LOG.info(s);
	        }
		}
	}
}
