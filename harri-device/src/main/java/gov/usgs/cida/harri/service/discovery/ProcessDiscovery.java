package gov.usgs.cida.harri.service.discovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author isuftin
 */
public class ProcessDiscovery {

    public static List<ProcessMD> getProcesses() throws IOException {
        List<ProcessMD> pmdList = new ArrayList<ProcessMD>();
        ProcessType[] pmdArr = new ProcessType[]{
            ProcessType.APACHE, 
            ProcessType.DJANGO, 
            ProcessType.TOMCAT
        };
        
        for (ProcessType ptype : pmdArr) {
            List<Integer> pidList = getProcessIDList(ptype);
            for (Integer pid : pidList) {
                ProcessMD pmd = new ProcessMD(pid, ptype);
                pmdList.add(pmd);
            }
        }
        
        return pmdList;
    }
    static List<String> getProcessList() throws IOException {
        return getProcessList(null);
    }

    static List<String> getProcessList(String processName) throws IOException {
        List<String> procList;
        Runtime run = Runtime.getRuntime();
        String grep = StringUtils.isEmpty(processName) ? "" : " | /bin/grep '" + processName + "' | /bin/grep -v grep ";
        String command = "/bin/ps -e " + grep + " | /bin/sed 1d";
        Process pr = run.exec(new String[]{"sh", "-c", command});

        try {
            int status= pr.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        procList = IOUtils.readLines(pr.getInputStream());
        return procList;
    }
    
    
    static List<Integer> getProcessIDList(ProcessType type) throws IOException {
        return getProcessIDList(getProcessList(type.toString()));
    }

    static List<Integer> getProcessIDList(List<String> processList) {
        List<Integer> processIDList = new ArrayList<Integer>();
        for (String process : processList) {
            try {
                processIDList.add(Integer.parseInt(process.trim().split(" ")[0]));
            } catch (NumberFormatException nfe) {
                // Ignore 
            }

        }
        return processIDList;
    }
    
}
