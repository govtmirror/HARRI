package gov.usgs.cida.harri.commons.datamodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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

    	LOG.info("searching in " + webAppsDir);
		return webAppsDir;
	}

    @Override
    public void populate() {
    	appList = new ArrayList<String>();

        try {
            Runtime run = Runtime.getRuntime();
        	Process pr = run.exec(new String[]{"ls", getWebAppsDirectory()});
            pr.waitFor();
            
            for(String line : IOUtils.readLines(pr.getInputStream())){
            	String[] apps = line.split("\\s");
            	for(int i = 0; i < apps.length; i++) {
            		appList.add(apps[i]);
            	}
            }
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage());
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
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
