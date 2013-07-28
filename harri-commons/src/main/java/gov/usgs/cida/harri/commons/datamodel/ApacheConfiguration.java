package gov.usgs.cida.harri.commons.datamodel;

import java.util.List;

public class ApacheConfiguration extends HarriBean {
	private static final long serialVersionUID = 823648L;
	List<ProxyMapping> proxyMappings;

	public List<ProxyMapping> getProxyMappings() {
		return proxyMappings;
	}

	public void setProxyMappings(List<ProxyMapping> proxyMappings) {
		this.proxyMappings = proxyMappings;
	}
	
	public void addProxyMapping(ProxyMapping proxyMapping) {
		this.proxyMappings.add(proxyMapping);
	}
}
