package gov.usgs.cida.harri.manager.service.vmware;

import gov.usgs.cida.harri.commons.datamodel.VirtualMachine;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author mbucknel
 */
public class VMClientTest {

    public VMClientTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getVirtualMachines method, of class VMClient.
	 * @throws Exception 
	 */
    @Test
    @Ignore
    public void testGetVirtualMachines() throws Exception {
        System.out.println("getVirtualMachines");
        List<VirtualMachine> result = VMClient.getVirtualMachines("https://cida-eros-vco.er.usgs.gov/sdk/vimService", "harri", "xxxxxx");//TODO put in real password for demo
        for (VirtualMachine vm: result){
            System.out.println(vm.getVmName());
            System.out.println(vm.getHostName());
        }
    }
}
