package gov.usgs.cida.harri.util;

import org.teleal.cling.model.meta.Device;

public class HarriUtils {	
	
	public static final String DEVICE_TYPE = "HARRI_Device";

	public static final String DEVICE_MANUFACTURER = "CIDA";
	
	public static boolean isHarriDevice(final Device device) {
		return device.getDetails().getManufacturerDetails()!=null &&
				device.getDetails().getManufacturerDetails().getManufacturer().equals(DEVICE_MANUFACTURER) && 
				device.getDetails().getModelDetails().getModelName().contains(DEVICE_TYPE);
	}
}
