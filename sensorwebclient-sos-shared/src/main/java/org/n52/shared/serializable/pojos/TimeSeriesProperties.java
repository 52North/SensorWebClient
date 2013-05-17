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

import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.Station;

public class TimeSeriesProperties implements Serializable {

	private static final long serialVersionUID = 1813366884589984223L;

	private String sosUrl;
	
	private Station station;

	private Offering off;

	private Feature foi;

	private Procedure proc;

	private Phenomenon phen;
	
	private int height;

	private int width;

	private String hexColor;

	private String label;
	
	private String language;

	private HashMap<String, ReferenceValue> refvalues = new HashMap<String, ReferenceValue>();

	/**
	 * The series type. 1 = levelline 2 = sumline
	 */
	private String seriesType = "1";

	private String lineStyle = "1";

	private int lineWidth = 2; // default

	private String uom;

	private String stationName;

	private String timeSeriesId;

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
	private TimeSeriesProperties() {
		// do nothing
	}

	public TimeSeriesProperties(String sosUrl, Station station, Offering offering,
			Feature foi, Procedure procedure, Phenomenon phenomenon,
			int width, int height) {
		this(sosUrl, station, offering, foi, procedure, phenomenon, width, height, "", true);
	}

	public TimeSeriesProperties(String sosUrl, Station station, Offering offering,
			Feature foi, Procedure procedure, Phenomenon phenomenon,
			int width, int height, String uom, boolean isAutoScale) {
		this.sosUrl = sosUrl;
		this.station = station;
		this.off = offering;
		this.foi = foi;
		this.proc = procedure;
		this.phen = phenomenon;
		this.width = width;
		this.height = height;
		this.uom = uom;
		this.isAutoScale = isAutoScale;
	}

	/**
	 * @return A deep copy of this Object
	 */
	public TimeSeriesProperties copy() {
		TimeSeriesProperties result = new TimeSeriesProperties(this.sosUrl, this.station,
				this.off, this.foi, this.proc, this.phen, this.width,
				this.height, this.uom, this.isAutoScale);
		result.setAxisData(this.axis); // XXX this is not a deep copy! => CBR
		result.setHexColor(this.hexColor);
		result.setLabel(this.label);
		result.setLanguage(this.language);
		result.setLineStyle(this.lineStyle);
		result.setLineWidth(this.lineWidth);
		result.setMetadataUrl(this.metadataUrl);
		result.setOpacity(this.opacity);
		result.setScaledToZero(this.isScaledToZero);
		result.setShowYAxis(this.isYAxisVisible);
		result.setStationName(this.stationName);
		result.setTsID(this.timeSeriesId);
		result.setUOM(this.uom);
		result.setSeriesType(this.seriesType);

		return result;
	}

	/**
	 * @return boolean
	 */
	public boolean showYAxis() {
		return this.isYAxisVisible;
	}

	/**
	 * @param showYAxis
	 */
	public void setShowYAxis(boolean showYAxis) {
		this.isYAxisVisible = showYAxis;
	}

