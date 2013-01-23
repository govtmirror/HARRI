package gov.usgs.cida.harri.service.discovery;

import gov.usgs.cida.harri.service.instance.Instance;
import gov.usgs.cida.harri.service.instance.Tomcat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

/**
 * http://www.kernel.org/doc/Documentation/filesystems/proc.txt
 * http://tldp.org/LDP/Linux-Filesystem-Hierarchy/html/proc.html
 *
 * @author isuftin
 */
public class ProcessMD {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(ProcessMD.class);

    public static org.slf4j.Logger getLOG() {
        return LOG;
    }
    private Long pid;
    private String name;
    private ProcessType type;
    private Long sessionId;
    private Map<String, String> startupOptions;
    
    // Command line arguments
    private String commandLine;
    // Link to the current working directory
    private String currentWorkingDirectory;
    // Values of environment variables
    private String environmentVars;
    // Link to the executable of this process
    private String exe;
    // Process memory status information.
    private String statm;
    // Process status in human readable form.
    private List<String> status;
    // IO statistics
    private List<String> io;

    public ProcessMD(Long pid, ProcessType type) throws IOException {
        this(pid, type.getName(), type);
    }

    public ProcessMD(Long pid, String name, ProcessType type) throws IOException {
        this.pid = pid;
        this.type = type;
        this.name = name;
        this.startupOptions = new HashMap<String, String>();
        this.learn();
    }

    public Instance createInstance() {
        Instance instance = null;
        switch (this.type) {
            case TOMCAT: 
                instance = new Tomcat(this);
        }
        return instance;
    }
    
    private void learn() throws IOException {
        File procDir = new File("/proc/" + this.getPid());
        String processDescription = ProcessDiscovery.getProcessList(String.valueOf(this.getPid())).get(0);
        
        if (this.getType().equals(ProcessType.TOMCAT)) {
            String[] split = processDescription.split(" ");
            for (String x : split) {
                if (x.startsWith("-D")) {
                   String[] kArr = x.substring(2).split("=");
                    this.startupOptions.put(kArr[0], kArr[1]);
                }
            }
        }
        
        if (!procDir.exists()) {
            getLOG().warn("/proc directory does not exist");
        } else if (!procDir.isDirectory()) {
            getLOG().warn("/proc directory is not a directory");
        } else if (!procDir.canRead()) {
            getLOG().warn("/proc directory is not readable");
        }

        List<String> readOut;
        File sessionIdFile = FileUtils.getFile(procDir, "sessionid");
        try {
            readOut = readFile(sessionIdFile);
            if (readOut.size() > 0) {
                this.sessionId = Long.parseLong(readOut.get(0));
            }
        } catch (Exception ex) {
            this.sessionId = 0l;
        }

        File cmdLineFile = FileUtils.getFile(procDir, "cmdline");
        try {
            readOut = readFile(cmdLineFile);
            if (readOut.size() > 0) {
                this.commandLine = readOut.get(0);
            }
        } catch (Exception ex) {
            this.commandLine = "";
        }

        File cwdFile = FileUtils.getFile(procDir, "cwd");
        try {
            this.currentWorkingDirectory = FileUtils.isSymlink(cwdFile) ? cwdFile.getCanonicalPath() : cwdFile.getAbsolutePath();
        } catch (Exception ex) {
            this.currentWorkingDirectory = "";
        }

        File envVarsFile = FileUtils.getFile(procDir, "environ");
        try {
            readOut = readFile(envVarsFile);
            if (readOut.size() > 0) {
                this.environmentVars = readOut.get(0);
            }
        } catch (Exception ex) {
            this.environmentVars = "";
        }

        File exeFile = FileUtils.getFile(procDir, "exe");
        try {
            this.exe = FileUtils.isSymlink(exeFile) ? exeFile.getCanonicalPath() : exeFile.getAbsolutePath();
        } catch (Exception ex) {
            this.exe = "";
        }

        File statmFile = FileUtils.getFile(procDir, "statm");
        try {
            readOut = readFile(statmFile);
            if (readOut.size() > 0) {
                this.statm = readOut.get(0);
            }
        } catch (Exception ex) {
            this.statm = "";
        }

        File statusFile = FileUtils.getFile(procDir, "status");
        try {
            readOut = readFile(statusFile);
            if (readOut.size() > 0) {
                for (String stat : readOut) {
                    this.getStatus().add(stat);
                }

            }
        } catch (Exception ex) {
            this.status = new ArrayList<String>();
        }

        File ioFile = FileUtils.getFile(procDir, "io");
        try {
            readOut = readFile(ioFile);
            if (readOut.size() > 0) {
                for (String ioline : readOut) {
                    this.getIo().add(ioline);
                }

            }
        } catch (Exception ex) {
            this.io = new ArrayList<String>();
        }
    }

    List<String> readFile(File file) throws IOException {
        List<String> fileContents = new ArrayList<String>();
        if (file.exists() && file.canRead()) {
            List<String> fileLines = FileUtils.readLines(file);
            for (String line : fileLines) {
                fileContents.add(line);
            }
        }
        return fileContents;
    }

    public Long getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public ProcessType getType() {
        return type;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public String getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }

    public String getEnvironmentVars() {
        return environmentVars;
    }

    public String getExe() {
        return exe;
    }

    public String getStatm() {
        return statm;
    }

    public List<String> getStatus() {
        return Collections.unmodifiableList(status);
    }

    public List<String> getIo() {
        return Collections.unmodifiableList(io);
    }

    public Map<String, String> getStartupOptions() {
        return Collections.unmodifiableMap(startupOptions);
    }
}
