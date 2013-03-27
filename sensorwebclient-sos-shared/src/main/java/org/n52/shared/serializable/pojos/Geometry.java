package org.n52.shared.serializable.pojos;

import java.io.Serializable;

import org.n52.shared.Constants;

public class Geometry implements Serializable {

	private static final long serialVersionUID = -4375356499762074776L;
	
	private String srs = Constants.DISPLAY_PROJECTION;
	
	public Geometry(){
		// client requires to be default instantiable
	}
	
	public Geometry(String srs) {
		this.srs = srs;
	}

    /**
     * @return the CRS's code including authority, eg 'EPSG:4326'.
     */
	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}
	
}
