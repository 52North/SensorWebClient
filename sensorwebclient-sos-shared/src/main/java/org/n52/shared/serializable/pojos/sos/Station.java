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
package org.n52.shared.serializable.pojos.sos;

import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.util.ArrayList;
import org.n52.io.geojson.old.GeojsonPoint;
import static org.n52.io.geojson.old.GeojsonPoint.createWithCoordinates;
import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashGenerator;

/**
 * A {@link Station} represents a location where timeseries data is observed.
 */
public class Station implements Serializable {

    private static final long serialVersionUID = 5016550440955260625L;

    private ArrayList<SosTimeseries> observingTimeseries;

    private Point location;

    private Feature feature;

    private String label;

    Station() {
        // keep serializable
    }

    public Station(Feature feature) {
        this.observingTimeseries = new ArrayList<SosTimeseries>();
        this.label = SosTimeseries.createLabelFromUri(feature.getFeatureId());
        this.feature = feature;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Feature getFeature() {
        return feature;
    }

    /**
     * @param location
     *        a spatially referenced location in lon/lat.
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * @return the spatially referenced location in lon/lat.
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Creates a GeoJSON representation of the station's location. The coordiantes' axes ordering is left as
     * is. Per default this should be the lon/lat oriented CRS:84 reference system.
     *
     * @return the location as GeoJSON.
     */
    // TODO still need this converting method?
    public GeojsonPoint asGeoJSON() {
        Double[] coordinates = new Double[] {location.getX(), location.getY()};
        GeojsonPoint geojson = createWithCoordinates(coordinates);
        return geojson;
    }

    public void addTimeseries(SosTimeseries timeseries) {
        observingTimeseries.add(timeseries);
    }

    public ArrayList<SosTimeseries> getObservedTimeseries() {
        return observingTimeseries;
    }

    public boolean contains(SosTimeseries timeseries) {
        return observingTimeseries.contains(timeseries);
    }

    public boolean contains(String timeseriesId) {
        return getTimeseriesById(timeseriesId) != null;
    }

    public SosTimeseries getTimeseriesById(String timeseriesId) {
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.getTimeseriesId().equals(timeseriesId)) {
                return timeseries;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Station: [ ").append("\n");
        sb.append("\tId: ").append(feature).append("\n");
        sb.append("\tLocation: ").append(location).append("\n");
        sb.append("\t#Timeseries: ").append(observingTimeseries.size()).append(" ]\n");
        return sb.toString();
    }

    public boolean hasStationCategoryLabel(String categoryLabel) {
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.getCategory().getLabel().equals(categoryLabel)) {
                return true;
            }
        }
        return false;
    }

    public SosTimeseries getTimeseriesByCategory(String category) {
        for (SosTimeseries paramConst : observingTimeseries) {
            if (paramConst.getCategory().getLabel().equals(category)) {
                return paramConst;
            }
        }
        return null;
    }

    public boolean hasAtLeastOneParameterConstellation() {
        return observingTimeseries.size() > 0;
    }

    // @Override // gwt fails to compile
    public Station clone() {
        Station station = new Station(feature);
        station.setLocation(location);
        station.setObservingTimeseries(new ArrayList<SosTimeseries>(observingTimeseries));
        return station;
    }

    private void setObservingTimeseries(ArrayList<SosTimeseries> observingTimeseries) {
        this.observingTimeseries = observingTimeseries;
    }

    public String getGlobalId() {

        String[] parameters = null;
        if (location == null) {
            // TODO currently Stations are allowed to omit location. Passing it to
            // calculate the globalId will lead either to NPE or inconsistencies ..
            // logging not possible since it's a shared gwt class
            parameters = new String[] {feature.getFeatureId()};
        } else {
            parameters = new String[] {feature.getFeatureId(), location.toString()};
        }

        IdGenerator idGenerator = new MD5HashGenerator("sta_");
        return idGenerator.generate(parameters);
    }

}
