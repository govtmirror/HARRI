package gov.usgs.cida.harri.service.discovery;

/**
 *
 * @author isuftin
 */
public enum ProcessType {
    TOMCAT("tomcat"), 
    DJANGO("fill_in_the_blank"), 
    APACHE("apache");
    
    private String processName;
    ProcessType(String processName) {
        this.processName = processName;
    }
    
    @Override
    public String toString() {
        return this.processName;
    }
}
