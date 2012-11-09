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
package org.n52.client.control;

import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.n52.client.control.service.SOSController;
import org.n52.client.control.service.SesController;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.EventCallback;
import org.n52.client.eventBus.events.DatesChangedEvent;
import org.n52.client.eventBus.events.InitEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetFeatureEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetOfferingEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetPhenomenonsEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetProcedureEvent;
import org.n52.client.eventBus.events.dataEvents.sos.GetStationEvent;
import org.n52.client.eventBus.events.dataEvents.sos.NewSOSMetadataEvent;
import org.n52.client.eventBus.events.dataEvents.sos.OverviewIntervalChangedEvent;
import org.n52.client.eventBus.events.dataEvents.sos.OverviewIntervalChangedEvent.IntervalType;
import org.n52.client.eventBus.events.dataEvents.sos.RequestDataEvent;
import org.n52.client.eventBus.events.dataEvents.sos.StoreSOSMetadataEvent;
import org.n52.client.model.communication.requestManager.RequestManager;
import org.n52.client.model.data.dataManagers.DataManagerSosImpl;
import org.n52.client.model.data.dataManagers.TimeManager;
import org.n52.client.view.View;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.client.view.gui.widgets.stationPicker.StationPicker;
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public final class Application {

    private static boolean HAS_STARTED = false;
    
    public static boolean isHasStarted() {
    	return HAS_STARTED;
    }
    
    public static void setHasStarted(boolean hasStarted) {
    	HAS_STARTED = hasStarted;
    }
    
	public static void start() {
		// TODO refactor startup to be more explicit
        PropertiesManager.getInstance();
	}

    public static void continueStartup() {

        // init handlers before throwing events
        DataManagerSosImpl.getInst();
        new I18N();
        new SOSController();
        if (PropertiesManager.getInstance().getTabsFromPropertiesFile().contains("SesTab")) {
            new SesController();
        }
        View.getInstance();
        Element element = Document.get().getElementById("loadingWrapper");
        while (element.hasChildNodes()) {
            element.removeChild(element.getFirstChild());
        }

        Application.finishStartup();
    }

    public static void finishStartup() {
        try {
        	// handle permalink
            if (Window.Location.getHref().indexOf("?") > -1) {
                String[] sosUrls = { null };
                String[] offerings = { null };
                String[] fois = { null };
                String[] procedures = { null };
                String[] phenomenons = { null };
                String begin = null;
                String end = null;
                try {
                    sosUrls = Window.Location.getParameter("sos").split(",");
                    offerings = Window.Location.getParameter("offering").split(",");
                    fois = Window.Location.getParameter("stations").split(",");
                    procedures = Window.Location.getParameter("procedures").split(",");
                    phenomenons = Window.Location.getParameter("phenomenons").split(",");
                    begin = Window.Location.getParameter("begin");
                    end = Window.Location.getParameter("end");

                    if (!isAllParametersAvailable(sosUrls, offerings, fois, procedures, phenomenons)) {
                        Toaster.getInstance().addErrorMessage(I18N.sosClient.errorUrlParsing());
                        return;
                    }
                } catch (Exception e) {
                    // happens in DEV mode where '?' is always present
                    PropertiesManager properties = PropertiesManager.getInstance();
                    boolean showStationPickerAtStartup = properties.getParameterAsBoolean("showStationPickerAtStartup");
                    if (showStationPickerAtStartup) {
                        StationPicker.getInst().show();
                    }
                    return;
                }

                try {
                    long beginDate = ClientUtils.parseDateStringToMillis(begin);
                    long endDate = ClientUtils.parseDateStringToMillis(end);
                    Toaster.getInstance().addMessage("Begin: " + begin + " (" + beginDate + ")");
                    Toaster.getInstance().addMessage("End: " + end + " (" + endDate + ")");
                    EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(beginDate, endDate, true));
                } catch (Exception e) {
                    if (!GWT.isProdMode()) {
                        GWT.log("", e);
                    }
                }

                for (int i = 0; i < sosUrls.length; i++) {
                    final String url = sosUrls[i];
                    final String offering = offerings[i];
                    final String procedure = procedures[i];
                    final String phenomenon = phenomenons[i];
                    final String foi = fois[i];
                    
					@SuppressWarnings("deprecation")
					SOSMetadata sosMetadata = new SOSMetadata(sosUrls[i], sosUrls[i]); 
                    EventBus.getMainEventBus().fireEvent(new StoreSOSMetadataEvent(sosMetadata));
                    GetPhenomenonsEvent getPhensEvt = new GetPhenomenonsEvent.Builder(url).build();
                    EventBus.getMainEventBus().fireEvent(getPhensEvt);
                    GetFeatureEvent getFoiEvt = new GetFeatureEvent(url, foi);
                    EventBus.getMainEventBus().fireEvent(getFoiEvt);
                    GetOfferingEvent getOffEvt = new GetOfferingEvent(url, offering);
                    EventBus.getMainEventBus().fireEvent(getOffEvt);
                    GetProcedureEvent getProcEvt = new GetProcedureEvent(url, procedure);
                    EventBus.getMainEventBus().fireEvent(getProcEvt);
                    GetStationEvent getStationEvt = new GetStationEvent(url, offering, procedure, phenomenon, foi);
                    EventBus.getMainEventBus().fireEvent(getStationEvt);
                    
                    new PermaLinkController(url, offering, procedure, phenomenon, foi);

                    EventBus.getMainEventBus().fireEvent(new NewSOSMetadataEvent());
                }
            } else {
				StationPicker.getInst().show();
			}
        } catch (Exception e) {
            if (!GWT.isProdMode()) {
                GWT.log("", e);
            }
            Toaster.getInstance().addErrorMessage(I18N.sosClient.errorUrlParsing());
        } finally {
            finalEvents();
        }
    }

    private static boolean isAllParametersAvailable(String[] sosUrls, String[] offerings, String[] fois,
            String[] procedures, String[] phenomenons) {
        return sosUrls.length != 0 && offerings.length != 0 && fois.length != 0 && procedures.length != 0
                && phenomenons.length != 0;
    }

    private static void finalEvents() {
        // check for time intervals bigger than the default overview interval
        // (in days)
        PropertiesManager propertiesMgr = PropertiesManager.getInstance();
        int days = propertiesMgr.getParamaterAsInt(Constants.DEFAULT_OVERVIEW_INTERVAL, 5);

        TimeManager timeMgr = TimeManager.getInst();
        long timeInterval = timeMgr.daysToMillis(days);
        long currentInterval = timeMgr.getEnd() - timeMgr.getBegin();

        if (timeInterval <= currentInterval) {
            timeInterval += timeMgr.getOverviewOffset(timeMgr.getBegin(), timeMgr.getEnd());
        }

        EventBus.getMainEventBus().fireEvent(new OverviewIntervalChangedEvent(timeInterval, IntervalType.DAY));
        EventBus.getMainEventBus().fireEvent(new InitEvent(), new EventCallback() {
            
            public void onEventFired() {
                EventBus.getOverviewChartEventBus().fireEvent(StateChangeEvent.createMove());
                final Timer t = new Timer() {

                    @Override
                    public void run() {
                        if (RequestManager.hasUnfinishedRequests()) {
                            this.schedule(200);
                        } else {
                        	HAS_STARTED = false;
                            EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
                        }
                    }
                };
                t.schedule(200);
            }
        });
    }
}
