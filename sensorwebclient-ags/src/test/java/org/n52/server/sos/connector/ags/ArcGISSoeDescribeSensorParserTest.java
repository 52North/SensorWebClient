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
package org.n52.server.sos.connector.ags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument.SensorML;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;

public class ArcGISSoeDescribeSensorParserTest {

    private final static String DESCRIBE_SENSOR_INSTANCE = "/files/describe-sensor-instance.xml";
    
    private final static String PHENOMENON = "http://dd.eionet.europa.eu/vocabulary/aq/pollutant/8";
    
    private ArcGISSoeDescribeSensorParser parser;
    
    @Before public void 
    setUp() throws IOException, XmlException {
        XmlObject sml = XmlFileLoader.loadXmlFileViaClassloader(DESCRIBE_SENSOR_INSTANCE, getClass());
        parser = new ArcGISSoeDescribeSensorParser(sml);
        assertThat("XML is not a SensorML 1.0.1!", parser.getSensorML().schemaType(), is(SensorML.type));
    }
    
    @Test public void 
    shouldParseFirstAvailableUomFromInconsistentMultipleOutputSection() {
        assertThat("UOM code is not correct!", parser.getUomFor(PHENOMENON), is("ug.m-3"));
    }
    
    @Test public void
    shouldParseShortName() {
        assertThat("shortName is incorrect!", parser.getShortName(), is("GB_StationProcess_1"));
    }

}
