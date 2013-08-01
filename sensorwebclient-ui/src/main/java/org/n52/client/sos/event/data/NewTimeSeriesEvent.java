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
package org.n52.client.sos.event.data;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.NewTimeSeriesEventHandler;
import org.n52.client.ui.View;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class NewTimeSeriesEvent extends FilteredDispatchGwtEvent<NewTimeSeriesEventHandler> {

    public static Type<NewTimeSeriesEventHandler> TYPE = new Type<NewTimeSeriesEventHandler>();

    private Station station;
    
    private SosTimeseries timeseries;
    
    private int width;

    private int height;

    private boolean requestSensorData;

    private TimeseriesRenderingOptions renderingOptions;

    public static class Builder {
		private Station station; // required
		private SosTimeseries timeseries; // required
		private int width = View.getView().getDataPanelWidth();
		private int height = View.getView().getDataPanelHeight();
		private TimeseriesRenderingOptions renderingOptions;
		private boolean requestSensorData = true;
		private NewTimeSeriesEventHandler[] blockedHandlers = new NewTimeSeriesEventHandler[0];
    	
    	public Builder addStation(final Station station) {
    		this.station = station;
    		return this;
    	}
    	public Builder addTimeseries(final SosTimeseries timeseries) {
    		this.timeseries = timeseries;
    		return this;
    	}
    	public Builder addWidth(final int width) {
    		this.width = width;
    		return this;
    	}
    	public Builder addHeight(final int height) {
    		this.height = height;
    		return this;
    	}
    	/**
    	 * Default is <code>true</code>.
    	 */
    	public Builder isRequestSensordata(final boolean requestSensordata) {
    		this.requestSensorData = requestSensordata;
    		return this;
    	}
    	public Builder addBlockedHandlers(NewTimeSeriesEventHandler... handlers) {
    		this.blockedHandlers = handlers;
    		return this;
    	}
        public Builder addRenderingOptions(TimeseriesRenderingOptions options) {
            this.renderingOptions = options;
            return this;
        }
    	public NewTimeSeriesEvent build() {
    		return new NewTimeSeriesEvent(this);
    	}
    	Station getStation() {
    		return this.station;
    	}
    	SosTimeseries getTimeseries() {
    		return this.timeseries;
    	}
    	int getWidth() {
    		return this.width;
    	}
    	int getHeight() {
    		return this.height;
    	}
    	boolean isRequestSensordata() {
    		return requestSensorData;
    	}
    	NewTimeSeriesEventHandler[] getBlockedHandlers() {
    		return blockedHandlers;
    	}
    	TimeseriesRenderingOptions getRenderingOptions() {
    	    return renderingOptions;
    	}
    }
    
    private NewTimeSeriesEvent(Builder builder) {
    	this.station = builder.getStation();
    	this.timeseries = builder.getTimeseries();
    	this.width = builder.getWidth();
    	this.height = builder.getHeight();
    	this.requestSensorData = builder.isRequestSensordata();
    	this.renderingOptions = builder.getRenderingOptions();
    }
    
    public boolean requestSensordata() {
        return this.requestSensorData;
    }

    public String getServiceUrl() {
        return this.timeseries.getServiceUrl();
    }
    
    public Station getStation() {
    	return this.station;
    }

    public SosTimeseries getTimeseries() {
        return this.timeseries;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    
    public TimeseriesRenderingOptions getRenderingOptions() {
        return this.renderingOptions;
    }

    @Override
    protected void onDispatch(NewTimeSeriesEventHandler handler) {
        handler.onNewTimeSeries(this);
    }

    public Type<NewTimeSeriesEventHandler> getAssociatedType() {
        return TYPE;
    }

}
