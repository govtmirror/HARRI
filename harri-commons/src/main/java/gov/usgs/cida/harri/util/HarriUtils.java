package gov.usgs.cida.harri.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.model.meta.Device;

public class HarriUtils {	
	private final static Logger LOG = LoggerFactory.getLogger(HarriUtils.class);
	
	public static final String DEVICE_TYPE = "HARRI_Device";

	public static final String DEVICE_MANUFACTURER = "CIDA";
	
	public static boolean isHarriDevice(@SuppressWarnings("rawtypes") final Device device) {
		return device.getDetails().getManufacturerDetails()!=null &&
				device.getDetails().getManufacturerDetails().getManufacturer().equals(DEVICE_MANUFACTURER) && 
				device.getDetails().getModelDetails().getModelName().contains(DEVICE_TYPE);
	}
	
    public static String getSystemHostName() {
    	String hostName = "NoHostNameFound";
    	try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOG.warn("Unable to get local host name: " + e.getMessage());
		}
    	return hostName;
    }
    
    public static Properties loadFileAsProperty(String fileName) throws FileNotFoundException, IOException {
		Properties result = new Properties();
		result.load(new FileReader(new File(fileName)));
		return result;
	}
    
    public static Properties getHarriConfigs() {
	    String configFile = System.getProperty("harri.config.file", "harri-config.properties");
	    try {
			return HarriUtils.loadFileAsProperty(configFile);
		} catch (Exception e) {
			LOG.warn("failed to load harri configuration file " + configFile + ": " + e.getMessage());
			return new Properties();
		}
	}
}
