package org.n52.io.v1.data.in;

import java.util.HashMap;
import java.util.Map;

public class DiagramOptions {
	
	private String type;

	
	/*
	 * 
	 * TODO be more concrete/ on a higher level of abstraction: getLine() .. getLineWidth ... etc. 
	 * 
	 */
	
	
	
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean hasProperty(String property) {
		return properties.containsKey(property);
	}
	
	public Object getProperty(String property) {
		return properties.get(property);
	}
	
	public String getPropertyAsString(String property) {
		return (String) properties.get(property);
	}
	
	public Double getPropertyAsDouble(String property) {
		return (Double) properties.get(property);
	}
	
	public int getPropertyAsInt(String property) {
		return ((Integer) properties.get(property)).intValue();
	}
	
	public Object[] getPropertyAsArray(String property) {
		return (Object[]) properties.get(property);
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
}
