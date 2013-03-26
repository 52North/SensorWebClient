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
import java.util.Collection;

/**
 * Represents a valid parameter constellation to retrieve timeseries data from
 * an SOS.
 * 
 * @see Station which contains multiple parameter constellations valid for a
 *      specific location.
 */
public class ParameterConstellation implements Serializable {

	private static final long serialVersionUID = 4336908002034438766L;

	private String procedure;

	private String phenomenon;

	private String featureOfInterest;

	private String offering;

	private String category;

	public boolean isValid() {
		if (this.offering == null || this.phenomenon == null
				|| this.procedure == null || featureOfInterest == null) {
			return false;
		}
		return true;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public String getPhenomenon() {
		return phenomenon;
	}

	public void setPhenomenon(String phenomenon) {
		this.phenomenon = phenomenon;
	}

	public String getFeatureOfInterest() {
		return featureOfInterest;
	}

	public void setFeatureOfInterest(String featureOfInterest) {
		this.featureOfInterest = featureOfInterest;
	}

	public String getOffering() {
		return offering;
	}

	public void setOffering(String offering) {
		this.offering = offering;
	}

	/**
	 * A label to categorize parameter constellation. If not set, the
	 * {@link #phenomenon} of the parameter constellation is returned. Can be
	 * used to filter a set of stations according a common category.
	 * 
	 * @return a label to categorize stations on which filtering can take place.
	 */
	public String getCategory() {
		return category == null ? phenomenon : category;
	}

	/**
	 * @param category
	 *            a filter to categorize stations.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((featureOfInterest == null) ? 0 : featureOfInterest
						.hashCode());
		result = prime * result
				+ ((offering == null) ? 0 : offering.hashCode());
		result = prime * result
				+ ((phenomenon == null) ? 0 : phenomenon.hashCode());
		result = prime * result
				+ ((procedure == null) ? 0 : procedure.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParameterConstellation))
			return false;
		ParameterConstellation other = (ParameterConstellation) obj;
		if (featureOfInterest == null) {
			if (other.featureOfInterest != null)
				return false;
		} else if (!featureOfInterest.equals(other.featureOfInterest))
			return false;
		if (offering == null) {
			if (other.offering != null)
				return false;
		} else if (!offering.equals(other.offering))
			return false;
		if (phenomenon == null) {
			if (other.phenomenon != null)
				return false;
		} else if (!phenomenon.equals(other.phenomenon))
			return false;
		if (procedure == null) {
			if (other.procedure != null)
				return false;
		} else if (!procedure.equals(other.procedure))
			return false;
		return true;
	}

	public boolean hasProcedure(String procedure) {
		return this.procedure != null && this.procedure.equals(procedure);
	}

	public boolean hasPhenomenon(String phenomenon) {
		return this.phenomenon != null && this.phenomenon.equals(phenomenon);
	}

	public boolean hasFoi(String foi) {
		return this.featureOfInterest != null
				&& this.featureOfInterest.equals(foi);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ParameterConstellation: [ ").append("\n");
		sb.append("\tOffering: ").append(offering).append("\n");
		sb.append("\tProcedure: ").append(procedure).append("\n");
		sb.append("\tFeatureOfInterest: ").append(featureOfInterest)
				.append("\n");
		sb.append("\tPhenomenon: ").append(phenomenon).append("\n");
		sb.append("\tCategory: ").append(category).append("]\n");
		return sb.toString();
	}

	public boolean hasOffering(String offeringId) {
		return this.offering != null && this.offering.equals(offeringId);
	}

	public ParameterConstellation clone() {
		ParameterConstellation paramConst = new ParameterConstellation();
		paramConst.setFeatureOfInterest(featureOfInterest);
		paramConst.setOffering(offering);
		paramConst.setPhenomenon(phenomenon);
		paramConst.setProcedure(procedure);
		paramConst.setCategory(category);
		return paramConst;
	}

	public boolean matchFilter(Collection<String> offeringFilter, Collection<String> phenomenonFilter,
			Collection<String> procedureFilter, Collection<String> featureFilter) {
		return matchOffering(offeringFilter) && matchPhenomenon(phenomenonFilter) && matchProcedure(procedureFilter) && matchFeature(featureFilter);
	}

	private boolean matchFeature(Collection<String> featureFilter) {
		if(featureFilter == null || featureFilter.size() == 0) {
			return true;
		}
		for (String featureID : featureFilter) {
			if (featureID == null || featureID.equals(this.featureOfInterest)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchProcedure(Collection<String> procedureFilter) {
		if(procedureFilter == null || procedureFilter.size() == 0) {
			return true;
		}
		for (String procedureID : procedureFilter) {
			if (procedureID == null || procedureID.equals(this.procedure)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchPhenomenon(Collection<String> phenomenonFilter) {
		if(phenomenonFilter == null || phenomenonFilter.size() == 0) {
			return true;
		}
		for (String phenomenonID : phenomenonFilter) {
			if (phenomenonID == null || phenomenonID.equals(this.phenomenon)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchOffering(Collection<String> offeringFilter) {
		if(offeringFilter == null || offeringFilter.size() == 0) {
			return true;
		}
		for (String offeringID : offeringFilter) {
			if (offeringID == null || offeringID.equals(this.offering)) {
				return true;
			}
		}
		return false;
	}

}
