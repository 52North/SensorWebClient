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

import org.gwtopenmaps.openlayers.client.Icon;
import org.gwtopenmaps.openlayers.client.Marker;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.event.EventHandler;
import org.gwtopenmaps.openlayers.client.event.EventObject;
import org.n52.shared.serializable.pojos.sos.Station;

public class InfoMarker extends Marker {

    private static final String MARKER_IMG_UNSELECTED = "img/icons/marker_unsel.png";
    
    private static final String MARKER_IMG_SELECTED = "img/icons/marker_sel.png";
    
    private static final String MARKER_IMG_HOVER = "img/icons/marker_hover.png";

    private Station station = new Station();

	private MapController controller;
	
	private boolean selected;

    private String id;

    public static InfoMarker createInfoMarker(Station station, MapController controller) {
		double lng = station.getLon();
		double lat = station.getLat();
		String srs = station.getSrs();
		String proj = controller.getMapProjection();
		final Coordinate coords = new Coordinate(lng, lat, proj, srs);
    	return new InfoMarker(coords, station, controller);
    }
    
    private InfoMarker(Coordinate coords, Station station, MapController controller) {
        super(coords);
        this.controller = controller;
        this.id = "infomarker";
    	setStation(station);
        setIcon(new Icon(MARKER_IMG_UNSELECTED, new Size(20, 20)));
    	final InfoMarker marker = this;
		getEvents().register("click", marker, getClickHandler());
//		getEvents().register("zoomend", marker, getZoomEndHandler());
//		getJSObject().setProperty("class", "sensorweb_client_marker");
    }

    // TODO resize marker dependend on zoom level 
//	private EventHandler getZoomEndHandler() {
//		return new EventHandler() {
//			@Override
//			public void onHandle(EventObject event) {
//				GWT.log("zoom event on marker " + parameterId);
//			}
//		};
//	}

	public void registerHoverHandler() {
		getEvents().register("mouseover", this, getMouseOverHandler());
		getEvents().register("mouseout", this, getMouseOutHandler());
    }
    
	private EventHandler getClickHandler() {
    	return new EventHandler() {
			@Override
			public void onHandle(EventObject event) {
				// TODO raise z-index to be the top most marker
				controller.handleInfoMarkerClicked(InfoMarker.this);
			}
		};
	}

    private EventHandler getMouseOverHandler() {
    	return new EventHandler() {
			@Override
			public void onHandle(EventObject event) {
				if (!selected) {
					// TODO raise z-index to be the top most marker
					InfoMarker.this.highlight();
				}
			}
		};
	}
    
    private EventHandler getMouseOutHandler() {
    	return new EventHandler() {
			@Override
			public void onHandle(EventObject event) {
				if (!selected) {
					// TODO decrease z-index to default
					InfoMarker.this.unlight();
				}
			}
		};
	}
    
    public void highlight() {
    	getIcon().setUrl(MARKER_IMG_HOVER);
    }
    
    public void unlight() {
    	getIcon().setUrl(MARKER_IMG_UNSELECTED);
    }

	public void select() {
		selected = true;
        getIcon().setUrl(MARKER_IMG_SELECTED);
    }
    
    public void deselect() {
    	selected = false;
        getIcon().setUrl(MARKER_IMG_UNSELECTED);
    }

    public String getId() {
        return id;
    }

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

}