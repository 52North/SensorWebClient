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
package org.n52.server.sos.connector.eea;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import javax.xml.namespace.QName;

import net.opengis.gml.x32.AbstractGeometryType;
import net.opengis.gml.x32.DirectPositionType;
import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.gml.x32.impl.PointTypeImpl;
import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.samplingSpatial.x20.ShapeDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;
import net.opengis.sos.x20.GetFeatureOfInterestResponseType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.MetadataHandler;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.parser.ConnectorUtils;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.opengis.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public class ArcGISSoeMetadataHandler extends MetadataHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ArcGISSoeMetadataHandler.class);

	public ArcGISSoeMetadataHandler(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public void assembleTimeseriesMetadata(TimeseriesProperties properties) throws Exception {
        /* 
         * XXX separating metadata assembling would need to be implemented here but the old 
         * SOE connector module is obsolete so we leave it as is. See sensorwebclient-ags 
         * module for implementation targeting the current SOS SOE implementation.
         */
    }

    @Override
	public SOSMetadata performMetadataCompletion() throws Exception {
		String sosUrl = getServiceUrl();
        String sosVersion = getServiceVersion();
        SOSMetadata metadata = initMetadata();
		TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
		
        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(sosUrl);
        
        // TODO send DescribeSensor for every procedure to get the UOM, when the EEA-SOS deliver the uom

        CRSUtils referenceHelper = createReferencingHelper();
        Map<String, String> offeringBBoxMap = getOfferingBBoxMap();
        Map<SosTimeseries, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<SosTimeseries, FutureTask<OperationResult>>();
        for (SosTimeseries timeseries : observingTimeseries) {
        	String bboxString = offeringBBoxMap.get(timeseries.getOfferingId());
        	futureTasks.put(timeseries,	new FutureTask<OperationResult>(createGetFoiAccess(sosUrl, sosVersion, bboxString, timeseries)));
		}
		// execute the GetFeatureOfInterest requests
		LOGGER.debug("Sending " + futureTasks.size() + " GetFeatureOfInterest requests");
		for (SosTimeseries timeseries : futureTasks.keySet()) {
		    LOGGER.debug("Sending request for " + timeseries);
			AccessorThreadPool.execute(futureTasks.get(timeseries));
			try {
				FutureTask<OperationResult> futureTask = futureTasks.get(timeseries);
				OperationResult opRes = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
				if (opRes == null) {
					LOGGER.error("Get no result for GetFeatureOfInterest " + timeseries + "!");
				}
				XmlObject xmlObject = XmlObject.Factory.parse(opRes.getIncomingResultAsStream());
				if (xmlObject instanceof GetFeatureOfInterestResponseDocument) {
					GetFeatureOfInterestResponseDocument getFoiRespDoc = (GetFeatureOfInterestResponseDocument) xmlObject;
					GetFeatureOfInterestResponseType getFoiResp = getFoiRespDoc.getGetFeatureOfInterestResponse();
					FeaturePropertyType[] featureMemberArray = getFoiResp.getFeatureMemberArray();
					for (FeaturePropertyType featurePropType : featureMemberArray) {
						SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory.parse(featurePropType.xmlText());
						SFSamplingFeatureType sfSamplingFeature = samplingFeature.getSFSamplingFeature();
						String id = sfSamplingFeature.getId();
						// create station if not exists
						Station station = metadata.getStation(id);
						if (station == null) {
							Point point = getPointOfSamplingFeatureType(sfSamplingFeature, referenceHelper);
	                        station = new Station(id, sosUrl);
	                        station.setLocation(point);
	                        metadata.addStation(station);
						}
                        // add feature
						String label;
						if (sfSamplingFeature.getNameArray().length > 0) {
							label = sfSamplingFeature.getNameArray(0).getStringValue();
						} else {
							label = id;
						}
						Feature feature = new Feature(id, sosUrl);
						feature.setLabel(label);
                        lookup.addFeature(feature);
                        
                        SosTimeseries tmp = timeseries.clone();
                        tmp.setFeature(new Feature(id, sosUrl));
                        station.addTimeseries(tmp);
					}
				}
			} catch (TimeoutException e) {
				LOGGER.error("Timeout occured.", e);
			} finally {
				futureTasks.remove(timeseries);
			}
		} 

        infoLogServiceSummary(metadata);
		metadata.setHasDonePositionRequest(true);
		return metadata;
	}

	public Point getPointOfSamplingFeatureType(SFSamplingFeatureType sfSamplingFeature, CRSUtils referenceHelper) throws XmlException, FactoryException {
		XmlCursor cursor = sfSamplingFeature.newCursor();
		if (cursor.toChild(new QName("http://www.opengis.net/samplingSpatial/2.0", "shape"))) {
			ShapeDocument shapeDoc = ShapeDocument.Factory.parse(cursor.getDomNode());
			AbstractGeometryType abstractGeometry = shapeDoc.getShape().getAbstractGeometry();
			if (abstractGeometry instanceof PointTypeImpl) {
				PointTypeImpl pointDoc = (PointTypeImpl) abstractGeometry;
				DirectPositionType pos = pointDoc.getPos();
				String[] lonLat = pos.getStringValue().split(" ");
                Double x = Double.parseDouble(lonLat[0]);
                Double y = Double.parseDouble(lonLat[1]);
		        return referenceHelper.createPoint(x, y, "CRS:84");
			}
		}
		return null;
	}
	
	private Map<String, String> getOfferingBBoxMap() throws OXFException {
		Map<String, String> offeringBBox = new HashMap<String, String>();
		Contents contents = getServiceDescriptor().getContents();
		for (String dataIdent : contents.getDataIdentificationIDArray()) {
			ObservationOffering offering = (ObservationOffering) contents.getDataIdentification(dataIdent);
			String key = offering.getIdentifier(); 
			String bboxString = createBboxString(ConnectorUtils.createBbox(null, offering), createReferencingHelper());
			offeringBBox.put(key, bboxString);
		}
		return offeringBBox;
	}

	public String createBboxString(IBoundingBox bbox, CRSUtils referenceHelper) {
		StringBuffer sb = new StringBuffer();
		sb.append("om:featureOfInterest/*/sams:shape,");
		sb.append(bbox.getLowerCorner()[0]).append(",");
		sb.append(bbox.getLowerCorner()[1]).append(",");
		sb.append(bbox.getUpperCorner()[0]).append(",");
		sb.append(bbox.getUpperCorner()[1]).append(",");
		int code = referenceHelper.getSrsIdFrom(bbox.getCRS());
		sb.append("urn:ogc:def:crs:EPSG::").append(code);
		return sb.toString();
	}

	private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, String bboxString, SosTimeseries timeseries) throws OXFException {
		SOSAdapter adapter = new SOSAdapterByGET(sosVersion);
		Operation operation = new Operation(SOSAdapter.GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		ParameterContainer container = new ParameterContainer();
		container.addParameterShell(ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("phenomenon", timeseries.getPhenomenonId());
        container.addParameterShell("procedure", timeseries.getProcedureId());
        container.addParameterShell("bbox", bboxString);
		return new OperationAccessor(adapter, operation, container);
	}

	@Override
	public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
		throw new UnsupportedOperationException();
	}

}
