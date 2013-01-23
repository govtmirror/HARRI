package gov.usgs.cida.harri.service.instance;

import gov.usgs.cida.harri.service.discovery.ProcessMD;

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

    abstract public String getManagerUsername();

    abstract public String getManagerPassword();
}
