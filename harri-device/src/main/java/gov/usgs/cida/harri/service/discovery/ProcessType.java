package gov.usgs.cida.harri.service.discovery;

/**
 *
 * @author isuftin
 */
public enum ProcessType {
    TOMCAT("tomcat"), 
    DJANGO("modwsgi"), 
    APACHE("apache"),
    JAVA("java");
    
    private String processName;
    ProcessType(String processName) {
        this.processName = processName;
    }
    
    public String getName() {
        return this.processName;
    }
    
    @Override
    public String toString() {
        return this.processName;
    }
}
