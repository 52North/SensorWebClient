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

import org.n52.shared.serializable.pojos.sos.ObservationParameter.DecodeType;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class TimeseriesProperties implements Serializable {

	private static final long serialVersionUID = 1813366884589984223L;

	private Station station;

    private SosTimeseries timeseries;

	private int height;

	private int width;

	private String label;
	
	private String language;

	private HashMap<String, ReferenceValue> refvalues = new HashMap<String, ReferenceValue>();

    private TimeseriesRenderingOptions renderingOptions = new TimeseriesRenderingOptions();

	/**
	 * Units of measurement
	 */
	private String uom;

	private String stationName;
	
	private String stationLongname;

	private String metadataUrl = "";

	private Axis axis = null;

	private boolean setAxis = true;

	private boolean isYAxisVisible = true;

	private boolean isHasData = false;
	
	private boolean jumpToLatestTimeSeriesDate = false;

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

	/**
	 * Use scaleType instead of isAutoScale
	 * @param timeseries
	 * @param station
	 * @param width
	 * @param height
	 * @param uom
	 * @param isAutoScale
	 * @deprecated
	 */
	public TimeseriesProperties(SosTimeseries timeseries, Station station,
			int width, int height, String uom, boolean isAutoScale) {
	    this.timeseries = timeseries;
        this.station = station;
        this.width = width;
        this.height = height;
		this.station = station;
		this.setAutoScale(isAutoScale);
        this.uom = uom;
	}

	public TimeseriesProperties(SosTimeseries timeseries, Station station,
			int width, int height, String uom, Scale scale) {
	    this.timeseries = timeseries;
        this.station = station;
        this.width = width;
        this.height = height;
		this.station = station;
		this.setScale(scale);
        this.uom = uom;
	}

	private TimeseriesProperties(SosTimeseries timeseries, Station station,
			int width, int height, String uom) {
	    this.timeseries = timeseries;
        this.station = station;
        this.width = width;
        this.height = height;
		this.station = station;
        this.uom = uom;
	}
	
	public TimeseriesProperties copy() {
		TimeseriesProperties result = new TimeseriesProperties(this.timeseries, this.station, 
		                                                       this.width, this.height, this.uom);
		result.setAxisData(this.axis);
		result.setRenderingOptions(renderingOptions.copy());
		result.setLabel(this.label);
		result.setLanguage(this.language);
		result.setMetadataUrl(this.metadataUrl);
		result.setShowYAxis(this.isYAxisVisible);
		result.setStationName(this.stationName);
		result.setStationLongname(this.stationLongname);
		result.setUnitOfMeasure(this.uom);
		return result;
	}

	public boolean showYAxis() {
		return this.isYAxisVisible;
	}

	public void setShowYAxis(boolean showYAxis) {
		this.isYAxisVisible = showYAxis;
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public String getGraphStyle() {
	    // XXX clear difference to lineStyle!
		return this.renderingOptions.getGraphStyle();
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public void setSeriesType(String sumLine) {
		this.renderingOptions.setGraphStyle(sumLine);
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

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public void setScale( Scale scale ){
		this.renderingOptions.setScale(Scale.copy(scale));
	}
	
	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public void setScale( Scale.Type type ){
		this.renderingOptions.setScale(new Scale(type));
	}
	
	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public Scale getScale(){
		return this.renderingOptions.getScale();
	}
	
	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public boolean isAutoScale() {
		return this.renderingOptions.getScale().isAuto();
	}

	/**
	 * If true sets scaleType to AUTO else to ZERO
	 * (for backwards compatibility when only ZERO and AUTO existed)
	 * Use setScaleType(ScaleType) 
	 * @param zeroScaled
	 * @deprecated
	 */
	public void setAutoScale(boolean autoScale) {
		this.renderingOptions.getScale().setAuto(autoScale);
	}

	/**
	 * If true sets scaleType to ZERO else to AUTO
	 * (for backwards compatibility when only ZERO and AUTO existed)
	 * Use setScaleType(ScaleType) 
	 * @param zeroScaled
	 * @deprecated
	 */
	public void setScaledToZero(boolean zeroScaled) {
		this.renderingOptions.getScale().setZero(zeroScaled);
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public boolean isZeroScaled() {
		return this.renderingOptions.getScale().isZero();
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public boolean isManualScaled() {
		return this.renderingOptions.getScale().isManual();
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

	public void setSosUrl(String sosUrl) {
	    if (timeseries == null) {
            timeseries = new SosTimeseries();
        }
		timeseries.setServiceUrl(sosUrl);
	}
	
	public void setStation(Station station) {
	    this.station = station;
	}
	
	public Station getStation() {
	    return station;
	}

	/**
	 * @deprecated use {@link #setTimeseries(SosTimeseries)}
	 */
	public void setOffering(String offering) {
		if (timeseries == null) {
            timeseries = new SosTimeseries();
        }
		timeseries.setOffering(offering);
	}

	/**
     * @deprecated use {@link #setTimeseries(SosTimeseries)}
     */
	public void setFeature(String feature) {
	    if (timeseries == null) {
            timeseries = new SosTimeseries();
        }
        timeseries.setFeature(feature);
	}

	/**
     * @deprecated use {@link #setTimeseries(SosTimeseries)}
     */
	public void setProcedure(String procedure) {
	    if (timeseries == null) {
            timeseries = new SosTimeseries();
        }
        timeseries.setProcedure(procedure);
	}

	/**
     * @deprecated use {@link #setTimeseries(SosTimeseries)}
     */
	public void setPhenomenon(String phenomenon) {
	    if (timeseries == null) {
            timeseries = new SosTimeseries();
        }
        timeseries.setPhenomenon(phenomenon);
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getStationLongname() {
		return stationLongname;
	}

	public void setStationLongname(String stationLongname) {
		this.stationLongname = stationLongname;
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
		return timeseries.getOffering();
	}
	
	/**
     * @deprecated user {@link #getTimeseries()}
     */
	public String getFeature() {
		return timeseries.getFeature();
	}

	/**
     * @deprecated user {@link #getTimeseries()}
     */
	public String getProcedure() {
		return timeseries.getProcedure();
	}

	/**
     * @deprecated user {@link #getTimeseries()}
     */
	public String getPhenomenon() {
		return getPhenomenon(DecodeType.ASCII);
	}

	public String getPhenomenon(DecodeType decodeType) {
		return timeseries.getPhenomenon(decodeType);
	}

	public String getTimeseriesId() {
		return timeseries.getTimeseriesId();
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public String getLineStyle() {
		return this.renderingOptions.getLineStyle();
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public void setLineStyle(String lineStyle) {
		this.renderingOptions.setLineStyle(lineStyle);
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
		return this.station.getLocation().getNorthing();
	}

	public double getLon() {
		return this.station.getLocation().getEasting();
	}

    public String getSrs() {
        if (station == null || station.getLocation() == null) {
            return null;
        }
        return station.getLocation().getSrs();
    }

    public void setSrs(String srs) {
        this.station.getLocation().setSrs(srs);
    }

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public void setOpacity(double opacityPercentage) {
		this.renderingOptions.setOpacity(opacityPercentage);
	}

	/**
	 * @deprecated use {@link #getRenderingOptions()}
	 */
	public double getOpacity() {
		return this.renderingOptions.getOpacity();
	}

	public void addRefValue(ReferenceValue v) {
		this.refvalues.put(v.getID(), v);
	}

	public ReferenceValue getRefValue(String s) {
		return this.refvalues.get(s);
	}

	public Set<String> getReferenceValues() {
		return this.refvalues.keySet();
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
		sb.append("Offering: ").append(timeseries.getOffering()).append(", ");
		sb.append("Feature: ").append(timeseries.getFeature()).append(", ");
		sb.append("Procedure: ").append(timeseries.getProcedure()).append(", ");
		sb.append("Phenomenon: ").append(timeseries.getPhenomenon()).append("]");
		return sb.toString();
	}

	public boolean isJumpToLatestTimeSeriesDate() {
		return jumpToLatestTimeSeriesDate;
	}

	public void setJumpToLatestTimeSeriesDate(boolean jumpToLatestTimeSeriesDate) {
		this.jumpToLatestTimeSeriesDate = jumpToLatestTimeSeriesDate;
	}
	
	public boolean isTypeZdm(){
		return timeseries.isTypeZdm();
	}
}