package org.n52.client.ses.ui;

import java.util.Set;

import org.n52.client.sos.legend.TimeSeries;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;

import com.google.gwt.user.client.ui.FlexTable;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class TimeSeriesMetadataTable extends FlexTable {

    private CreateEventAbonnementController controller;

    public TimeSeriesMetadataTable(CreateEventAbonnementController controller) {
        setStyleName("n52_sensorweb_client_create_abo_metadata_table");
        this.controller = controller;
        updateTimeSeries();
    }

    public void updateTimeSeries() {
        removeAllRows();
        
        TimeSeries timeSeries = controller.getTimeSeries();
        addRow("Datenanbieter",timeSeries.getSosUrl());
        addRow("Station", timeSeries.getStationName());
        addRow("Parameter", timeSeries.getProperties().getPhenomenon().getLabel());
        
        addRow("Wert", "");
        addRow("Maßeinheit", timeSeries.getUnitOfMeasure());

        addRefValueTable(timeSeries);
    }

    private void addRow(String label, FormItem item) {
        
    }

    private void addRow(String label, String value) {
        int row = getRowCount();
        setText(row, 0, label);
//        getFlexCellFormatter().setStyleName(row, 0, "n52_simpleRuleTable_column_left");
        setText(row, 1, value);
//        getFlexCellFormatter().setStyleName(row, 1, "n52_simpleRuleTable_column_right");
    }
    
    private void addRefValueTable(TimeSeries timeSeries) {
        TimeSeriesProperties properties = timeSeries.getProperties();
        Set<String> refValues = properties.getrefValues();
        if (refValues != null && refValues.size() > 0) {
            for (String refValueStr : refValues) {
                ReferenceValue refValue = properties.getRefValue(refValueStr);
                addRow(refValue.getID(), refValue.getValue()+"");
            }
        }
    }
    
}
