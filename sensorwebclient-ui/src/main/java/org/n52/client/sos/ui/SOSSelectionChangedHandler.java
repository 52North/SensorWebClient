/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.client.sos.ui;

import org.n52.client.sos.ctrl.SosDataManager;
import org.n52.io.crs.BoundingBox;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

final class SOSSelectionChangedHandler implements SelectionChangedHandler {

	private final StationSelectorController controller;

	public SOSSelectionChangedHandler(StationSelectorController controller) {
		this.controller = controller;
	}

	@Override
	public void onSelectionChanged(SelectionEvent event) {
		Record record = event.getRecord();
		if (event.getState() && record != null) {
			String serviceURL = record.getAttribute("url");
			parseAndStoreSOSMetadata(serviceURL, record);
			this.controller.performSOSDataRequests(serviceURL);
		}
	}

	private void parseAndStoreSOSMetadata(String serviceURL, Record record) {
		SOSMetadataBuilder builder = new SOSMetadataBuilder();
		SosDataManager dataManager = SosDataManager.getDataManager();
		if (!dataManager.contains(serviceURL)) {
	        parseAndSetServiceConfiguration(builder, record);
			dataManager.storeData(serviceURL, builder.build());
		};
	}

	private void parseAndSetServiceConfiguration(SOSMetadataBuilder builder, Record record) {
		try {
		    builder.addServiceURL(getValueFor(record, "url"))
		           .addServiceName(getValueFor(record, "itemName"))
                   .addServiceVersion(getValueFor(record, "version"))
                   .setWaterML(Boolean.parseBoolean(getValueFor(record, "waterML")))
                   .setForceXYAxisOrder(Boolean.parseBoolean(getValueFor(record, "forceXYAxisOrder")))
                   .setAutoZoom(Boolean.parseBoolean(getValueFor(record, "autoZoom")))
                   .setRequestChunk(Integer.parseInt(getValueFor(record, "requestChunk")))
                   //.addDefaultZoom(Integer.parseInt(getValueFor(record, "defaultZoom")))
                   .withExtent(createBoundingBox(record));
		} catch (Exception e) {
			GWT.log("Could not parse SERVICES configuration for: " + builder.getServiceURL(), e);
		}
	}
	
	private BoundingBox createBoundingBox(Record record) {
        Double llEasting = Double.parseDouble(getValueFor(record, "llEasting"));
        Double llNorthing = Double.parseDouble(getValueFor(record, "llNorthing"));
        Double urEasting = Double.parseDouble(getValueFor(record, "urEasting"));
        Double urNorthing = Double.parseDouble(getValueFor(record, "urNorthing"));
        
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        Point ll = factory.createPoint(new Coordinate(llEasting, llNorthing));
        Point ur = factory.createPoint(new Coordinate(urEasting, urNorthing));
        return new BoundingBox(ll, ur, "EPSG:4326");
    }

    private String getValueFor(Record record, String parameter) {
	    String value = record.getAttribute(parameter);
        return value == null || value.isEmpty() ? null: value;
	}

}