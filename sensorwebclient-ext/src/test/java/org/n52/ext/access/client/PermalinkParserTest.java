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
package org.n52.ext.access.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.ext.access.client.QueryParser;
import org.n52.ext.access.client.TimeRange;

public class PermalinkParserTest {
    
    private static final String ENCODED_SOS_URL = "http%3A//sensorweb.demo.52north.org%3A80/PegelOnlineSOSv2.1/sos";
    
    private static String DECODED_SOS_URL;
    
    @Before
    public void setUp() throws Exception {
        DECODED_SOS_URL = URLDecoder.decode(ENCODED_SOS_URL, Charset.forName("utf-8").name());
    }

    @Test
    public void testValidCreationWithNullValues() {
        assertNotNull(new QueryParser(null, false));
        assertNotNull(new QueryParser(null, true));
    }
    
    @Test
    public void testNonExceptionsThrownWhenCreatedWithNullValues() {
        QueryParser permalinkParser = new QueryParser(null, false);
        assertTrue(permalinkParser.parseOfferings().isEmpty());
        assertTrue(permalinkParser.parsePhenomenons().isEmpty());
        assertTrue(permalinkParser.parseProcedures().isEmpty());
        assertTrue(permalinkParser.parseServices().isEmpty());
        assertTrue(permalinkParser.parseStations().isEmpty());
    }
    
    @Test
    public void testParseValidKvps() {
        String validQuery = "single=blah&multiple=value1,vlaue2";
        QueryParser permalinkParser = new QueryParser(null, false);
        Map<String, String> kvps = permalinkParser.parseKvps(validQuery);
        assertTrue("map does not contain value 'single'.", kvps.containsKey("SINGLE"));
        assertTrue("map does not contain value 'multiple'.", kvps.containsKey("MULTIPLE"));
    }
    
    @Test
    public void testParseInvalidKvps() {
        String invalidQuery = "single&multiple=value1,vlaue2";
        QueryParser permalinkParser = new QueryParser(null, false);
        Map<String, String> kvps = permalinkParser.parseKvps(invalidQuery);
        assertFalse("map does not contain value 'single'.", kvps.containsKey("SINGLE"));
        assertTrue("map does not contain value 'multiple'.", kvps.containsKey("MULTIPLE"));
    }
    
    @Test
    public void testDecodeQuery() {
        QueryParser permalinkParser = new QueryParser(null, false);
        assertEquals("Unexpected decoded URL.", DECODED_SOS_URL, permalinkParser.decodeValue(ENCODED_SOS_URL));
        String decodedCharacters = "! # $ % & ' ( ) * + , / : ; = ? @ [ ]";
        // check at http://www.ulimatbach.de/links/url_decoder.html
        String encodedCharacters = "%21+%23+%24+%25+%26+%27+%28+%29+*+%2B+%2C+/+%3A+%3B+%3D+%3F+@+%5B+%5D";
        assertEquals("Unexpected decoding.", decodedCharacters, permalinkParser.decodeValue(encodedCharacters));
    }
    
    @Test
    public void testParseEncodedKvps() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("single");
        validQuery.append("=");
        validQuery.append("sadf");
        validQuery.append("&");
        validQuery.append("multiple");
        validQuery.append("=");
        validQuery.append("value1");
        validQuery.append("%2C");
        validQuery.append("value2_part1+value2_part2");
        validQuery.append("&");
        validQuery.append("url");
        validQuery.append("=");
        validQuery.append("http%3A//url%3A8080/%3Fparam1%3Dvalue1+part2%2Cparam2%3Dvalue2");
        QueryParser permalinkParser = new QueryParser(null, false);
        Map<String, String> kvps = permalinkParser.parseKvps(validQuery.toString());
        assertTrue("map does not contain value 'single'.", kvps.containsKey("SINGLE"));
        assertTrue("map does not contain value 'multiple'.", kvps.containsKey("MULTIPLE"));
        assertTrue("map does not contain value 'url'.", kvps.containsKey("URL"));
    }
    
    @Test
    public void testParseServiceKvp() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("sos=").append(ENCODED_SOS_URL);
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        Collection<String> parsedServices = permalinkParser.parseServices();
        assertTrue("Invalid size: " + parsedServices.size(), parsedServices.size() == 1);
        assertTrue("URL could not be parsed.", parsedServices.contains(DECODED_SOS_URL));
    }

    @Test
    public void testParseVersionKvp() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("versions=").append("2.0.0");
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        Collection<String> parsedVersions = permalinkParser.parseVersions();
        assertTrue("Invalid size: " + parsedVersions.size(), parsedVersions.size() == 1);
        assertTrue("Versions could not be parsed.", parsedVersions.contains("2.0.0"));
    }
    
    @Test
    public void testParseOfferingKvp() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("offerings=").append("WASSERSTAND_ROHDATEN");
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        Collection<String> parsedOfferings = permalinkParser.parseOfferings();
        assertTrue("Invalid size: " + parsedOfferings.size(), parsedOfferings.size() == 1);
        assertTrue("Offerings could not be parsed.", parsedOfferings.contains("WASSERSTAND_ROHDATEN"));
    }

    @Test
    public void testParseStationKvp() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("stations=").append("Heldra_41700105");
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        Collection<String> parsedStations = permalinkParser.parseStations();
        assertTrue("Invalid size: " + parsedStations.size(), parsedStations.size() == 1);
        assertTrue("Stations could not be parsed.", parsedStations.contains("Heldra_41700105"));
    }
    
    @Test
    public void testParsePhenomenonsKvp() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("phenomenons=").append("Wasserstand");
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        Collection<String> parsedPhenomenons = permalinkParser.parsePhenomenons();
        assertTrue("Invalid size: " + parsedPhenomenons.size(), parsedPhenomenons.size() == 1);
        assertTrue("Stations could not be parsed.", parsedPhenomenons.contains("Wasserstand"));
    }

    @Test
    public void testParseProceduresKvp() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("procedures=").append("Wasserstand-Heldra_41700105");
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        Collection<String> parsedProcedures = permalinkParser.parseProcedures();
        assertTrue("Invalid size: " + parsedProcedures.size(), parsedProcedures.size() == 1);
        assertTrue("Stations could not be parsed.", parsedProcedures.contains("Wasserstand-Heldra_41700105"));
    }
    
    @Test
    public void testParsingBeginAndEndKvps() {
        StringBuilder validQuery = new StringBuilder();
        validQuery.append("begin");
        validQuery.append("=");
        validQuery.append("2012-10-01T12:01:00");
        validQuery.append("&");
        validQuery.append("end");
        validQuery.append("=");
        validQuery.append("2014-10-01T12:01:00");
        QueryParser permalinkParser = new QueryParser(validQuery.toString(), false);
        TimeRange parsedTimeRange = permalinkParser.parseTimeRange();
        assertEquals("2012-10-01T12:01:00", parsedTimeRange.getStart());
        assertEquals("2014-10-01T12:01:00", parsedTimeRange.getEnd());
    }

}
