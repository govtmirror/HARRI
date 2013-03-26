package gov.usgs.cida.harri.manager.service.vmware;

import gov.usgs.cida.harri.commons.interfaces.manager.IHarriExternalServiceProvider;
import gov.usgs.cida.harri.util.HarriUtils;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.UpnpService;

public class VMWareExternalServiceProvider implements IHarriExternalServiceProvider {
	static Logger LOG = LoggerFactory.getLogger(VMWareExternalServiceProvider.class);

	private static String vmwareVcoUrl;
	private static String vmwareVcoUserName;
	private static String vmwareVcoPassword;
	
	private static Properties harriConfigProps;
	
	public VMWareExternalServiceProvider() {
		LOG.debug("Constructor called for VMWareManagerServiceProvider");

		harriConfigProps = HarriUtils.getHarriConfigs();
		
		vmwareVcoUrl = harriConfigProps.getProperty("vcoUrl", "https://a-vco-server.er.usgs.gov");
	    vmwareVcoUserName = harriConfigProps.getProperty("vcoUsername", "harri");
	    vmwareVcoPassword = harriConfigProps.getProperty("vcoPassword", "setValidPassword");
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
}
