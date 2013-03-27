package gov.usgs.cida.harri.commons.datamodel;

import java.util.List;
import java.util.Map;

/**
 *
 * @author isuftin
 */
public abstract class Instance {

    ProcessMD processMetaData;

    public ProcessMD getMD() {
        return this.processMetaData;
    }

    abstract public void populate();

    abstract void getRemoteInfo();

    abstract public Integer getHttpPort();

    abstract public Integer getHttpsPort();

    abstract public Map<String, ApplicationInfo> getApplicationMap();

    abstract public List<String> getAppList();
	
	abstract public String toJSON();
	
	public static Instance fromJSON(String json) {
		throw new RuntimeException("This function must be overriden by child class");
	}
	
}
