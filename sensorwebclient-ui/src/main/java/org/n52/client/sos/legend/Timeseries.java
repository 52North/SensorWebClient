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

package org.n52.client.sos.legend;

import static org.n52.client.sos.ctrl.SosDataManager.getDataManager;
import static org.n52.client.ui.map.OpenLayersMapWrapper.currentMapProjection;

import java.util.HashMap;

import org.n52.client.sos.DataparsingException;
import org.n52.client.ui.legend.LegendData;
import org.n52.client.ui.legend.LegendElement;
import org.n52.client.ui.legend.LegendEntryTimeSeries;
import org.n52.client.ui.map.Coordinate;
import org.n52.shared.serializable.pojos.Axis;
import org.n52.shared.serializable.pojos.Scale;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.shared.serializable.pojos.sos.ObservationParameter.DecodeType;

public class Timeseries implements LegendData {

	public static final String GRAPH_STYLE_GAUGELINE = "1";

	public static final String GRAPH_STYLE_SUMLINE = "2";

	public static final String GRAPH_STYLE_DEFAULT = GRAPH_STYLE_GAUGELINE;

	private String id;

	private TimeseriesProperties properties;

	private int ordering;

	private LegendElement legendElement;

	private HashMap<Long, String> data;

	private long firstValueDate = 0;

	private long lastValueDate = 0;

	private String firstValue = "";

	private String lastValue = "";

	private Coordinate coords;

	public Timeseries(String id, TimeseriesProperties properties) {
		this.id = id;
		this.properties = properties;
		this.data = new HashMap<Long, String>();
		init();
	}

	private void init() {
        this.ordering = 0;
		this.properties.setLabel(this.id);
		this.legendElement = new LegendEntryTimeSeries(this, "100%", "30");
	}

	public void addData(HashMap<Long, String> datamap) throws DataparsingException {
		try {
			// TODO do it this way, when enable caching
			// this.data.putAll(data);

			this.data = datamap;
			updateLegendElement();

			// create image entities for diagram

		} catch (Exception e) {
			throw new DataparsingException("Failed to insert incoming data", e);
		}
	}

	public HashMap<Long, String> getData() {
		return this.data;
	}

	public void setAxisData(Axis a) {
		this.properties.setAxisData(a);
	}

	public HashMap<Long, String> getData(long begin, long end) {

		HashMap<Long, String> result = new HashMap<Long, String>();
		if (this.data.isEmpty()) {
			return result; // TODO perform caching?
		}

		Long[] timestamps = this.data.keySet().toArray(
				new Long[this.data.keySet().size()]);
		for (Long timestamp : timestamps) {
			if (isBetween(begin, end, timestamp)) {
				result.put(timestamp, this.data.get(timestamp));
			}
		}
		return result;
	}

	private boolean isBetween(long begin, long end, Long value) {
		return value.compareTo(begin) >= 0 && value.compareTo(end) <= 0;
	}

	public Coordinate getCoords() {
		return this.coords;
	}

	/**
	 * The coordinate has to be set explicitly, due to missing spatial
	 * information at the time of instance creation. Use this method when
	 * spatial information for this {@link Timeseries} instance is available.
	 * 
	 * @param coords
	 *            the spatial information as coordinate.
	 */
	public void setCoords(Coordinate coords) {
		this.coords = coords;
	}

	public long getFirstValueDate() {
		return this.firstValueDate;
	}

	public void setFirstValueDate(long firstValueDate) {
		this.firstValueDate = firstValueDate;
	}

	public long getLastValueDate() {
		return this.lastValueDate;
	}

