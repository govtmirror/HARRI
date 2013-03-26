package gov.usgs.cida.harri.commons.datamodel;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author isuftin
 */
public class DjangoTest {
	
	String resultString = "{\"httpPort\":8080,\"httpsPort\":8443,\"appList\":[\"test\"]}";
	
	public DjangoTest() {
	}

	@Test
	public void testToJSON() {
		Django input = new Django();
		List<String> al = new ArrayList<String>();
		al.add("test");
	
		input.setAppList(al);
		input.setHttpPort(8080);
		input.setHttpsPort(8443);
		
		String result = input.toJSON();
		assertNotNull(result);
		assertEquals(result, resultString);
	}

	@Test
	public void testFromJSON() {
		String input = resultString;
		Django result = Django.fromJSON(resultString);
		assertNotNull(result);
		assertEquals(result.getAppList().size(), 1);
		assertEquals(result.getHttpPort().intValue(), 8080);
		assertEquals(result.getHttpsPort().intValue(), 8443);
	}
}