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
package org.n52.server.oxf.util.connector.grdc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.opengis.gml.CodeType;
import net.opengis.gml.DirectPositionType;
import net.opengis.gml.FeatureCollectionDocument2;
import net.opengis.gml.FeaturePropertyType;

import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.server.oxf.util.access.OperationAccessor;
import org.n52.server.oxf.util.access.oxfExtensions.SOSAdapter_OXFExtension;
import org.n52.server.oxf.util.access.oxfExtensions.SOSRequestBuilderFactory_OXFExtension;
import org.n52.server.oxf.util.connector.SOSConnector;
import org.n52.server.oxf.util.crs.AReferencingFacade;
import org.n52.server.oxf.util.parser.ConnectorUtils;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import de.bafg.grdc.sampling.x10.GrdcSamplingPointDocument;
import de.bafg.grdc.sampling.x10.GrdcSamplingPointType;

public class GrdcSOSConnector implements SOSConnector {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GrdcSOSConnector.class);
	
	@Override
	public SOSMetadataResponse buildUpServiceMetadata(String sosUrl, String sosVersion) throws Exception {
	    ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.generateRequestBuilder(sosVersion);
		SOSAdapter adapter = new SOSAdapter_OXFExtension(sosVersion, requestBuilder);
		ServiceDescriptor serviceDesc = ConnectorUtils.getServiceDescriptor(sosUrl, adapter);

        String sosTitle = serviceDesc.getServiceIdentification().getTitle();
		String omFormat = ConnectorUtils.getOMFormat(serviceDesc);
		String smlVersion = ConnectorUtils.getSMLVersion(serviceDesc, sosVersion);
		ConnectorUtils.setVersionNumbersToMetadata(sosUrl, sosTitle, sosVersion, omFormat, smlVersion);
		
		SOSMetadata meta = (SOSMetadata) ConfigurationContext.getServiceMetadata(sosUrl);
		
		IBoundingBox sosBbox = null;
		Map<String, FutureTask<OperationResult>> futureTasks = new HashMap<String, FutureTask<OperationResult>>();
		Contents contents = serviceDesc.getContents();
		for (String dataIdent : contents.getDataIdentificationIDArray()) {
			ObservationOffering observationOffering = (ObservationOffering) contents.getDataIdentification(dataIdent);
			String offeringID = observationOffering.getIdentifier();
			
			sosBbox = ConnectorUtils.createBbox(sosBbox, observationOffering);
            
			String[] phenArray = observationOffering.getObservedProperties();
			String[] procArray = observationOffering.getProcedures();

			// add offering
			Offering offering = new Offering(offeringID);
			offering.setLabel(observationOffering.getTitle());
            meta.addOffering(offering);
			
			// add phenomenons
			for (String phenomenonId : phenArray) {
				Phenomenon phenomenon = new Phenomenon(phenomenonId);
				phenomenon.setLabel(phenomenonId.substring(phenomenonId.lastIndexOf(":") + 1));
                meta.addPhenomenon(phenomenon);
			}
			
			// add procedures
			for (String procedure : procArray) {
				meta.addProcedure(new Procedure(procedure));
			}

			ArrayList<String> fois = new ArrayList<String>();
			// remove related features
			for (String foi : observationOffering.getFeatureOfInterest()) {
				if (!foi.contains("related")) {
					fois.add(foi);
				}
			}
			
			// add station
			for (String procedure : procArray) {
				for (String phenomenon : phenArray) {
					for (String foi : fois) {
						Station station = new Station();
						station.setPhenomenon(phenomenon);
						station.setProcedure(procedure);
						station.setOffering(offeringID);
						station.setFeature(foi);
						meta.addStation(station);	
					}
				}
			}
		
			for (String foi : fois) {
				// create GetFeatureOfInterest
				futureTasks.put(foi, new FutureTask<OperationResult>(createGetFOI(sosUrl, sosVersion, foi)));
			}
		}
		
		// execute the GetFeatureOfInterest requests
		LOGGER.info("Sending " + futureTasks.size() + " GetFeatureOfInterest requests");
		for (String getFoi : futureTasks.keySet()) {
			LOGGER.info("Sending request for " + getFoi);
			AccessorThreadPool.execute(futureTasks.get(getFoi));
		}
		
		// add srs
        try {
            if (!sosBbox.getCRS().startsWith("EPSG")) {
                String tmp = "EPSG:" + sosBbox.getCRS().split(":")[sosBbox.getCRS().split(":").length - 1];
                meta.setSrs(tmp);
            } else {
                meta.setSrs(sosBbox.getCRS());
            }
        } catch (Exception e) {
            LOGGER.error("Could not insert spatial metadata", e);
        }
        
        while (!futureTasks.isEmpty()) {
        	Set<String> keys = new HashSet<String>();
        	keys.addAll(futureTasks.keySet());
			for (String foi : keys) {
				try {
					FutureTask<OperationResult> futureTask = futureTasks.get(foi);
					OperationResult opRes = futureTask.get(ConfigurationContext.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
					Set<Station> stations = meta.getStationsByFeatureID(foi);
					meta.removeStations(stations);
					if (opRes == null) {
						LOGGER.error("Get no result for GetFeatureOfInterest " + foi + "!");
					}
					XmlObject xmlObject = XmlObject.Factory.parse(opRes.getIncomingResultAsStream());
					if (xmlObject instanceof FeatureCollectionDocument2) {
						FeatureCollectionDocument2 foicoll = (FeatureCollectionDocument2) xmlObject;
						for (FeaturePropertyType featurePropertyType : foicoll.getFeatureCollection().getFeatureMemberArray()) {
							GrdcSamplingPointDocument doc = GrdcSamplingPointDocument.Factory.parse(featurePropertyType.xmlText());
							GrdcSamplingPointType grdcSamplingPoint = doc.getGrdcSamplingPoint();
							String featureId = grdcSamplingPoint.getId();
							String label;
							CodeType[] nameArray = grdcSamplingPoint.getNameArray();
							if (nameArray.length > 0) {
								label = nameArray[0].getStringValue();
							} else {
								label = featureId;
							}
							// add feature
							FeatureOfInterest feature = new FeatureOfInterest(featureId);
							feature.setLabel(label);
							meta.addFeature(feature);
							// add position and foiID to a new station
							for (Station station : stations) {
								Station clone = station.clone();
								clone.setFeature(featureId);
								ParsedPoint point = getPositionOfGRDCSamplingPoint(grdcSamplingPoint);
	                            double lat = Double.parseDouble(point.getLat());
	                            double lng = Double.parseDouble(point.getLon());
	                            EastingNorthing coords = new EastingNorthing(lng, lat);
	                            clone.setLocation(coords, point.getSrs());
								meta.addStation(clone);
							}
						}
					}
				} catch (TimeoutException e) {
					LOGGER.error("Timeout occured.", e);
				} finally {
					futureTasks.remove(foi);
				}
			} 
		}
		
		meta.setHasDonePositionRequest(true);
		return new SOSMetadataResponse(meta);
	}

	private ParsedPoint getPositionOfGRDCSamplingPoint(GrdcSamplingPointType grdcSamplingPoint) {
		DirectPositionType pos = grdcSamplingPoint.getPosition().getPoint().getPos();
		String[] coords = pos.getStringValue().split(" ");
		
		Double lat = Double.parseDouble(coords[0]);
		Double lon = Double.parseDouble(coords[1]);
		Double h = null;
		if (coords.length == 3) {
			h = Double.parseDouble(coords[2]);
		}
		
        AReferencingFacade ref = AReferencingFacade.createReferenceFacade();
        String srs = ref.extractSRSCode(pos.getSrsName());
        String wgs84 = "EPSG:4326";
        if (!srs.equals(wgs84)) {
            try {
                int srsId = ref.getSrsIdFromEPSG(srs);
                PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING);
                GeometryFactory geometryFactory = new GeometryFactory(pm, srsId);
                Coordinate coordinate = ref.createCoordinate(srs, lon, lat, h);
                Point point = geometryFactory.createPoint(coordinate);
                point = ref.transform(point, srs, wgs84);
                srs = wgs84;
                lat = point.getX();
                lon = point.getY();
                LOGGER.trace(lon + "," + lat + " (" + srs + ")");
            }
            catch (Exception e) {
                LOGGER.debug("Could not transform! Keeping old SRS: " + srs, e);
            }
        }
		
		return new ParsedPoint(lon+"", lat+"", srs);
	}

	private OperationAccessor createGetFOI(String sosUrl, String sosVersion, String foi) throws OXFException {
        ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.generateRequestBuilder(sosVersion);
        SOSAdapter adapter = new SOSAdapter_OXFExtension(sosVersion, requestBuilder);
		Operation operation = new Operation(SOSAdapter.GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
		ParameterContainer paramCon = new ParameterContainer();
		paramCon.addParameterShell(ISOSRequestBuilder.GET_FOI_ID_PARAMETER, foi);
		paramCon.addParameterShell(ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER, "SOS");
		paramCon.addParameterShell(ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER, sosVersion);
		OperationAccessor opAccessorCallable = new OperationAccessor(adapter, operation, paramCon);
		return opAccessorCallable;
	}

}