package gov.usgs.cida.harri.service.discovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author isuftin
 */
public class ProcessDiscovery {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(ProcessDiscovery.class);

    public static List<ProcessMD> getProcesses() throws IOException {
        return getProcesses(null);
    }
    
    public static List<ProcessMD> getProcesses(ProcessType pt) throws IOException {
        List<ProcessMD> pmdList = new ArrayList<ProcessMD>();
        ProcessType[] pmdArr;
        if (pt == null) {
            pmdArr = new ProcessType[]{
                ProcessType.APACHE,
                ProcessType.DJANGO,
                ProcessType.TOMCAT
            };
        } else {
            pmdArr = new ProcessType[]{pt};
        }

        for (ProcessType ptype : pmdArr) {
            List<Long> pidList = getProcessIDList(ptype);
            for (Long pid : pidList) {
                ProcessMD pmd = new ProcessMD(pid, ptype);
                pmdList.add(pmd);
            }
        }

        return pmdList;
    }

    static List<String> getProcessList() throws IOException {
        return getProcessList(null);
    }

    static List<String> getProcessList(String grepCrit) throws IOException {
        List<String> procList;
        Runtime run = Runtime.getRuntime();
        String grep = StringUtils.isEmpty(grepCrit) ? "" : " | grep '" + grepCrit + "' | grep -v grep ";
        String command = "ps -ax " + grep;
        Process pr = run.exec(new String[]{"sh", "-c", command});

        try {
            pr.waitFor();
        } catch (InterruptedException ex) {
            LOG.error(ex.getMessage());
        }

        procList = IOUtils.readLines(pr.getInputStream());
        return procList;
    }

    static List<Long> getProcessIDList(ProcessType type) throws IOException {
        return getProcessIDList(getProcessList(type.toString()));
    }

    static List<Long> getProcessIDList(List<String> processList) {
        List<Long> processIDList = new ArrayList<Long>();
        for (String process : processList) {
            try {
                processIDList.add(Long.parseLong(process.trim().split(" ")[0]));
            } catch (NumberFormatException nfe) {
                // Ignore 
            }

        }
        return processIDList;
    }
}
