package gov.usgs.cida.harri.commons.interfaces.dao;

import gov.usgs.cida.harri.commons.datamodel.HarriBean;

import java.util.List;

/**
 *
 * @author isuftin
 */
public interface IHarriDAO {
	public HarriBean create(HarriBean o);
	public HarriBean read(HarriBean o);
	public HarriBean update(HarriBean o);
	public boolean delete(HarriBean o);
}
