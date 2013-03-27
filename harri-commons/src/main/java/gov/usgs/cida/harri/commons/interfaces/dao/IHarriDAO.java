package gov.usgs.cida.harri.commons.interfaces.dao;

import gov.usgs.cida.harri.commons.datamodel.HarriBean;

/**
 *
 * @author isuftin
 */
public interface IHarriDAO {
	public void initialize();
	public HarriBean create(HarriBean o);
	public HarriBean read(HarriBean o);
	public HarriBean update(HarriBean o);
	public void delete(HarriBean o);
}
