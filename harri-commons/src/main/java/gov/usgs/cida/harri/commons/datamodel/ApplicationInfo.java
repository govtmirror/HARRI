package gov.usgs.cida.harri.commons.datamodel;

public class ApplicationInfo {
    private String context;
    private String startTime;
    private String startupTime;
    private Boolean running;
    
    public ApplicationInfo(String name, String startTime, String startupTime, Boolean running) {
        this.context = name;
        this.startTime = startTime;
        this.startupTime = startupTime;
        this.running = running;
    }

    public String getContext() {
        return context;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getStartupTime() {
        return startupTime;
    }

    public Boolean getRunning() {
        return running;
    }
}