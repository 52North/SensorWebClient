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
package org.n52.io.crs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.Station;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferencingFacadeTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencingFacadeTest.class);

    private AReferencingHelper referencingFacade;
    private BoundingBox bbox;

    @Before
    public void setUp() throws Exception {
        referencingFacade = AReferencingHelper.createEpsgStrictAxisOrder();
        EastingNorthing ll = new EastingNorthing(6.4, 51.9, "EPSG:4326");
        EastingNorthing ur = new EastingNorthing(8.9, 53.4, "EPSG:4326");
        bbox = new BoundingBox(ll, ur);
    }

    @Test
    public void testGetContainingStations() throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        Station stationWithin = getStationWithinBBox();
        Station stationOutside = getStationOutsideBBox();
        List<Station> stations = new ArrayList<Station>();
        stations.add(stationWithin);
        stations.add(stationOutside);
        List<Station> containingStations = referencingFacade.getContainingStations(bbox, stations);
        assertTrue(containingStations.contains(stationWithin));
        assertFalse(containingStations.contains(stationOutside));
    }

    @Test
    public void testIsStationContainedByBBox() throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        Station stationWithin = getStationWithinBBox();
        Station stationOutside = getStationOutsideBBox();
        assertTrue(referencingFacade.isStationContainedByBBox(bbox, stationWithin));
        assertFalse(referencingFacade.isStationContainedByBBox(bbox, stationOutside));
    }

    private Station getStationWithinBBox() {
        LOGGER.warn("Make Referencing Facade Test more flexible!");
        // TODO make random station within bbox
        // TODO add different epsg codes!
        Station stationWithin = new Station("test");
        stationWithin.setLocation(new EastingNorthing(7.0, 52.0, "EPSG:4326"));
        return stationWithin;
    }
    
    private Station getStationOutsideBBox() {
        LOGGER.warn("Make Referencing Facade Test more flexible!");
        // TODO make random station within bbox
        // TODO add different epsg codes!
        Station stationOutside = new Station("test");
        stationOutside.setLocation(new EastingNorthing(10.4, 52.0, "EPSG:4326"));
        return stationOutside;
    }

}
