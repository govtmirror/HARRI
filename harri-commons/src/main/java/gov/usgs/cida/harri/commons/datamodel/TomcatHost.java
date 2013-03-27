package gov.usgs.cida.harri.commons.datamodel;

import java.util.List;

public class TomcatHost extends HarriBean {
	private List<Tomcat> tomcatInstances;
	private String managerId;

	public List<Tomcat> getTomcatInstances() {
		return tomcatInstances;
	}

	public void setTomcatInstances(List<Tomcat> tomcatInstances) {
		this.tomcatInstances = tomcatInstances;
	}
	
	public void addTomcatInstance(Tomcat tomcatInstance) {
		this.tomcatInstances.add(tomcatInstance);
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
}
