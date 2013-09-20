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
import static org.n52.client.ui.map.Coordinate.createProjectedCoordinate;
import static org.n52.client.ui.map.OpenLayersMapWrapper.currentMapProjection;

import java.util.HashMap;

import org.n52.client.sos.DataparsingException;
import org.n52.client.ui.legend.LegendData;
import org.n52.client.ui.legend.LegendElement;
import org.n52.client.ui.legend.LegendEntryTimeSeries;
import org.n52.client.ui.map.Coordinate;
import org.n52.shared.serializable.pojos.Axis;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

public class TimeseriesLegendData implements LegendData {

	public static final String GRAPH_STYLE_GAUGELINE = "1";

	public static final String GRAPH_STYLE_SUMLINE = "2";

	public static final String GRAPH_STYLE_DEFAULT = GRAPH_STYLE_GAUGELINE;

	private String id;

	private TimeseriesProperties properties;

	private int ordering;

	private LegendElement legendElement;

	private HashMap<Long, Double> data;

	private long firstValueDate = 0;

	private long lastValueDate = 0;

	private String firstValue = "";

	private String lastValue = "";

	private Coordinate coords;

	public TimeseriesLegendData(String id, TimeseriesProperties properties) {
		this.id = id;
		this.properties = properties;
		this.data = new HashMap<Long, Double>();
		init();
	}

	private void init() {
        this.ordering = 0;
		this.legendElement = new LegendEntryTimeSeries(this, "100%", "30");
	}

	public void addData(HashMap<Long, Double> datamap) throws DataparsingException {
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

	public HashMap<Long, Double> getData() {
		return this.data;
	}

	public void setAxisData(Axis a) {
		this.properties.setAxisData(a);
	}

	public HashMap<Long, Double> getData(long begin, long end) {

		HashMap<Long, Double> result = new HashMap<Long, Double>();
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

	public String getTimeSeriesLabel() {
	    TimeseriesParametersLookup lookup = getParameterLookup(properties);
		return lookup.getFeature(properties.getFeature()).getLabel();
	}

	public String getFeatureId() {
		return properties.getTimeseries().getFeatureId();
	}

	public String getPhenomenonId() {
		return properties.getPhenomenon();
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
		return properties.getTimeseries().getOfferingId();
	}

	public String getSosUrl() {
		return properties.getServiceUrl();
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

	public LegendElement getLegendElement() {
		return this.legendElement;
	}

	public void setLegendElement(LegendElement elem) {
		this.legendElement = elem;
	}

	public void setProperties(TimeseriesProperties props) {
		this.coords = getCoords(props.getStation());
		this.properties = props;
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
		if (!properties.isStationInitialized()) {
			// coords not available yet (eg client started from permalink)
			return null;
		}
		return createProjectedCoordinate(station.getLocation(), currentMapProjection);
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

	public void setScaleToZero(boolean zeroScaled) {
		this.properties.setScaledToZero(zeroScaled);
	}

	public boolean isScaledToZero() {
		return this.properties.isZeroScaled();
	}

	public void popAxis() {
		this.properties.getAxis().popAxis();
	}

	public boolean isAutoScale() {
		return this.properties.isAutoScale();
	}

	public void setAutoScale(boolean b) {
		this.properties.setAutoScale(b);
	}

	public void setHasData(boolean hasData) {
		this.properties.setHasData(hasData);
	}

}