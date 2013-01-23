package gov.usgs.cida.harri.service.instance;

class ApplicationInfo {
    private String name;
    private String startTime;
    private String startupTime;
    private Boolean running;
    
    public ApplicationInfo(String name, String startTime, String startupTime, Boolean running) {
        this.name = name;
        this.startTime = startTime;
        this.startupTime = startupTime;
        this.running = running;
    }

    public String getName() {
        return name;
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