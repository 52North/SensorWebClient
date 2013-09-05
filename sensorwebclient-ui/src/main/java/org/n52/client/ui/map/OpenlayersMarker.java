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

package org.n52.client.ui.map;

import static org.n52.client.sos.ctrl.SosDataManager.getDataManager;

import java.util.ArrayList;

import org.gwtopenmaps.openlayers.client.Icon;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.event.EventHandler;
import org.gwtopenmaps.openlayers.client.event.EventObject;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.n52.client.sos.ctrl.SosDataManager;
import org.n52.client.sos.legend.Timeseries;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.shared.serializable.pojos.sos.ObservationParameter.EncodeType;

/**
 * The Class OpenlayersMarker.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class OpenlayersMarker extends Marker {

    private String infoTxt;

    private Timeseries timeseries;

    private ArrayList<String> timeseriesIds = new ArrayList<String>();

    private Popup popup;

    private Coordinate coords;

    private final static String UNMARKED_IMG_RELPATH = "img/icons/marker_unsel.png";
    private final static String MARKED_IMG_RELPATH = "img/icons/marker_sel.png";

    public OpenlayersMarker(Coordinate coords, Timeseries ts) {
        super(coords);
        this.timeseries = ts;
        this.coords = coords;
        this.setIcon(new Icon(UNMARKED_IMG_RELPATH, new Size(25, 25)));
        init();
    }

    public void mark() {
        updateMarker(MARKED_IMG_RELPATH);
    }

    public void unmark() {
        updateMarker(UNMARKED_IMG_RELPATH);
    }
    
    private void updateMarker(String imgPath) {
        if (getIcon() != null) {
            this.getIcon().setUrl(imgPath);
        }
        else {
            this.setIcon(new Icon(imgPath, new Size(21, 25)));
        }
    }

    private void init() {

        this.timeseriesIds.add(this.timeseries.getId());
        this.infoTxt = getInfoTxt();

    }

    public void createInfoPopup(final Map map) {
        EventHandler popupHandler = new EventHandler() {
            @Override
            public void onHandle(EventObject eventObject) {

                if (OpenlayersMarker.this.popup != null) {
                    map.removePopup(OpenlayersMarker.this.popup);
                }
                Pixel pixel = new Pixel(0, 0);
                Size size = new Size(300, 200);
                FramedCloud frame = new FramedCloud("marker-info",
                                                    OpenlayersMarker.this.coords,
                                                    size,
                                                    OpenlayersMarker.this.infoTxt,
                                                    new Icon("", new Size(0, 0), pixel),
                                                    true);
                OpenlayersMarker.this.setPopup(frame);
                map.addPopup(OpenlayersMarker.this.popup);
            }
        };
        //getEvents().register("click", this, popupHandler);
    }

    public String getInfoTxt() {
        TimeseriesProperties properties = timeseries.getProperties();
        TimeseriesParametersLookup lookup = getParameterLookup(properties);
        if (infoTxt == null || infoTxt.length() == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Offering: ");
            sb.append(lookup.getOffering(properties.getOffering()).getLabel(EncodeType.HTML));
            sb.append("</br>Feature: ");
            sb.append(lookup.getFeature(properties.getFeature()).getLabel());
            sb.append("</br>Procedure: ");
            sb.append(lookup.getProcedure(properties.getProcedure()).getLabel());
            sb.append("</br>Phenomenon: ");
            sb.append(lookup.getPhenomenon(properties.getPhenomenon()).getLabel());
            sb.append("[" + properties.getUnitOfMeasure() + "]</br>");
            infoTxt = sb.toString();
        }
        return infoTxt;
    }

    private TimeseriesParametersLookup getParameterLookup(TimeseriesProperties properties) {
        SOSMetadata metadata = getDataManager().getServiceMetadata(properties.getServiceUrl());
        return metadata.getTimeseriesParametersLookup();
    }

    public boolean containsTS(String id) {
        for (String identifier : timeseriesIds) {
            if (identifier.equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the to info txt.
     * 
     * @param txt
     *        void
     */
    public void addToInfoTxt(String txt) {
        this.infoTxt += txt;
    }

    /**
     * Gets all available timeseries ids.
     * 
     * @return availableTimeseriesIds
     */
    public ArrayList<String> availableTimeseriesIds() {
        return this.timeseriesIds;
    }

    /**
     * Adds the ts parameterId to array.
     * 
     * @param parameterId
     *        timeSeries ID
     */
    public void addTimeseriesId(String id) {
        if ( !this.timeseriesIds.contains(id)) {
            this.timeseriesIds.add(id);
        }
    }

    /**
     * Sets the popup.
     * 
     * @param popup
     *        the popup to set
     */
    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    /**
     * Gets the lonlat.
     * 
     * @return the lonlat
     */
    public Coordinate getCoords() {
        return this.coords;
    }

}