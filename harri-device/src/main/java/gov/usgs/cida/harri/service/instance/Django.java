package gov.usgs.cida.harri.service.instance;

import gov.usgs.cida.harri.service.discovery.ProcessMD;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thongsav
 */
public class Django extends Instance {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Django.class);

    public static org.slf4j.Logger getLOG() {
        return LOG;
    }
    
    private Integer httpPort = 0;
    private Integer httpsPort = 0;

    public Django(ProcessMD md) {
        this.md = md;
    }

    @Override
    public void populate() {
        if (getHttpPort() != 0) {
            getRemoteInfo();
        }
    }


    @Override
    void getRemoteInfo() {
        
    }

    @Override
    public Integer getHttpPort() {
        return httpPort;
    }

    @Override
    public Integer getHttpsPort() {
        return httpsPort;
    }
}
