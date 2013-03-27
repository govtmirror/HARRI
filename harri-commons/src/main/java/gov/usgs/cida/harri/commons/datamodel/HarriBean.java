package gov.usgs.cida.harri.commons.datamodel;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 *
 * @author isuftin
 */
public class HarriBean implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String identifier;
	
	private final String type;
	
	public HarriBean() {
		this.type = this.getClass().getSimpleName();
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String serialize() {
		return new Gson().toJson(this);
	}

	public HarriBean deserialize(String input) {
		return new Gson().fromJson(input, this.getClass());
	}
	
	public String getType() {
		return type;
	}
}
