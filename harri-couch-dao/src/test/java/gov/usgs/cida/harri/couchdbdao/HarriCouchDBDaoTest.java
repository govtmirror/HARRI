package gov.usgs.cida.harri.couchdbdao;

import java.util.ArrayList;

import gov.usgs.cida.harri.commons.datamodel.Vco;
import gov.usgs.cida.harri.commons.datamodel.VirtualMachine;

import org.junit.Ignore;
import org.junit.Test;

public class HarriCouchDBDaoTest {
	
	@Test
	@Ignore
	public void doCrudTest() {
		HarriCouchDBDao dao = new HarriCouchDBDao();
		String id = "teEEeeest";

		Vco vco = new Vco();
		vco.setIdentifier(id);
				
		vco = (Vco) dao.read(vco);
		if(vco != null) {
			dao.delete(vco);
		} else {
			vco = new Vco();
			vco.setIdentifier(id);
		}
		vco.setManagerId("harriManager");
		vco.setVmHosts(new ArrayList<VirtualMachine>());
		vco.setTimestamp("timestamp 1");
		dao.create(vco);
		vco.setTimestamp("timestamp 2");
		dao.update(vco);
	}
}
