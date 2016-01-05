/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.server.parser;

import com.vividsolutions.jts.geom.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import net.opengis.gml.x32.AbstractGeometryType;
import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.impl.PointTypeImpl;
import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.waterml.x20.MonitoringPointDocument;
import net.opengis.waterml.x20.MonitoringPointType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetFeatureOfInterestParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(GetFeatureOfInterestParser.class);

	private OperationResult getFoiResult;

	private SOSMetadata metadata;

	private CRSUtils referenceHelper = CRSUtils.createEpsgStrictAxisOrder();

	public GetFeatureOfInterestParser(OperationResult opsRes, SOSMetadata metadata) {
		getFoiResult = opsRes;
		if (getFoiResult == null) {
            LOGGER.error("Get no result for GetFeatureOfInterest!");
        }
		this.metadata = metadata;
		if(metadata.isForceXYAxisOrder()) {
			referenceHelper = CRSUtils.createEpsgForcedXYAxisOrder();
		}
	}

	public List<Station> createStations() throws XmlException, IOException, OXFException {

        List<Station> stations = new ArrayList<Station>();
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        GetFeatureOfInterestResponseDocument foiResDoc = getFOIResponseOfOpResult(getFoiResult);

        String id = null;
        String label = null;
        for (FeaturePropertyType featurePropertyType : foiResDoc.getGetFeatureOfInterestResponse().getFeatureMemberArray()) {
            Point point = null;
            XmlCursor xmlCursor = featurePropertyType.newCursor();
            if (xmlCursor.toChild(new QName("http://www.opengis.net/samplingSpatial/2.0",
                                            "SF_SpatialSamplingFeature"))) {
                SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory.parse(xmlCursor.getDomNode());
                SFSamplingFeatureType sfSamplingFeature = samplingFeature.getSFSamplingFeature();
                id = sfSamplingFeature.getIdentifier().getStringValue();
                if (sfSamplingFeature.getNameArray().length > 0) {
                    label = sfSamplingFeature.getNameArray(0).getStringValue();
                }
                else {
                    label = id;
                }
                point = createParsedPoint(sfSamplingFeature, referenceHelper);
            }
            else if (xmlCursor.toChild(new QName("http://www.opengis.net/waterml/2.0", "MonitoringPoint"))) {
                MonitoringPointDocument monitoringPointDoc = MonitoringPointDocument.Factory.parse(xmlCursor.getDomNode());
                MonitoringPointType monitoringPoint = monitoringPointDoc.getMonitoringPoint();
                id = monitoringPoint.getIdentifier().getStringValue();
                if (monitoringPoint.getNameArray().length > 0) {
                    label = monitoringPoint.getNameArray(0).getStringValue();
                }
                else {
                    label = id;
                }
                point = createParsedPoint(monitoringPoint, referenceHelper);
            }
            else {
                LOGGER.error("Did not find supported feature members in the GetFeatureOfInterest response");
            }
            if (point == null) {
                LOGGER.warn("The foi with ID {} has no valid point: {}", id, featurePropertyType.toString());
            }
            else {
                // add feature
                Feature feature = new Feature(id, metadata.getServiceUrl());
                feature.setLabel(label);
                lookup.addFeature(feature);

                // create station if not exists
                Station station = metadata.getStationByFeature(feature);
                if (station == null) {
                    station = new Station(feature);
                    station.setLocation(point);
                    metadata.addStation(station);
                    stations.add(station);
                }
            }
        }
        return stations;
	}

	private Point createParsedPoint(XmlObject feature, CRSUtils referenceHelper) throws XmlException {
        XmlCursor cursor = feature.newCursor();
        if (cursor.toChild(new QName("http://www.opengis.net/samplingSpatial/2.0", "shape"))) {
            ShapeDocument shapeDoc = ShapeDocument.Factory.parse(cursor.getDomNode());
            AbstractGeometryType abstractGeometry = shapeDoc.getShape().getAbstractGeometry();
            if (abstractGeometry instanceof PointTypeImpl) {
                PointTypeImpl pointDoc = (PointTypeImpl) abstractGeometry;
                DirectPositionType pos = pointDoc.getPos();
                String srsName = pos.getSrsName();
                String[] lonLat = pos.getStringValue().split(" ");
                if (lonLat[0].isEmpty()) {
                    return null;
                }

                Double lon = Double.parseDouble(lonLat[0]);
                Double lat = Double.parseDouble(lonLat[1]);
                Double alt = Double.NaN;
                if (lonLat.length == 3) {
                    alt = Double.parseDouble(lonLat[2]);
                }
                try {
                    String srs = referenceHelper.extractSRSCode(srsName);
                    Point point = referenceHelper.createPoint(lon, lat, alt, srs);
                    return (Point) referenceHelper.transformOuterToInner(point, srs);
                }
                catch (FactoryException e) {
                    LOGGER.warn("Could not create intern CRS.", e);
                }
                catch (TransformException e) {
                    LOGGER.warn("Could not transform to intern CRS.", e);
                }
            }
        }
        return null;
    }

	private GetFeatureOfInterestResponseDocument getFOIResponseOfOpResult(
			OperationResult getFoiResult) throws XmlException, IOException, OXFException {
    	XmlObject foiResponse = XmlObject.Factory.parse(getFoiResult.getIncomingResultAsAutoCloseStream());
        if (foiResponse instanceof GetFeatureOfInterestResponseDocument) {
            return (GetFeatureOfInterestResponseDocument) foiResponse;
        }
        else {
            throw new OXFException("No valid GetFeatureOfInterestREsponse");
        }
	}
}
