package gov.usgs.cida.harri.commons.interfaces.dao;

import java.util.List;

/**
 *
 * @author isuftin
 */
public interface IHarriDAO {
	
	public boolean isAvailable();
	public void writeList(String identifier, List<String> data);
}
