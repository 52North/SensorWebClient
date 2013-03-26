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
package org.n52.server.service.rest;

import java.util.Collection;

public class QuerySet {

	private Collection<String> offeringFilter;

	private Collection<String> procedureFilter;

	private Collection<String> phenomenonFilter;

	private Collection<String> featureOfInterestFilter;

	private int pagingStartIndex;

	private int pagingInterval;

	private String spatialFilter;

	public QuerySet() {
		// for serialization
	}

	public Collection<String> getOfferingFilter() {
		return offeringFilter;
	}

	public void setOfferingFilter(Collection<String> offeringFilter) {
		this.offeringFilter = offeringFilter;
	}

	public Collection<String> getProcedureFilter() {
		return procedureFilter;
	}

	public void setProcedureFilter(Collection<String> procedureFilter) {
		this.procedureFilter = procedureFilter;
	}

	public Collection<String> getPhenomenonFilter() {
		return phenomenonFilter;
	}

	public void setPhenomenonFilter(Collection<String> phenomenonFilter) {
		this.phenomenonFilter = phenomenonFilter;
	}

	public Collection<String> getFeatureOfInterestFilter() {
		return featureOfInterestFilter;
	}

	public void setFeatureOfInterestFilter(Collection<String> featureOfInterestFilter) {
		this.featureOfInterestFilter = featureOfInterestFilter;
	}

	public int getPagingStartIndex() {
		return pagingStartIndex;
	}

	public void setPagingStartIndex(int pagingStartIndex) {
		this.pagingStartIndex = pagingStartIndex;
	}

	public int getPagingInterval() {
		return pagingInterval;
	}

	public void setPagingInterval(int pagingInterval) {
		this.pagingInterval = pagingInterval;
	}

	public String getSpatialFilter() {
		return spatialFilter;
	}

	public void setSpatialFilter(String spatialFilter) {
		this.spatialFilter = spatialFilter;
	}

}
