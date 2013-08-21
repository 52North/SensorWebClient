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
                addRow(refValue.getID(), refValue.getValue().toString());
            }
        }
    }

    private void addRow(String label, String value) {
        int row = metadataTable.getRowCount();
        metadataTable.setText(row, 0, label);
        metadataTable.setText(row, 1, value);
    }
    
}
