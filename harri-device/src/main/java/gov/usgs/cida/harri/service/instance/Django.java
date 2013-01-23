package gov.usgs.cida.harri.service.instance;

import gov.usgs.cida.harri.service.discovery.ProcessMD;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thongsav
 */
public class Django extends Instance {
	private static String WEBAPPS_DIR = "webapps"; 

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(Django.class);

    public static org.slf4j.Logger getLOG() {
        return LOG;
    }
    
    private Integer httpPort = 0;
    private Integer httpsPort = 0;
    private ArrayList<String> appList;

    public Django(ProcessMD md) {
        this.md = md;
    }
    
    private String getWebAppsDirectory() {
		String webAppsDir = FileUtils.getUserDirectory().getAbsolutePath();
		if(webAppsDir == null) {
			webAppsDir = WEBAPPS_DIR;
		} else {
			if(!webAppsDir.endsWith("/")) {
				webAppsDir += "/";
			}
			webAppsDir += WEBAPPS_DIR;
		}
		
		return webAppsDir;
	}

    @Override
    public void populate() {
    	appList = new ArrayList<String>();
    	
    	Collection<File> dirs = FileUtils.listFilesAndDirs(new File(getWebAppsDirectory()), null, null); 
    	for(File f : dirs) {
    		if(f.isDirectory()) {
    			appList.add(f.getName());
    		}
    	}
    }
    
    @Override
    public List<String> getAppList() {
    	return appList;
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

    @Override
    public Map<String, ApplicationInfo> getApplicationMap() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