	public String getGraphStyle() {
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

	public void setSosUrl(String sosUrl) {
		this.sosUrl = sosUrl;
	}
	
	public void setStation(Station station) {
	    this.station = station;
	}
	
	public Station getStation() {
	    return station;
	}

	public void setOff(Offering off) {
		this.off = off;
	}

	public void setFoi(Feature foi) {
		this.foi = foi;
	}

	public void setProc(Procedure proc) {
		this.proc = proc;
	}

	public void setPhen(Phenomenon phen) {
		this.phen = phen;
	}

	public String getUom() {
		return this.uom;
	}

	public String getStationName() {
		if (this.stationName == null || this.stationName.isEmpty()) {
			return this.foi.getLabel();
		}
		return this.stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getSosUrl() {
		return this.sosUrl;
	}
	
	public String getLanguage() {
	    return this.language;
	}

	/**
	 * Gets the off.
	 * 
	 * @return the off
	 */
	public Offering getOffering() {
		return this.off;
	}

	/**
	 * Gets the foi.
	 * 
	 * @return the foi
	 */
	public Feature getFoi() {
		return this.foi;
	}

	/**
	 * Gets the proc.
	 * 
	 * @return the proc
	 */
	public Procedure getProcedure() {
		return this.proc;
	}

	/**
	 * Gets the phen.
	 * 
	 * @return the phen
	 */
	public Phenomenon getPhenomenon() {
		return this.phen;
	}

	/**
	 * Gets the hex color.
	 * 
	 * @return the hex color
	 */
	public String getHexColor() {
		return this.hexColor;
	}

	/**
	 * Gets the ts parameterId.
	 * 
	 * @return the ts parameterId
	 */
	public String getTsID() {
		return this.timeSeriesId;
	}

	/**
	 * Sets the ts parameterId.
	 * 
	 * @param tsID
	 *            the new ts parameterId
	 */
	public void setTsID(String tsID) {
		this.timeSeriesId = tsID;
	}

	/**
	 * Sets the hex color.
	 * 
	 * @param hexColor
	 *            the new hex color
	 */
	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets the line style.
	 * 
	 * @return the line style
	 */
	public String getLineStyle() {
		return this.lineStyle;
	}

	/**
	 * Sets the line style.
	 * 
	 * @param lineStyle
	 *            the new line style
	 */
	public void setLineStyle(String lineStyle) {
		this.lineStyle = lineStyle;
	}

	/**
	 * Gets the line width.
	 * 
	 * @return the line width
	 */
	public int getLineWidth() {
		return this.lineWidth;
	}
	
	/**
	 * Sets the line width.
	 * @param lineWidth
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * Gets the height.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}

	// public String getStartDateString() {
	// // String ret = "";
	// // //for getting the +/- encoding right
	// // if (DateTimeFormat.getFormat("Z").format(startDate).indexOf("+") >
	// // -1) {
	// // ret =
	// // DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss").format(startDate) +
	// // "%2B";
	// // } else {
	// // ret =
	// // DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss").format(startDate) +
	// // "-";
	// // }
	// //
	// // String tz =
	// // DateTimeFormat.getFormat("Z").format(startDate).substring(1);
	// // return ret + tz;
	// return DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss").format(
	// TimeManager.getInst().getBegin()) +
	// DateTimeFormat.getFormat("Z").format(TimeManager.getInst().getBegin()).substring(0,
	// 3)
	// + ":" +
	// DateTimeFormat.getFormat("Z").format(TimeManager.getInst().getBegin()).substring(3,
	// 5);
	// }

	// public String getEndDateString() {
	// // //for getting the +/- encoding right
	// // if (DateTimeFormat.getFormat("Z").format(endDate).indexOf("+") > -1)
	// // {
	// // ret =
	// // DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss").format(endDate) +
	// // "%2B";
	// // } else {
	// // ret =
	// // DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss").format(endDate) +
	// // "-";
	// // }
	// //
	// // String tz =
	// // DateTimeFormat.getFormat("Z").format(endDate).substring(1);
	// // return ret + tz;
	// return DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss").format(
	// TimeManager.getInst().getEnd()) +
	// DateTimeFormat.getFormat("Z").format(TimeManager.getInst().getEnd()).substring(0,
	// 3)
	// + ":" +
	// DateTimeFormat.getFormat("Z").format(TimeManager.getInst().getEnd()).substring(3,
	// 5);
	// }

	/**
	 * Gets the uOM.
	 * 
	 * @return the uOM
	 */
	public String getUnitOfMeasure() {
		return this.uom;
	}

	/**
	 * Sets the uOM.
	 * 
	 * @param uom
	 *            the new uOM
	 */
	public void setUOM(String uom) {
		this.uom = uom;
		this.phen.setUnitOfMeasure(uom);
	}

	public double getLat() {
		return this.station.getLocation().getNorthing();
	}

	public double getLon() {
		return this.station.getLocation().getEasting();
	}

    /**
     * Gets the srs.
     * 
     * @return the srs
     */
    public String getSrs() {
        return this.station.getLocation().getSrs();
    }

    public void setSrs(String srs) {
        this.station.getLocation().setSrs(srs);
    }

	/**
	 * Sets the opacity.
	 * 
	 * @param opacityPercentage
	 *            the new opacity
	 */
	public void setOpacity(double opacityPercentage) {
		this.opacity = opacityPercentage;
	}

	/**
	 * Gets the opacity.
	 * 
	 * @return the opacity
	 */
	public double getOpacity() {
		return this.opacity;
	}

	/**
	 * Sets the scaled to zero.
	 * 
	 * @param zeroScaled
	 *            the new scaled to zero
	 */
	public void setScaledToZero(boolean zeroScaled) {
		this.isScaledToZero = zeroScaled;
	}

	/**
	 * Checks if is zero scaled.
	 * 
	 * @return true, if is zero scaled
	 */
	public boolean isZeroScaled() {
		return this.isScaledToZero;
	}

	/**
	 * Adds the ref value.
	 * 
	 * @param s
	 *            the s
	 * @param v
	 *            the v
	 */
	public void addRefValue(ReferenceValue v) {
		this.refvalues.put(v.getID(), v);
	}

	/**
	 * Gets the ref value.
	 * 
	 * @param s
	 *            the s
	 * @return the ref value
	 */
	public ReferenceValue getRefValue(String s) {
		return this.refvalues.get(s);
	}

	/**
	 * Gets the ref values.
	 * 
	 * @return the ref values
	 */
	public Set<String> getReferenceValues() {
		return this.refvalues.keySet();
	}

	/**
	 * Adds the all ref values.
	 * 
	 * @param refvalues2
	 *            the refvalues2
	 */
	public void addAllRefValues(HashMap<String, ReferenceValue> refvalues2) {

		this.refvalues.putAll(refvalues2);

	}

	/**
	 * @return the setAxis
	 */
	public boolean isSetAxis() {
		return this.setAxis;
	}

	/**
	 * @param setAxis
	 *            the setAxis to set
	 */
	public void setSetAxis(boolean setAxis) {
		this.setAxis = setAxis;
	}

	public String getOffFoiProcPhenCombination() {
		StringBuilder sb = new StringBuilder();
		sb.append("TimeSeriesProperties: [");
		sb.append("Offering: ").append(off.getId()).append(", ");
		sb.append("Feature: ").append(foi.getId()).append(", ");
		sb.append("Procedure: ").append(proc.getId()).append(", ");
		sb.append("Phenomenon: ").append(phen.getId()).append("]");
		return sb.toString();
	}

}