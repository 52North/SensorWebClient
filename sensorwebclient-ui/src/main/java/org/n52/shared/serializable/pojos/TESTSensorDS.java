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
package org.n52.shared.serializable.pojos;

import static org.n52.client.ses.i18n.I18NStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.List;

import org.n52.client.eventBus.EventBus;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.service.rpc.RpcSesDataSourceService;
import org.n52.shared.service.rpc.RpcSesDataSourceServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class TESTSensorDS extends GwtRpcDataSource {

    public TESTSensorDS(String id) {
        setID(id);

        DataSourceTextField name = new DataSourceTextField("name", "SensorID");
        name.setPrimaryKey(true);
        name.setHidden(false);
        addField(name);

        DataSourceTextField status = new DataSourceTextField("status", "Status", 125);
        addField(status);

        DataSourceTextField inUse = new DataSourceTextField("inUse", "In Use", 200);
        addField(inUse);
    }

    @Override
    protected void executeFetch (final String requestId, final DSRequest request, final DSResponse response) {
        // These can be used as parameters to create paging.
        //        request.getStartRow ();
        //        request.getEndRow ();
        //        request.getSortBy ();
//        SC.showPrompt("LOADING...");
        RpcSesDataSourceServiceAsync service = GWT.create (RpcSesDataSourceService.class);

        service.fetch (new AsyncCallback<List<TestRecord>> () {
            public void onFailure (Throwable caught) {
                response.setStatus (RPCResponse.STATUS_FAILURE);
                processResponse (requestId, response);
                caught.printStackTrace();
            }
            public void onSuccess (List<TestRecord> result) {
                ListGridRecord[] list = new ListGridRecord[result.size ()];

                TestRecord testRecord;
                ListGridRecord listGridRecord;
                String status;
                
                int activated = 0;
                int deactivated = 0;
                
                for (int i = 0; i < list.length; i++) {
                    testRecord = result.get(i);
                    if (Boolean.parseBoolean(testRecord.getStatus())) {
                        status = i18n.active();
                        activated++;
                    } else {
                        status = i18n.inactive();
                        deactivated++;
                    }
                    
                    listGridRecord = new SensorRecord(testRecord.getName(), status, testRecord.getInUse());
//                    copyValues(result.get(i), listGridRecord);
                    list[i] = listGridRecord;
                }

                ArrayList<Integer> statistics = new ArrayList<Integer>();
                statistics.add(activated);
                statistics.add(deactivated);
                
                EventBus.getMainEventBus().fireEvent(new InformUserEvent(new SesClientResponse(SesClientResponse.types.REGISTERED_SENSORS, statistics)));
                response.setData (list);
                processResponse (requestId, response);
            }
        });
//        SC.clearPrompt();
    }

    @Override
    protected void executeAdd (final String requestId, final DSRequest request, final DSResponse response) {
        // Retrieve record which should be added.
        JavaScriptObject data = request.getData ();
        ListGridRecord rec = new ListGridRecord (data);
        TestRecord testRec = new TestRecord ();
        copyValues (rec, testRec);
        RpcSesDataSourceServiceAsync service = GWT.create (RpcSesDataSourceService.class);
        service.add (testRec, new AsyncCallback<TestRecord> () {
            public void onFailure (Throwable caught) {
                response.setStatus (RPCResponse.STATUS_FAILURE);
                processResponse (requestId, response);
            }
            public void onSuccess (TestRecord result) {
                ListGridRecord[] list = new ListGridRecord[1];
                SensorRecord newRec = new SensorRecord (result.getName(), result.getStatus(), result.getInUse());
//                copyValues (result, newRec);
                list[0] = newRec;
                response.setData (list);
                processResponse (requestId, response);
            }
        });
    }

    @Override
    protected void executeUpdate (final String requestId, final DSRequest request, final DSResponse response) {
        // Retrieve record which should be updated.
        JavaScriptObject data = request.getData ();
        ListGridRecord rec = new ListGridRecord (data);
        // Find grid
        ListGrid grid = (ListGrid) Canvas.getById (request.getComponentId ());
        // Get record with old and new values combined
        int index = grid.getRecordIndex (rec);
        rec = (ListGridRecord) grid.getEditedRecord (index);
        TestRecord testRec = new TestRecord ();
        copyValues (rec, testRec);
        RpcSesDataSourceServiceAsync service = GWT.create (RpcSesDataSourceService.class);
        service.update (testRec, new AsyncCallback<TestRecord> () {
            public void onFailure (Throwable caught) {
                response.setStatus (RPCResponse.STATUS_FAILURE);
                processResponse (requestId, response);
            }
            public void onSuccess (TestRecord result) {
                ListGridRecord[] list = new ListGridRecord[1];
                ListGridRecord updRec = new ListGridRecord ();
                copyValues (result, updRec);
                list[0] = updRec;
                response.setData (list);
                processResponse (requestId, response);
            }
        });
    }

    @Override
    protected void executeRemove (final String requestId, final DSRequest request, final DSResponse response) {
        // Retrieve record which should be removed.
        JavaScriptObject data = request.getData ();
        final ListGridRecord rec = new ListGridRecord (data);
        TestRecord testRec = new TestRecord ();
        copyValues (rec, testRec);
        RpcSesDataSourceServiceAsync service = GWT.create (RpcSesDataSourceService.class);
        service.remove(testRec, new AsyncCallback<Void> () {
            public void onFailure (Throwable caught) {
                response.setStatus (RPCResponse.STATUS_FAILURE);
                processResponse (requestId, response);
            }
            @Override
            public void onSuccess(Void thing) {
                ListGridRecord[] list = new ListGridRecord[1];
                // We do not receive removed record from server.
                // Return record from request.
                list[0] = rec;
                response.setData (list);
                processResponse(requestId, response);
            }
        });
    }

    private static void copyValues (ListGridRecord from, TestRecord to) {
        to.setName (from.getAttributeAsString ("name"));
        to.setName (from.getAttributeAsString ("status"));
        to.setInUse(from.getAttributeAsString ("inUse"));
    }

    private static void copyValues (TestRecord from, ListGridRecord to) {
        to.setAttribute ("name", from.getName());
        to.setAttribute ("status", from.getStatus());
        to.setAttribute ("inUse", from.getInUse());
    }
}