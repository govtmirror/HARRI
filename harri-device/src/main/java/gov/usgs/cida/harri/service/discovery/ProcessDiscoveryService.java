package gov.usgs.cida.harri.service.discovery;

import gov.usgs.cida.harri.service.ProcessDiscovery;
import gov.usgs.cida.harri.commons.datamodel.ProcessMD;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.binding.annotations.*;

@UpnpService(
        serviceId = @UpnpServiceId("ProcessDiscoveryService"),
        serviceType = @UpnpServiceType(value = "ProcessDiscoveryService", version = 1)
)

/**
 * @author thongsav
 */
public class ProcessDiscoveryService {
    private static Logger LOG = LoggerFactory.getLogger(ProcessDiscoveryService.class);
	
    @UpnpStateVariable(defaultValue = "no_id_provided")
    private String harriManagerId = "no_id_provided";
    
    @UpnpStateVariable(defaultValue = "")
    private String getAllProcessesResponse = "";
	
    @UpnpAction(out = @UpnpOutputArgument(name = "GetAllProcessesResponse"))
    public String getAllProcesses(@UpnpInputArgument(name = "HarriManagerId")
                          String harriManagerId) {
    	getAllProcessesResponse = "";
    	
    	this.harriManagerId = harriManagerId;
        LOG.info("GetAllProcesses action called by HARRI Manager with ID: " + this.harriManagerId);
        
        List<ProcessMD> ps;
        try {
			ps = ProcessDiscovery.getProcesses();
		} catch (IOException e) {
			return getAllProcessesResponse;
		}
        
        for(ProcessMD p : ps) {
        	getAllProcessesResponse += p.getType() + ":" + p.getPid() + "\n";
        }
        
        return getAllProcessesResponse;
    }
}