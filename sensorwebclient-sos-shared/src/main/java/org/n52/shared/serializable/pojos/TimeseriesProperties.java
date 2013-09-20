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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class TimeseriesProperties implements Serializable {

	private static final long serialVersionUID = 1813366884589984223L;

	private Station station;

    private SosTimeseries timeseries;

	private int height;

	private int width;

	private String language;

	private HashMap<String, ReferenceValue> refvalues = new HashMap<String, ReferenceValue>();

    private TimeseriesRenderingOptions renderingOptions = new TimeseriesRenderingOptions();

	/**
	 * The series type. 1 = levelline 2 = sumline
	 */
	private String seriesType = "1";

	private String lineStyle = "1";
	
	private String uom;

	private String metadataUrl = "";

	private Axis axis = null;

	private boolean setAxis = true;

	private double opacity = 100d;

	private boolean isScaledToZero = false;

	private boolean isYAxisVisible = true;

	private boolean isAutoScale = true;

	private boolean isHasData = false;

	public boolean hasData() {
		return this.isHasData;
	}

	public void setHasData(boolean hasData) {
		this.isHasData = hasData;
	}

	@SuppressWarnings("unused")
	private TimeseriesProperties() {
		// do nothing
	}
	    
	public TimeseriesProperties(SosTimeseries timeseries, Station station, int width, int height) {
	    this(timeseries, station, width, height, "", true);
	}

	public TimeseriesProperties(SosTimeseries timeseries, Station station,
			int width, int height, String uom, boolean isAutoScale) {
	    this.timeseries = timeseries;
        this.station = station;
        this.width = width;
        this.height = height;
		this.station = station;
		this.isAutoScale = isAutoScale;
        this.uom = uom;
	}

	public TimeseriesProperties copy() {
		TimeseriesProperties result = new TimeseriesProperties(this.timeseries, this.station, 
		                                                       this.width, this.height, this.uom, 
		                                                       this.isAutoScale);
		result.setAxisData(this.axis); // XXX this is not a deep copy! => CBR
		TimeseriesRenderingOptions options = new TimeseriesRenderingOptions();
		options.setColor(renderingOptions.getColor());
		options.setLineWidth(renderingOptions.getLineWidth());
		result.setRenderingOptions(options);
		result.setLanguage(this.language);
		result.setLineStyle(this.lineStyle);
		result.setMetadataUrl(this.metadataUrl);
		result.setOpacity(this.opacity);
		result.setScaledToZero(this.isScaledToZero);
		result.setShowYAxis(this.isYAxisVisible);
		result.setUnitOfMeasure(this.uom);
		result.setSeriesType(this.seriesType);
		return result;
	}

	public boolean showYAxis() {
		return this.isYAxisVisible;
	}

	public void setShowYAxis(boolean showYAxis) {
		this.isYAxisVisible = showYAxis;
	}

	public String getGraphStyle() {
	    // XXX clear difference to lineStyle!
		return this.seriesType;
	}

	public void setSeriesType(String sumLine) {
		this.seriesType = sumLine;
	}

	public Axis getAxis() {
		return this.axis;
	}

	public double getAxisLowerBound() {
		if (this.axis == null) {
			return 0d;
		}
		return this.axis.getLowerBound();
	}

	public double getAxisUpperBound() {
		if (this.axis == null) {
			return 0d;
		}
		return this.axis.getUpperBound();
	}

	public void setAxisData(Axis a) {
		if (a == null) {
			return;
		}
		if (this.axis == null) {
			this.axis = new Axis(a.getUpperBound(), a.getLowerBound());
		} else {
			this.axis.setLowerBound(a.getLowerBound());
			this.axis.setUpperBound(a.getUpperBound());
		}
		this.axis.setMaxY(a.getMaxY());
		this.axis.setMinY(a.getMinY());
	}

	public String getMetadataUrl() {
		return this.metadataUrl;
	}

	public boolean isAutoScale() {
		return this.isAutoScale;
	}

	public void setAutoScale(boolean autoScale) {
		this.isAutoScale = autoScale;
	}

	public void setMetadataUrl(String metadataUrl) {
		this.metadataUrl = metadataUrl;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setLanguage(String language) {
	    this.language = language;
	}
	
	public void setTimeseries(SosTimeseries timeseries) {
	    this.timeseries = timeseries;
	}
	
	public SosTimeseries getTimeseries() {
	    return timeseries;
	}

	public void setStation(Station station) {
	    this.station = station;
	}
	
	public Station getStation() {
	    return station;
	}


	public String getStationName() {
		return station.getLabel();
	}

	public String getServiceUrl() {
		return timeseries.getServiceUrl();
	}
	
	public String getLanguage() {
	    return this.language;
	}

	/**
	 * @deprecated user {@link #getTimeseries()}
	 */
	public String getOffering() {
		return timeseries.getOfferingId();
	}
	
	/**
     * @deprecated user {@link #getTimeseries()}
     */
	public String getFeature() {
		return timeseries.getFeatureId();
	}

	/**
     * @deprecated user {@link #getTimeseries()}
     */
	public String getProcedure() {
		return timeseries.getProcedureId();
	}

	/**
     * @deprecated user {@link #getTimeseries()}
     */
	public String getPhenomenon() {
		return timeseries.getPhenomenonId();
	}

	public String getTimeseriesId() {
		return timeseries.getTimeseriesId();
	}

	public String getLineStyle() {
		return this.lineStyle;
	}

	public void setLineStyle(String lineStyle) {
		this.lineStyle = lineStyle;
	}

    /**
     * @deprecated use {@link #getRenderingOptions()}
     */
    public String getHexColor() {
        return renderingOptions.getColor();
    }

    /**
     * @deprecated use {@link #setRenderingOptions(TimeseriesRenderingOptions)}
     */
    public void setHexColor(String hexColor) {
        if (renderingOptions == null) {
            renderingOptions = new TimeseriesRenderingOptions();
        }
        renderingOptions.setColor(hexColor);
    }

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public int getLineWidth() {
		return renderingOptions.getLineWidth();
	}

	/**
	 * @deprecated use {@link #setRenderingOptions(TimeseriesRenderingOptions)}
	 */
	@Deprecated
	public void setLineWidth(int lineWidth) {
	    if (renderingOptions == null) {
            renderingOptions = new TimeseriesRenderingOptions();
        }
	    renderingOptions.setLineWidth(lineWidth);
	}
	
	public TimeseriesRenderingOptions getRenderingOptions() {
	    return renderingOptions;
	}
	
	/**
	 * @param renderingOptions the timeseries' rendering options to set.
	 */
	public void setRenderingOptions(TimeseriesRenderingOptions renderingOptions) {
	    if (renderingOptions != null) {
	        this.renderingOptions = renderingOptions;
        }
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public String getUnitOfMeasure() {
		return this.uom;
	}

	public void setUnitOfMeasure(String uom) {
		this.uom = uom;
	}

	public double getLat() {
		return this.station.getLocation().getY();
	}

	public double getLon() {
		return this.station.getLocation().getX();
	}

    public boolean isStationInitialized() {
        return station != null && station.getLocation() != null;
    }

	public void setOpacity(double opacityPercentage) {
		this.opacity = opacityPercentage;
	}

	public double getOpacity() {
		return this.opacity;
	}

	public void setScaledToZero(boolean zeroScaled) {
		this.isScaledToZero = zeroScaled;
	}

	public boolean isZeroScaled() {
		return this.isScaledToZero;
	}

	public void addRefValue(ReferenceValue v) {
		this.refvalues.put(v.getId(), v);
	}

	public ReferenceValue getRefValue(String s) {
		return this.refvalues.get(s);
	}

	public Set<String> getReferenceValues() {
		return this.refvalues.keySet();
	}

	public HashMap<String, ReferenceValue> getRefvalues() {
        return refvalues;
    }

    public void addAllRefValues(HashMap<String, ReferenceValue> refvalues2) {
		this.refvalues.putAll(refvalues2);
	}

	public boolean isSetAxis() {
		return this.setAxis;
	}

	public void setSetAxis(boolean setAxis) {
		this.setAxis = setAxis;
	}

	public String getOffFoiProcPhenCombination() {
		StringBuilder sb = new StringBuilder();
		sb.append("TimeSeriesProperties: [");
		sb.append("Offering: ").append(timeseries.getOfferingId()).append(", ");
		sb.append("Feature: ").append(timeseries.getFeatureId()).append(", ");
		sb.append("Procedure: ").append(timeseries.getProcedureId()).append(", ");
		sb.append("Phenomenon: ").append(timeseries.getPhenomenonId()).append("]");
		return sb.toString();
	}

}
