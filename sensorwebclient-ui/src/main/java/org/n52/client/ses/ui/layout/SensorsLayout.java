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
package org.n52.client.ses.ui.layout;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.List;

import org.n52.client.eventBus.EventBus;
import org.n52.client.ses.event.UpdateSensorEvent;
import org.n52.client.ses.ui.Layout;
import org.n52.shared.serializable.pojos.TESTSensorDS;

import com.google.gwt.user.client.Random;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class SensorsLayout.
 * 
 * This view is only for admins visible. Here the admin can activate or
 * delete sensors from the client database.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class SensorsLayout extends Layout {

    /** The sensor grid. */
    private ListGrid sensorGrid;
    
//    private SensorDS dataSource;
    
    private TESTSensorDS ds;
    
    private StaticTextItem sensorCountItem;
    
    private int activatedSensors = 0;
    private int deactivatedSensors = 0;

    /**
     * Instantiates a new sensors layout.
     */
    public SensorsLayout() {
        super(i18n.sensorManagement());
//        this.dataSource = new SensorDS("Sensors", null);
        this.ds = new TESTSensorDS("SensorsTest");
        init();
    }

    /**
     * Inits the.
     */
    private void init() {
        this.sensorGrid = new ListGrid() {

            @Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

                if (record != null) {
                    String fieldName = this.getFieldName(colNum);

                    if (fieldName.equals("status")) {
                        DynamicForm dynamic = new DynamicForm();
                        dynamic.setWidth(90);
                        
                        final SelectItem activeItem = new SelectItem();
                        activeItem.setWidth(90);
                        activeItem.setMultiple(false);
                        activeItem.setShowTitle(false);
                        activeItem.setValueMap(i18n.active(), i18n.inactive());
                        activeItem.addChangedHandler(new ChangedHandler() {
                            public void onChanged(ChangedEvent event) {
                                if (event.getValue().equals(i18n.active())) {
                                    activatedSensors++;
                                    deactivatedSensors--;
                                    record.setAttribute("status", i18n.active());
                                } else {
                                    deactivatedSensors++;
                                    activatedSensors--;
                                    record.setAttribute("status", i18n.inactive());
                                }
                                // save changes
                                String sensorID = record.getAttribute("name");
                                boolean newStatus = false;
                                String status = record.getAttribute("status");
                                if (status.equals(i18n.active())) {
                                    newStatus = true;
                                }
                                
                                EventBus.getMainEventBus().fireEvent(new UpdateSensorEvent(sensorID, newStatus));

                                setStatisticsData();
                            }
                        });
                        
                        if (record.getAttribute("status").equals(i18n.active())) {
                            activeItem.setValue(i18n.active());
                        } else if (record.getAttribute("status").equals(i18n.inactive())) {
                            activeItem.setValue(i18n.inactive());
                        }
                        
                        dynamic.setFields(activeItem);
                        return dynamic;

                    }
                    return null;
                }
                return null;
            }
        };

        // grid config
        this.sensorGrid.setShowRecordComponents(true);
        this.sensorGrid.setShowRecordComponentsByCell(true);
        this.sensorGrid.setWidth100();
        this.sensorGrid.setHeight100();
        this.sensorGrid.setShowAllRecords(false);
        this.sensorGrid.setShowFilterEditor(true);
        this.sensorGrid.setFilterOnKeypress(true);
        this.sensorGrid.setShowRollOver(false);
        this.sensorGrid.setDataSource(this.ds);
        this.sensorGrid.setAutoFetchData(true);
//        this.sensorGrid.sort(0, SortDirection.ASCENDING);
        
        // fields
        ListGridField nameField = new ListGridField("name", "SensorID");
        nameField.setType(ListGridFieldType.TEXT);
        nameField.setAlign(Alignment.CENTER);
        nameField.setWidth(300);

        ListGridField statusField = new ListGridField("status", "Status");
        statusField.setAlign(Alignment.CENTER);
        statusField.setValueMap(i18n.active(), i18n.inactive());

        ListGridField inUseField = new ListGridField("inUse", i18n.inUse());
        inUseField.setAlign(Alignment.CENTER);

        this.sensorGrid.setFields(nameField, statusField, inUseField);
        
        // sensorCountItem
        this.sensorCountItem = new StaticTextItem();
        this.sensorCountItem.setTitle(i18n.active() + "/" + i18n.inactive());

        // add member
        this.form.setFields(this.headerItem, this.sensorCountItem);
        addMember(this.form);
        addMember(this.sensorGrid);
    }
    
    /**
     * 
     * @param statistics 
     */
    public void setData(List<Integer> statistics) {
        
        this.activatedSensors = statistics.get(0);
        this.deactivatedSensors = statistics.get(1);
        setStatisticsData();
        
//        SensorRecord sensor;
//        Sensor sensorDTO;
//        String status;
//        
//        this.activatedSensors = 0;
//        this.deactivatedSensors = 0;
//        
//        if (this.dataSource == null) {
//            this.dataSource = new SensorDS("Sensors", sensors);
        }
        
        
//        for (int i = 0; i < sensors.size(); i++) {
//            sensorDTO = sensors.get(i);
//            if (sensorDTO.isActivated()) {
//                status = i18nManager.i18nSESClient.active();
//            } else {
//                status = i18nManager.i18nSESClient.inactive();
//            }
//            sensor = new SensorRecord(sensorDTO.getSensorID(), status, Boolean.toString(sensorDTO.isInuse()));
//            this.dataSource.removeData(sensor);
//            this.dataSource.addData(sensor);
//            if (sensorDTO.isActivated()) {
//                this.activatedSensors++;
//            } else {
//                this.deactivatedSensors++;
//            }
//        }
//        setStatisticsData();

    private void setStatisticsData(){
        this.sensorCountItem.setValue(this.activatedSensors + "/" + this.deactivatedSensors);
    }
    
    /**
     * 
     */
    public void fetchData(){
        this.sensorGrid.clear();
        this.ds.destroy();
        this.ds = new TESTSensorDS(String.valueOf(Random.nextInt()));
        
        // nach dem fetchData ist die Tabelle auf einmal linksbündig und
        // nicht mehr mittig!!!
        this.sensorGrid.setDataSource(this.ds);
        this.sensorGrid.fetchData();
//        this.sensorGrid.redraw();
    }
}