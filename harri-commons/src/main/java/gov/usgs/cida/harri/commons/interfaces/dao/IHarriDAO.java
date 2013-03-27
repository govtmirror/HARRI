package gov.usgs.cida.harri.commons.interfaces.dao;

import java.util.List;

/**
 *
 * @author isuftin
 */
public interface IHarriDAO {
	public void persistVmList(String managerId, List<String> data);
	
	public void updateObject(Object o);
}
