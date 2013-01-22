package gov.usgs.cida.harri.service.discovery;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * http://www.kernel.org/doc/Documentation/filesystems/proc.txt
 * http://tldp.org/LDP/Linux-Filesystem-Hierarchy/html/proc.html
 * @author isuftin
 */
public class ProcessMD {
    private Integer pid;
    private String name;
    private ProcessType type;
    
    
    private long sessionId;
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
    private String status;
    // IO statistics
    private String io;
    
    public ProcessMD(Integer pid, ProcessType type) throws IOException {
        this(pid, type.getName(), type);
    }
    
    public ProcessMD(Integer pid, String name, ProcessType type) throws IOException {
        this.pid = pid;
        this.type = type;
        this.name = name;
        
        this.learn();
    }
    
    private void learn() throws IOException {
        File procDir = new File("/proc");
        if (!procDir.exists()) {
            throw new IOException("/proc directory does not exist - HARRI is unable to learn about this process");
        } else if (!procDir.isDirectory()) {
             throw new IOException("/proc directory is not a directory - HARRI is unable to learn about this process");
        } else if (!procDir.canRead()) {
             throw new IOException("/proc directory is not readable - HARRI is unable to learn about this process");
        }
    }

    public Integer getPid() {
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

    public String getStatus() {
        return status;
    }
    
}
