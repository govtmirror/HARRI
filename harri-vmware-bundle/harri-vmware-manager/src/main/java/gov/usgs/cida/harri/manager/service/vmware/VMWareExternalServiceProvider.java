package gov.usgs.cida.harri.manager.service.vmware;

import gov.usgs.cida.harri.commons.datamodel.Vco;
import gov.usgs.cida.harri.commons.interfaces.dao.IHarriDAO;
import gov.usgs.cida.harri.commons.interfaces.manager.IHarriExternalServiceProvider;
import gov.usgs.cida.harri.util.HarriUtils;
import java.text.DateFormat;
import java.util.Date;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VMWareExternalServiceProvider implements IHarriExternalServiceProvider {

	private static Logger LOG = LoggerFactory.getLogger(VMWareExternalServiceProvider.class);
	
	public static final String VMS_ID = "hosts";
	
	private static String vmwareVcoUrl;
	private static String vmwareVcoUserName;
	private static String vmwareVcoPassword;
	private static Properties harriConfigProps;

	public VMWareExternalServiceProvider() {
		LOG.debug("Constructor called for VMWareManagerServiceProvider");

		harriConfigProps = HarriUtils.getHarriConfigs();

		vmwareVcoUrl = harriConfigProps.getProperty("vco.url", "https://a-vco-server.er.usgs.gov");
		vmwareVcoUserName = harriConfigProps.getProperty("vco.user", "harri");
		vmwareVcoPassword = harriConfigProps.getProperty("vco.password", "setValidPassword");
	}

	public static List<String> getVirtualMachines(final String vmwareVcoUrl, final String vmwareVcoUserName, final String vmwareVcoPassword) {
		LOG.debug("Retrieving list of VMs from " + vmwareVcoUrl);
		List<String> result = null;
		try {
			result = VMClient.getVirtualMachines(vmwareVcoUrl, vmwareVcoUserName, vmwareVcoPassword);
		} catch (Exception e1) {
			LOG.error("Error calling VMWare VCO:" + e1.getMessage());
		}
		return result;
	}

	@Override
	public void doServiceCalls(String managerId, IHarriDAO dao) {
		List<String> results = getVirtualMachines(vmwareVcoUrl, vmwareVcoUserName, vmwareVcoPassword);

		//create HarriBean
		Vco vmBean = new Vco();
		vmBean.setIdentifier(VMS_ID);
		vmBean.setReportingManager(managerId);
		vmBean.setTimestamp(DateFormat.getDateInstance().format(new Date()));
		vmBean.setVmHosts(results);
		
		if(dao.read(vmBean) == null) {
			dao.create(vmBean);
		} else {
			dao.update(vmBean);
		}
	}
}
