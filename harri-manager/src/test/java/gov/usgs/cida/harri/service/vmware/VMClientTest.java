/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cida.harri.service.vmware;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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
     */
    @Test
    public void testGetVirtualMachines() throws Exception {
        System.out.println("getVirtualMachines");
        List<String> result = VMClient.getVirtualMachines("https://cida-eros-vco.er.usgs.gov/sdk/vimService", "harri", "xxxxxxxxxxx");//TODO put in real password for demo
        for (String s: result){
            System.out.println(s);
        }
    }
}
