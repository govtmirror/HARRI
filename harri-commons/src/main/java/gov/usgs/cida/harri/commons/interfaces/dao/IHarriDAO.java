package gov.usgs.cida.harri.commons.interfaces.dao;

import java.util.List;

/**
 *
 * @author isuftin
 */
public interface IHarriDAO {
	
	public boolean isAvailable();
	public void persistVmList(String managerId, List<String> data);
}
