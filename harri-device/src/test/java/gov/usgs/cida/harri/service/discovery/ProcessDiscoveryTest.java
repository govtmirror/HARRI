package gov.usgs.cida.harri.service.discovery;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author isuftin
 */
public class ProcessDiscoveryTest {
    
    public ProcessDiscoveryTest() {
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

    @Test
    public void testGetProcessList() throws IOException {
        System.out.println("testGetProcessList");
        List<String> expResult = ProcessDiscovery.getProcessList();
        assertNotNull(expResult);
        assertFalse(expResult.isEmpty());
    }

    @Test
    public void testGetProcessList_String() throws Exception {
        System.out.println("testGetProcessList_String");
        List<String> expResult = ProcessDiscovery.getProcessList("java");
        assertNotNull(expResult);
        assertFalse(expResult.isEmpty());
    }

    @Test
    public void testGetProcessIDList() throws IOException {
        System.out.println("testGetProcessIDList");
        List<String> processList = ProcessDiscovery.getProcessList();
        List<Integer> result = ProcessDiscovery.getProcessIDList(processList);
        assertNotNull(result);
    }
    
    @Test
    public void testGetProcessIDListForJava() throws IOException {
        System.out.println("testGetProcessIDListForJava");
        List<String> processList = ProcessDiscovery.getProcessList("java");
        List<Integer> result = ProcessDiscovery.getProcessIDList(processList);
        assertNotNull(result);
    }
    
    @Test
    public void testGetProcessIDListForTomcatUsingProcessTypeEnum() throws IOException {
        System.out.println("testGetProcessIDListForJavaUsingProcessTypeEnum");
        List<Integer> result = ProcessDiscovery.getProcessIDList(ProcessType.TOMCAT);
        assertNotNull(result);
    }
    @Test
    public void testGetProcessMDList() throws IOException {
        System.out.println("testGetProcessMDListForTomcat");
        List<ProcessMD> result = ProcessDiscovery.getProcesses();
        assertNotNull(result);
    }
    
}