	public void setLastValueDate(long lastValueDate) {
		this.lastValueDate = lastValueDate;
	}

	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}

	public String getFirstValue() {
		return this.firstValue;
	}

	public String getGraphStyle() {
		String styleType = this.properties.getGraphStyle();
		return (styleType != null) ? styleType : GRAPH_STYLE_DEFAULT;
	}

	public void setSeriesType(String type) {
		this.properties.setSeriesType(type);
	}

	public String getLastValue() {
		return this.lastValue;
	}

	public int getOrdering() {
		return this.ordering;
	}

	public void setOrdering(int ordering) {
		this.ordering = ordering;
	}

	public String getId() {
		return this.id;
	}

	public String getColor() {
		return this.properties.getHexColor();
	}

	public void setColor(String hexColor) {
		this.properties.setHexColor(hexColor);
	}

	public String getMetadataUrl() {
		return this.properties.getMetadataUrl();
	}

	public void setMetadataUrl(String metadataUrl) {
		this.properties.setMetadataUrl(metadataUrl);
	}

	public TimeseriesProperties getProperties() {
		return this.properties;
	}

    private TimeseriesParametersLookup getParameterLookup(TimeseriesProperties properties) {
        SOSMetadata metadata = getDataManager().getServiceMetadata(properties.getServiceUrl());
        return metadata.getTimeseriesParametersLookup();
    }

	public String getTimeSeriesLabel( DecodeType decodeType ) {
	    TimeseriesParametersLookup lookup = getParameterLookup(properties);
		return lookup.getFeature(properties.getFeature()).getLabel(decodeType);
	}
	
	public String getTimeSeriesLabel() {
		return getTimeSeriesLabel(DecodeType.ASCII);
	}

	public String getFeatureId() {
		return properties.getFeature();
	}

	public String getPhenomenonId() {
		return getPhenomenonId(DecodeType.ASCII);
	}

	public String getPhenomenonId(DecodeType decodeType) {
		return properties.getPhenomenon(decodeType);
	}

	public String getProcedureId() {
		return properties.getProcedure();
	}

	public String getUnitOfMeasure() {
		return properties.getUnitOfMeasure();
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		properties.setUnitOfMeasure(unitOfMeasure);
	}

	public String getOfferingId() {
		return properties.getOffering();
	}

	public String getSosUrl() {
		return properties.getServiceUrl();
	}

	public double getLat() {
		return properties.getLat();
	}

	public double getLon() {
		return properties.getLon();
	}
	
	public void setStation(Station station) {
	    properties.setStation(station);
	}
	
	public Station getStation() {
	    return properties.getStation();
	}

	public void setStationName(String name) {
		this.properties.setStationName(name);
		this.properties.setLabel(name);
	}

	public String getStationName() {
		return properties.getStationName();
	}

	public void setLineStyle(String lineStyle) {
		this.properties.setLineStyle(lineStyle);
	}

	public String getLineStyle() {
		return this.properties.getLineStyle();
	}

	public void setLineWidth(int lineWidth) {
		this.properties.setLineWidth(lineWidth);
	}

	public int getLineWidth() {
		return this.properties.getLineWidth();
	}

	public String getSrs() {
		return this.properties.getSrs();
	}

	public LegendElement getLegendElement() {
		return this.legendElement;
	}

	public void setLegendElement(LegendElement elem) {
		this.legendElement = elem;
	}

	public void setProperties(TimeseriesProperties props) {
		this.properties = props;
		this.coords = getCoords(props.getStation());
		updateLegendElement();
	}

	/**
	 * Creates a coordinate from the spatial information given in the given
	 * {@link Procedure}. If no spatial information is available yet (eg the
	 * client was started from a permalink), <code>null</code> is returned.
	 * 
	 * @return the coordinate or <code>null</code> if spatial information was
	 *         not already available
	 */
	private Coordinate getCoords(Station station) {
		if (properties.getSrs() == null) {
			// coords not available yet (eg client started from permalink)
			return null;
		}
		return new Coordinate(station.getLocation(), currentMapProjection);
	}

	public boolean hasData() {
		return this.properties.hasData();
		// if (this.data != null) {
		// return !this.data.isEmpty();
		// }
		// return false;
	}

	private void updateLegendElement() {
		this.legendElement.setHasData(this.hasData());
		this.legendElement.update();

	}

	public void setOpacity(double opacityPercentage) {
		this.properties.setOpacity(opacityPercentage);
	}

	public double getOpacity() {
		return this.properties.getOpacity();
	}

	public void setScale(Scale scale){
		this.properties.setScale(scale);
	}
	
	public Scale getScale(){
		return this.properties.getScale();
	}
	
	/**
	 * 
	 * @param zeroScaled
	 * @deprecated
	 */
	public void setScaleToZero(boolean zeroScaled) {
		this.properties.setScaledToZero(zeroScaled);
	}

	/**
	 * @deprecated
	 */
	public boolean isScaledToZero() {
		return this.properties.isZeroScaled();
	}

	public void popAxis() {
		this.properties.getAxis().popAxis();
	}

	/**
	 * @deprecated
	 */
	public boolean isAutoScale() {
		return this.properties.isAutoScale();
	}

	/**
	 * @deprecated
	 */
	public void setAutoScale(boolean b) {
		this.properties.setAutoScale(b);
	}

	public void setHasData(boolean hasData) {
		this.properties.setHasData(hasData);
	}

}