package gov.usgs.cida.harri.service.instance;

import gov.usgs.cida.harri.service.discovery.ProcessMD;
import java.util.Map;

/**
 *
 * @author isuftin
 */
public abstract class Instance {

    ProcessMD md;

    public ProcessMD getMD() {
        return this.md;
    }
    
    abstract public void populate();
    
    abstract void getRemoteInfo();

    abstract public Integer getHttpPort();

    abstract public Integer getHttpsPort();
    
    abstract public Map<String, ApplicationInfo> getApplicationMap();
}
