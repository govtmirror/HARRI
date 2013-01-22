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

    static List<Integer> gerProcessIDList(ProcessType pType) throws IOException {
        String pTypeName = pType.toString();
        List<Integer> processIDList = new ArrayList<Integer>();
        List<String> pStringList = ProcessDiscovery.getProcessList(pTypeName);
        processIDList = getProcessIDList(pStringList);
        return processIDList;
    }

    static List<String> getProcessList(String processName) throws IOException {
        List<String> procList;
        Runtime run = Runtime.getRuntime();
        String grep = StringUtils.isEmpty(processName) ? "" : " | grep '" + processName + "'";

        // Using sed 1d here to kill the first line. On linux, "ps h" doesn't print the header. 
        // This is not the case with MacOS
        Process pr = run.exec(new String[]{"/bin/sh", "-c", "ps -ex" + grep + " | grep -v grep | sed 1d"});
        try {
            pr.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessDiscovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        procList = IOUtils.readLines(pr.getInputStream());
        return procList;
    }

    static List<String> getProcessList() throws IOException {
        return ProcessDiscovery.getProcessList(null);
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

    static List<Integer> getProcessIDList(ProcessType type) throws IOException {
        return getProcessIDList(getProcessList(type.toString()));
    }
}
