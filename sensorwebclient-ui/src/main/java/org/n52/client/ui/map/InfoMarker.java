/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ui.map;

import static org.n52.client.ui.map.Coordinate.createProjectedCoordinate;

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

    private MapController controller;
    
    private Station station;

    private String id;

    private boolean selected;

    public static InfoMarker createInfoMarker(Station station, MapController controller) {
		String mapProjection = controller.getMapProjection();
		final Coordinate coords = createProjectedCoordinate(station.getLocation(), mapProjection);
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
//		getJSObject().setProperty("class", "n52_sensorweb_client_marker");
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