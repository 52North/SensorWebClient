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

package org.n52.server.util;

import static org.n52.oxf.xmlbeans.tools.XmlUtil.selectPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.PointDocument;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.TermDocument.Term;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.TextDocument.Text;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XmlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlHelper.class);

    private static final String SAMS_20_NAMESPACE = "http://www.opengis.net/samplingSpatial/2.0";

    private static final String GML_321_NAMESPACE = "http://www.opengis.net/gml/3.2";

    private Map<String, String> namespaceDeclarations = new HashMap<String, String>();

    public XmlHelper(Map<String, String> namespaceDecarations) {
        this.namespaceDeclarations = namespaceDecarations;
    }

    public String getShortName(IdentifierList identifiers) {
        String xpath = "$this//*//sml:identifier[@name='shortName']";
        Identifier shortName = parseFirst(identifiers, xpath, Identifier.class);
        return shortName == null ? null : shortName.getTerm().getValue();
    }

    public String getUniqueId(IdentifierList identifiers) {
        String xpath = "$this//sml:identifier//sml:Term[@definition='urn:ogc:def:identifier:OGC:1.0:uniqueID']";
        Term uniqueId = parseFirst(identifiers, xpath, Term.class);
        return uniqueId == null ? null : uniqueId.getValue();
    }

    public String[] getRelatedFeatures(Capabilities identifiers) {
        String xpath = "$this//*//swe:field/swe:Text[@definition='FeatureOfInterestID']";
        Text[] results = parseAll(identifiers, xpath, Text.class);
        return results.length > 0 ? getTextValues(results) : null;
    }

    private String[] getTextValues(Text[] results) {
        List<String> values = new ArrayList<String>();
        for (Text text : results) {
            values.add(text.getValue());
        }
        return values.toArray(new String[0]);
    }

    public PointDocument getPoint(FeaturePropertyType member, CRSUtils referenceHelper) throws XmlException {

        String namepace = String.format("declare namespace sams='%s' ", SAMS_20_NAMESPACE);
        namepace += String.format("declare namespace gml='%s'", GML_321_NAMESPACE);
        String xpath = "$this//*//sams:shape";
        String query = String.format("%s ; %s", namepace, xpath);
        XmlObject[] points = (XmlObject[]) selectPath(query, member);
        if (points.length > 0) {
            return PointDocument.Factory.parse(points[0].xmlText());
        }
        else {
            return null;
        }
    }

    public String[] getRelatedPhenomena(Outputs outputs) {
        List<String> phenomena = new ArrayList<String>();
        OutputList outputList = outputs.getOutputList();
        IoComponentPropertyType[] outputArray = outputList.getOutputArray();
        for (IoComponentPropertyType output : outputArray) {
            if (output.isSetQuantity()) {
                Quantity quantity = output.getQuantity();
                phenomena.add(quantity.getDefinition());
            }
        }
        return phenomena.toArray(new String[0]);
    }

    /**
     * Parses the first available xml content defined by given xpath. Search is starting relative to given
     * node and {@link Class#cast(Object)}s to expected type.<br/>
     * <br/>
     * To qualify search the {@link XmlHelper} must be created with appropriate namespace declarations. Then
     * using prefixes within xpath string is enough. For example:
     * 
     * <pre>
     * private static final Map&lt;String, String&gt; namespaceDeclarations = new HashMap&lt;String, String&gt;();
     * 
     * {
     *     namespaceDeclarations.put(&quot;sml&quot;, &quot;http://www.opengis.net/sensorML/1.0.1&quot;);
     *     namespaceDeclarations.put(&quot;swe&quot;, &quot;http://www.opengis.net/swe/1.0.1&quot;);
     *     namespaceDeclarations.put(&quot;aqd&quot;, &quot;http://aqd.ec.europa.eu/aqd/0.3.7c&quot;);
     *     namespaceDeclarations.put(&quot;base&quot;, &quot;http://inspire.ec.europa.eu/schemas/base/3.3rc3/&quot;);
     * }
     * 
     * private XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);
     * </pre>
     * 
     * With this a search can be performed via:
     * <pre>
     *     SystemType systemType = xmlHelper.parseFirst(smlDoc, "$this//sml:member//sml:System", SystemType.class);
     * </pre>
     * 
     * @param from
     *        the node where search is started.
     * @param xPath
     *        the xpath search string.
     * @param ofType
     *        which type is expected.
     * @return the first value which could be parsed.
     */
    public <T> T parseFirst(XmlObject from, String xPath, Class<T> ofType) {
        T[] results = parseAll(from, xPath, ofType);
        return results.length > 0 ? results[0] : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] parseAll(XmlObject from, String xPath, Class<T> ofType) {
        String query = String.format("%s ; %s", buildNamespaceDeclarationString(), xPath);
        return (T[]) selectPath(query, from);
    }

    private String buildNamespaceDeclarationString() {
        if (namespaceDeclarations == null || namespaceDeclarations.isEmpty()) {
            LOGGER.warn("No namespace declarations present! XPath evaluation is likely to fail.");
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String prefix : namespaceDeclarations.keySet()) {
            sb.append("declare namespace ").append(prefix).append("=");
            sb.append("'").append(namespaceDeclarations.get(prefix)).append("' ");
        }
        return sb.toString();
    }

}
