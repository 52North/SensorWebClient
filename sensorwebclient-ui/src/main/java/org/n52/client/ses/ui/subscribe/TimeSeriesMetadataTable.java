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
package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.Set;

import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeseriesProperties;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smartgwt.client.widgets.layout.VLayout;

public class TimeSeriesMetadataTable extends VLayout {

    private EventSubscriptionController controller;
    
    private FlexTable metadataTable = new FlexTable();

    public TimeSeriesMetadataTable(EventSubscriptionController controller) {
        metadataTable.setStyleName("n52_sensorweb_client_create_abo_metadata_table");
        setStyleName("n52_sensorweb_client_create_abo_metadata");
        this.controller = controller;
        initializeUserInterface();
    }

    private void initializeUserInterface() {
        setIsGroup(true);
        setGroupTitle(i18n.timeseriesMetadataTable());
        addMember(new ScrollPanel(metadataTable));
        updateTimeSeriesMetadata();
    }

    public void updateTimeSeriesMetadata() {
        metadataTable.removeAllRows();
        TimeseriesLegendData timeSeries = controller.getTimeSeries();
        addRow(i18n.provider(),timeSeries.getSosUrl());
        addRow(i18n.station(), timeSeries.getStationName());
        addRow(i18n.phenomenon(), timeSeries.getTimeSeriesLabel());
        addRow(i18n.unit(), timeSeries.getUnitOfMeasure());
        addRowsForReferenceValues(timeSeries);
        markForRedraw();
    }
    
    private void addRowsForReferenceValues(TimeseriesLegendData timeSeries) {
        TimeseriesProperties properties = timeSeries.getProperties();
        Set<String> refValues = properties.getReferenceValues();
        if (refValues != null && refValues.size() > 0) {
            for (String refValueStr : refValues) {
                ReferenceValue refValue = properties.getRefValue(refValueStr);
                addRow(refValue.getId(), refValue.getValue().toString());
            }
        }
    }

    private void addRow(String label, String value) {
        int row = metadataTable.getRowCount();
        metadataTable.setText(row, 0, label);
        metadataTable.setText(row, 1, value);
    }
    
}
