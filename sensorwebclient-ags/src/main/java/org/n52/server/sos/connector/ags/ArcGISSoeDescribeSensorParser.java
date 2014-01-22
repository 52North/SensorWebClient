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
        this.sensorML = xmlHelper.parseFirst(sml, "$this//*/sml:SensorML", SensorML.class);
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
