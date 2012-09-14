/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;

import org.n52.shared.serializable.pojos.EastingNorthing;

public class Station implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id = IDGenerator.generate(); 

	private String srs;
	
	private String feature;
	
	private String phenomenon;
	
	private String procedure;
	
	private String offering;

    private EastingNorthing location;
	
	public String getId() {
		return id;
	}
	
	public void setLocation(EastingNorthing location, String srs) {
	    this.location = location;
	    this.srs = srs;
	}

	public double getLat() {
		return location.getNorthing();
	}

	public double getLon() {
		return location.getEasting();
	}

    public void setSrs(String srs) {
        this.srs = srs;
    }

	public String getSrs() {
		return srs;
	}

	public boolean hasAllEntries() {
		if (this.srs == null || this.feature == null || this.offering == null || this.phenomenon == null || this.procedure == null) {
			return false;
		}
		return true;
	}

	public boolean isProcedureEqual(String procedure) {
		return this.procedure.equals(procedure);
	}

	public boolean isPhenomenonEqual(String phenomenon) {
		return this.phenomenon.equals(phenomenon);
	}

	public boolean isFeatureEqual(String feature) {
		return this.feature.equals(feature);
	}
	
	public boolean isOfferingEqual(String offering) {
		return this.offering.equals(offering);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// TODO wait for fix: http://code.google.com/p/google-web-toolkit/issues/detail?id=3404
//      sb.append(getClass().getSimpleName()).append(" [ ");
        sb.append("Station: [ ").append("\n");
		sb.append("Location: ").append(location).append("\n");
        sb.append("Feature: ").append(feature).append("\n");
		sb.append("Offering: ").append(offering).append("\n");
		sb.append("Procedure: ").append(procedure).append("\n");
		sb.append("Phenomenon: ").append(phenomenon).append(" ]");
		return sb.toString();
	}
	
	public String getFeature() {
		return this.feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getPhenomenon() {
		return this.phenomenon;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public String getProcedure() {
		return this.procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}
	
	public String getOffering() {
		return this.offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}
	
	public Station clone() {
		Station station = new Station();
        station.setLocation(location, srs);
        station.setPhenomenon(phenomenon);
        station.setProcedure(procedure);
        station.setOffering(offering);
		station.setFeature(feature);
		return station;
	}

	private static class IDGenerator {
		
		private static int id = 0;
		
		public static String generate() {
			return String.valueOf(++id);
		}
	}

}


