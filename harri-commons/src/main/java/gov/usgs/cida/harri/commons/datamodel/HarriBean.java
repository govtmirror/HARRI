package gov.usgs.cida.harri.commons.datamodel;

import java.io.Serializable;

/**
 *
 * @author isuftin
 */
public abstract class HarriBean implements Serializable {
	private String identifier;
	abstract String serialize();
	abstract HarriBean deserialize(String input);

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
