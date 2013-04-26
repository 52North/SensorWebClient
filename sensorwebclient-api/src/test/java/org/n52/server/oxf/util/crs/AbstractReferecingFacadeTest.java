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
package org.n52.server.oxf.util.crs;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.Station;

public class AbstractReferecingFacadeTest {
    
    private AReferencingHelper referenceHelper;

    @Before
    public void setUp() throws Exception {
        referenceHelper = new NonCrsReferencingHelperSeam();
    }

    @Test
    public void testGetSrsIdFromEPSG() {
        assertValidCodeFromEpsg(4326, "4326");
        assertValidCodeFromEpsg(4326, "EPSG:4326");
        assertValidCodeFromEpsg(4326, "epsg:4326");
        assertValidCodeFromEpsg(4326, "epsg::4326");
        assertValidCodeFromEpsg(4326834, "ePsG:4326834");
        assertValidCodeFromEpsg(4326, "ogc:def:ref:epsg:4.7:4326");
    }
    
    private void assertValidCodeFromEpsg(int expected, String code) {
        assertEquals("Unexpected EPSG code!", expected, referenceHelper.getSrsIdFromEPSG(code));
    }
    
    @Test
    public void testExtractSRSCode() {
        String smallCaseUrn = "urn:ogc:def:crs:epsg::4326";
        String capitalCaseUrn= "URN:OGC:DEF:CRS:EPSG:3.5:4326";
        String mixedCaseUrn = "UrN:OfC:dEf:crs:EPSG::4323426";
        String capitalEpsgLink = "http://www.opengis.net/def/crs/EPSG/0/4324336";
        String smallCaseEpsgLink = "http://www.opengis.net/def/crs/epsg/0/4326";
        assertValidEpsgShortCut("EPSG:4326", smallCaseUrn);
        assertValidEpsgShortCut("EPSG:4326", capitalCaseUrn);
        assertValidEpsgShortCut("EPSG:4323426", mixedCaseUrn);
        assertValidEpsgShortCut("EPSG:4324336", capitalEpsgLink);
        assertValidEpsgShortCut("EPSG:4326", smallCaseEpsgLink);
    }

    private void assertValidEpsgShortCut(String expected, String epsgCode) {
        assertEquals("Unexpected EPSG string!", expected, referenceHelper.extractSRSCode(epsgCode));
    }
    
    // TODO add tests for creating coordinates
    // TODO add tests for transform coordinates

    /**
     * Provides testing harness for {@link AReferencingHelper} to test all high level implementations.
     */
    private class NonCrsReferencingHelperSeam extends AReferencingHelper {
        protected NonCrsReferencingHelperSeam() {
            super(null); // XXX change when testing coordinate handling
        }

        @Override
        public List<Station> getContainingStations(BoundingBox bbox, Collection<Station> stations) {
            throw new UnsupportedOperationException("no test");
        }

        @Override
        public boolean isStationContainedByBBox(BoundingBox bbox, Station station) {
            throw new UnsupportedOperationException("no test");
        }
        
    }

}
