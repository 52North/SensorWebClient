/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.sos.connector.ags;

import java.util.HashMap;
import java.util.Map;

import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.swe.x101.QuantityDocument.Quantity;

import org.apache.xmlbeans.XmlObject;
import org.n52.server.util.XmlHelper;

public class ArcGISSoeDescribeSensorParser {
    
    private static final Map<String, String> namespaceDeclarations = new HashMap<String, String>();

    {
        namespaceDeclarations.put("gml", "http://www.opengis.net/gml");
        namespaceDeclarations.put("swe", "http://www.opengis.net/swe/1.0.1");
        namespaceDeclarations.put("swes", "http://www.opengis.net/swes/2.0");
        namespaceDeclarations.put("sml", "http://www.opengis.net/sensorML/1.0.1");
        namespaceDeclarations.put("base", "http://inspire.ec.europa.eu/schemas/base/3.3rc3/");
        namespaceDeclarations.put("aqd", "http://aqd.ec.europa.eu/aqd/0.3.7c");
    }

    private XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);

    private SensorML sensorML;

    public ArcGISSoeDescribeSensorParser(XmlObject sml) {
        if (sml == null) {
            throw new NullPointerException("no SensorML to parse (was null)");
        }
        sensorML = xmlHelper.parseFirst(sml, "$this//sml:SensorML", SensorML.class);
        if (sensorML == null) {
            sensorML = xmlHelper.parseFirst(sml, "$this//*/sml:SensorML", SensorML.class);
        }
    }

    public String getUomFor(String phenomenonId) {
        String xPath = "$this//*/sml:output/swe:Quantity[@definition='%s' and swe:uom[@code]]";
        String query = String.format(xPath, phenomenonId);
        Quantity output = xmlHelper.parseFirst(sensorML, query, Quantity.class);
        return output == null ? null : output.getUom().getCode();
    }

    public String getShortName() {
        String query = "$this//*/sml:identifier[@name='shortName']";
        Identifier identifier = xmlHelper.parseFirst(sensorML, query, Identifier.class);
        return identifier == null ? null : identifier.getTerm().getValue();
    }
    
    public SensorML getSensorML() {
        return sensorML;
    }

}
