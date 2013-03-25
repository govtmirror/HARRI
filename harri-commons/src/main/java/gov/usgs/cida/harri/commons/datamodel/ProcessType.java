package gov.usgs.cida.harri.commons.datamodel;

/**
 *
 * @author isuftin
 */
public enum ProcessType {
    TOMCAT("tomcat", "org.apache.catalina.startup.Bootstrap start"), 
    DJANGO("wsgi", "django"), 
    APACHE("apache", "");
    
    private String processName;
    private String psIdentifier;
    ProcessType(String processName, String psIdentifier) {
        this.processName = processName;
        this.psIdentifier = psIdentifier;
    }
    
    public String getName() {
        return this.processName;
    }
    
    @Override
    public String toString() {
        return this.processName;
    }

    public String getProcesssIdentifier() {
        return this.psIdentifier;
    }
}
