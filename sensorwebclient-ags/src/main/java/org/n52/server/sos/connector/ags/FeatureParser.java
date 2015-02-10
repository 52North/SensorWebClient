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
package org.n52.server.sos.connector.ags;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.PointDocument;
import net.opengis.gml.x32.PointType;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.server.util.XmlHelper;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public final class FeatureParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureParser.class);

    private static final Map<String, String> namespaceDeclarations = new HashMap<String, String>();

    static {
        namespaceDeclarations.put("sml", "http://www.opengis.net/sensorML/1.0.1");
        namespaceDeclarations.put("swe", "http://www.opengis.net/swe/1.0.1");
        namespaceDeclarations.put("aqd", "http://aqd.ec.europa.eu/aqd/0.3.7c");
        namespaceDeclarations.put("base", "http://inspire.ec.europa.eu/schemas/base/3.3rc3/");
    }

    private final XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);

    private final String serviceUrl;

    private final CRSUtils crsUtil;

    public FeatureParser(String serviceUrl, CRSUtils crsHelper) {
        this.serviceUrl = serviceUrl;
        this.crsUtil = crsHelper;
    }

    public Map<Feature, Point> parseFeatures(InputStream stream) {
        Map<Feature, Point> featureLocations = new HashMap<Feature, Point>();
        try {
            GetFeatureOfInterestResponseDocument responseDoc = GetFeatureOfInterestResponseDocument.Factory.parse(stream);
            GetFeatureOfInterestResponseType response = responseDoc.getGetFeatureOfInterestResponse();
            for (FeaturePropertyType member : response.getFeatureMemberArray()) {
                PointDocument pointDoc = xmlHelper.getPoint(member, crsUtil);
                Feature feature = parseFeatureFrom(member);
                Point location = getCrs84Location(pointDoc);
                featureLocations.put(feature, location);
            }
        }
        catch (XmlException e) {
            LOGGER.error("Could not parse GetFeatureOfInterestResponse.", e);
        }
        catch (IOException e) {
            LOGGER.error("Could not read GetFeatureOfInterestResponse.", e);
        }
        LOGGER.debug("Parsed #" + featureLocations.size() + " feature locations.");
        return featureLocations;
    }

    private Feature parseFeatureFrom(FeaturePropertyType member) {
        String id = getTextFrom(member, "$this//base:Identifier/base:localId/text()");
        String namespace = getTextFrom(member, "$this//base:Identifier/base:namespace");
        Feature feature = new Feature(namespace + id, serviceUrl);
        feature.setLabel(id);
        return feature;
    }

    private String getTextFrom(FeaturePropertyType member, String xpath) {
        XmlObject textNode = xmlHelper.parseFirst(member, xpath, XmlObject.class);
        return XmlUtil.getTextContentFromAnyNode(textNode);

    }

    private Point getCrs84Location(PointDocument pointDoc) {
        try {
            PointType point = pointDoc.getPoint();
            DirectPositionType position = point.getPos();
            String[] lonLat = position.getStringValue().split(" ");
            Double x = Double.parseDouble(lonLat[0]);
            Double y = Double.parseDouble(lonLat[1]);
            String srsName = getSrsName(point);
            Point outerRefPoint = crsUtil.createPoint(x, y, srsName);
            return crsUtil.transformOuterToInner(outerRefPoint, srsName);
        }
        catch (FactoryException e) {
            LOGGER.error("Could not create reference helper to parse shape from feature.", e);
            return null;
        }
        catch (TransformException e) {
            LOGGER.error("Could not transform to CRS:84.", e);
            return null;
        }
    }

    private String getSrsName(PointType point) {
        String srsName = crsUtil.extractSRSCode(point.getSrsName());
        return srsName != null && srsName.contains("102100")
                ? "3857"
                : srsName;
    }
}
