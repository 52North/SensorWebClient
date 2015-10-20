/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
