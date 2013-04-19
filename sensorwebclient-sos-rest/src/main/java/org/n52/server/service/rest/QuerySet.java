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

import java.util.ArrayList;
import java.util.Collection;

import org.n52.server.service.rest.model.BoundingBox;

public class QuerySet {

	private Collection<String> offerings = new ArrayList<String>();

	private Collection<String> procedures = new ArrayList<String>();

	private Collection<String> phenomenons = new ArrayList<String>();

	private Collection<String> featureOfInterests = new ArrayList<String>();

    private BoundingBox spatialFilter;

	private int offset;

	private int size;

	public QuerySet() {
		// for serialization
	}

	public Collection<String> getOfferings() {
		return offerings;
	}
	
	public void addOffering(String offering) {
	    offerings.add(offering);
	}

	public void addAllOfferings(Collection<String> offerings) {
		this.offerings.addAll(offerings);
	}

	public Collection<String> getProcedures() {
		return procedures;
	}
	
	public void addProcedure(String procedure) {
	    procedures.add(procedure);
	}

	public void addAllProcedures(Collection<String> procedures) {
		this.procedures.addAll(procedures);
	}

	public Collection<String> getPhenomenonS() {
		return phenomenons;
	}

	public void addAllPhenomenons(Collection<String> phenomenons) {
		this.phenomenons.addAll(phenomenons);
	}

	public Collection<String> getFeatureOfInterests() {
		return featureOfInterests;
	}

	public void addAllFeatureOfInterests(Collection<String> featureOfInterests) {
		this.featureOfInterests.addAll(featureOfInterests);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getTotal() {
		return size;
	}

	public void setTotal(int size) {
		this.size = size;
	}

	public BoundingBox getSpatialFilter() {
		return spatialFilter;
	}

	public void setSpatialFilter(BoundingBox spatialFilter) {
		this.spatialFilter = spatialFilter;
	}

}
