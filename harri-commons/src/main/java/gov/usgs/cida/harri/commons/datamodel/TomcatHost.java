package gov.usgs.cida.harri.commons.datamodel;

import java.util.List;

public class TomcatHost extends HarriBean {
	private static final long serialVersionUID = 234231L;
	private List<Tomcat> tomcatInstances;

	public List<Tomcat> getTomcatInstances() {
		return tomcatInstances;
	}

	public void setTomcatInstances(List<Tomcat> tomcatInstances) {
		this.tomcatInstances = tomcatInstances;
	}
	
	public void addTomcatInstance(Tomcat tomcatInstance) {
		this.tomcatInstances.add(tomcatInstance);
	}
}
