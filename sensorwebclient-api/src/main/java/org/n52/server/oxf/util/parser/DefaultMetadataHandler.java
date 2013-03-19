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

package org.n52.server.oxf.util.parser;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;
import static org.n52.server.oxf.util.ConfigurationContext.SERVER_TIMEOUT;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.sos.x20.GetFeatureOfInterestResponseDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.ows.capabilities.IBoundingBox;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.server.oxf.util.access.OperationAccessor;
import org.n52.server.oxf.util.connector.MetadataHandler;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.ParameterConstellation;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMetadataHandler.class);

    public SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws OXFException, InterruptedException, XMLHandlingException {
    	
    	SOSMetadata sosMetadata = initMetadata(sosUrl, sosVersion);

    	Contents contents = getServiceDescriptorContent();

        // association: Offering - FOIs
        Map<String, String[]> offeringFoiMap = new HashMap<String, String[]>();

        // association: Offering - Procedures
        Map<String, String[]> offeringProcMap = new HashMap<String, String[]>();

        // association: Offering - Phenomenons
        Map<String, String[]> offeringPhenMap = new HashMap<String, String[]>();

        IBoundingBox sosBbox = null;
        HashSet<String> featureIds = new HashSet<String>();

        for (int i = 0; i < contents.getDataIdentificationCount(); i++) {
            ObservationOffering offering = (ObservationOffering) contents.getDataIdentification(i);

            sosBbox = ConnectorUtils.createBbox(sosBbox, offering);

            try {
                if ( sosBbox != null && !sosBbox.getCRS().startsWith("EPSG")) {
                    String tmp = "EPSG:" + sosBbox.getCRS().split(":")[sosBbox.getCRS().split(":").length - 1];
                    sosMetadata.setSrs(tmp);
                }
                else {
                	sosMetadata.setSrs(sosBbox.getCRS());
                }
            }
            catch (Exception e) {
                LOGGER.error("Could not insert spatial metadata", e);
            }

            String offeringID = offering.getIdentifier();

            // associate:
            String[] procArray = offering.getProcedures();
            offeringProcMap.put(offeringID, procArray);

            // associate:
            String[] phenArray = offering.getObservedProperties();
            offeringPhenMap.put(offeringID, phenArray);

            // associate:
            String[] foiArray = new String[]{};
            if (SosUtil.isVersion100(sosVersion)) {
                foiArray = offering.getFeatureOfInterest();
            } else if (SosUtil.isVersion200(sosVersion)) {
            	for (String procedure : procArray) {
            		foiArray = getFoisByProcedure(sosMetadata, sosUrl, sosVersion, procedure).toArray(foiArray);
				}
            }
            offeringFoiMap.put(offeringID, foiArray);

            // iterate over fois to delete double entries for the request
            for (int j = 0; j < foiArray.length; j++) {
                featureIds.add(foiArray[j]);
            }
        }
        
        LOGGER.debug("There are " + featureIds.size() + " FOIs registered in the SOS");

        // add fois
        for (String featureId : featureIds) {
        	sosMetadata.addFeature(new FeatureOfInterest(featureId));
        	sosMetadata.addStation(new Station(featureId));
        }

        Collection<ParameterConstellation> parameterConstellations = new ArrayList<ParameterConstellation>();
        // FOI -> Procedure -> Phenomenon
        for (String offeringId : offeringFoiMap.keySet()) {
            for (String procedure : offeringProcMap.get(offeringId)) {
                if (procedure.contains("urn:ogc:generalizationMethod:")) {
                	sosMetadata.setCanGeneralize(true);
                }
                else {
                    for (String phenomenon : offeringPhenMap.get(offeringId)) {
                        /*
                         * add a station for a procedure expecting that there is only one for each right now.
                         * Further stations may be added later when additional information is parsed from
                         * getFeatureOfInterest of describeSensor operations.
                         */
                    	ParameterConstellation paramConst = new ParameterConstellation();
                        paramConst.setPhenomenon(phenomenon);
                        paramConst.setProcedure(procedure);
                        paramConst.setOffering(offeringId);
                        parameterConstellations.add(paramConst);
                    }
                    // add procedures
                    sosMetadata.addProcedure(new Procedure(procedure));
                    for (String phenomenonId : offeringPhenMap.get(offeringId)) {
                    	sosMetadata.addPhenomenon(new Phenomenon(phenomenonId));
                    }
                }
            }
            // add offering
            Offering offering = new Offering(offeringId);
            sosMetadata.addOffering(offering);
        }

        sosMetadata.setInitialized(true);

        // XXX hack to get conjunctions between procedures and fois
        if ( !sosMetadata.hasDonePositionRequest()) {
            try {
                performMetadataInterlinking(sosUrl, parameterConstellations);
            }
            catch (IOException e) {
                LOGGER.warn("Could not retrieve relations between procedures and fois", e);
            }
        }

        LOGGER.debug("Got metadata for SOS " + sosMetadata.getId());
        return new SOSMetadataResponse(sosMetadata);
    }

    /**
     * Performs a DescribeSensor request for every offered procedure of an SOS. This is intended to obtain the
     * concrete references between each offering, procedure, featureOfInterest and observedProperty (aka
     * phenomenon). <br>
     * <br>
     * <bf>Note:</bf> to create the associations between these key items the Sensor Web Client assumes that the
     * selected SOS provides the 52°North discovery profile.
     * 
     * @param sosUrl
     *        the SOS service URL.
     * @param parameterConstellations 
     * @throws OXFException
     *         when request creation fails.
     * @throws InterruptedException if getting the DescribeSensor result gets interrupted.
     * @throws XMLHandlingException if parsing the DescribeSensor result fails
     * @throws IOException if reading the DescribeSensor result fails.
     * @throws IllegalStateException
     *         if SOS version is not supported.
     */
    private void performMetadataInterlinking(String sosUrl, Collection<ParameterConstellation> parameterConstellations) throws OXFException, InterruptedException, XMLHandlingException, IOException {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosUrl);
        ArrayList<Procedure> procedures = metadata.getProcedures();

        // do describe sensor for each procedure
        String sosVersion = metadata.getSosVersion();
        String smlVersion = metadata.getSensorMLVersion();
        Map<String, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<String, FutureTask<OperationResult>>();
        for (Procedure proc : procedures) {
        	SOSAdapter adapter = SosAdapterFactory.createSosAdapter(metadata);
            Operation operation = new Operation(SOSAdapter.DESCRIBE_SENSOR, sosUrl, sosUrl);
            ParameterContainer paramCon = new ParameterContainer();
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER, sosVersion);
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER, proc.getId());
            if (SosUtil.isVersion100(sosVersion)) {
                paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT, smlVersion);
            }
            else if (SosUtil.isVersion200(sosVersion)) {
                paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, smlVersion);
            }
            else {
                throw new IllegalStateException("SOS Version (" + sosVersion + ") is not supported!");
            }
            OperationAccessor opAccessorCallable = new OperationAccessor(adapter, operation, paramCon);
            futureTasks.put(proc.getId(), new FutureTask<OperationResult>(opAccessorCallable));
        }

        int i = 1;
        List<String> illegalProcedures = new ArrayList<String>();
        LOGGER.debug("Going to send #{} DescribeSensor requests.", futureTasks.size());
        for (String procedureId : futureTasks.keySet()) {
            ByteArrayInputStream incomingResultAsStream = null;
            try {
                LOGGER.trace("Sending request {}", i++);
                FutureTask<OperationResult> futureTask = futureTasks.get(procedureId);
                AccessorThreadPool.execute(futureTask);
                OperationResult opResult = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
                if (opResult == null) {
                    illegalProcedures.add(procedureId);
                    LOGGER.debug("Got NO sensor description for '{}'", procedureId);
                }
                else {
                    incomingResultAsStream = opResult.getIncomingResultAsStream();
                    DescribeSensorParser parser = new DescribeSensorParser(incomingResultAsStream, metadata);
                    
                    Procedure procedure = metadata.getProcedure(procedureId);
                    procedure.addAllRefValues(parser.parseCapsDataFields());
                    List<String> phenomenons = parser.getPhenomenons();
                    List<String> fois = parser.parseFOIReferences();
                    ParsedPoint point = parser.buildUpSensorMetadataPosition();
                    
                    double lat = Double.parseDouble(point.getLat());
                    double lng = Double.parseDouble(point.getLon());

                    
                    String srs = point.getSrs();
                    EastingNorthing eastingNorthing = new EastingNorthing(lng, lat);

                    if (fois.size() == 0) {
                    	metadata.getStation(fois.get(0)).setLocation(eastingNorthing, srs);
                    }

                    if (fois.isEmpty()) {
                        Collection<FeatureOfInterest> features = metadata.getFeatures();
                        LOGGER.warn("No FOI references found for procedure '{}'.", procedureId);
                        LOGGER.warn("==> Reference all ({}) available.", features.size());
                        for (FeatureOfInterest foi : features) {
                            fois.add(foi.getId());
                        }
                    }

					for (String foi : fois) {
						Station station = metadata.getStation(foi);
						if (station != null) {
							for (String phenomenon : phenomenons) {
								Collection<ParameterConstellation> paramConstellations = getMatchingConstellations(
										parameterConstellations, procedureId,
										phenomenon);

								station.setLocation(eastingNorthing, srs);
								for (ParameterConstellation paraCon : paramConstellations) {
									paraCon.setFeatureOfInterest(foi);
									station.addParameterConstellation(paraCon);
								}
							}
						} else {
							LOGGER.error(
									"Foi '{}' defined in SensorML document '{}' doesn't exist in capabilities",
									foi, procedure.getId());
						}
					}
					LOGGER.trace("Got Procedure data for '{}'.", procedure);
                }
            }
            catch (TimeoutException e) {
                LOGGER.warn("Could NOT connect to SOS '{}'.", sosUrl, e);
                illegalProcedures.add(procedureId);
            }
            catch (ExecutionException e) {
                LOGGER.warn("Could NOT get OperationResult from SOS for '{}'.", procedureId, e.getCause());
                illegalProcedures.add(procedureId);
            }
            catch (XmlException e) {
                LOGGER.warn("Could NOT parse OperationResult from '{}'", sosUrl, e);
                if (LOGGER.isDebugEnabled()) {
                    try {
                        XmlObject response = XmlObject.Factory.parse(incomingResultAsStream);
                        StringBuilder sb = new StringBuilder();
                        sb.append(String.format("Error sending DescribeSensor for '%s'\n", procedureId));
                        sb.append(String.format("Could NOT parse incoming OperationResult:\n %s", response));
                        LOGGER.debug(sb.toString(), e);
                        illegalProcedures.add(procedureId);
                    }
                    catch (XmlException ex) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(incomingResultAsStream));
                        LOGGER.warn("No XML response for procedure '{}'.", procedureId);
                        LOGGER.debug("First line of response: {}", reader.readLine());
                    }
                    catch (IOException ex) {
                        LOGGER.warn("Could read result", ex);
                    }
                }
            }
            catch (IOException e) {
                illegalProcedures.add(procedureId);
                LOGGER.info("Could NOT parse sensorML for procedure '{}'.", procedureId, e);
            }
            catch (IllegalStateException e) {
                illegalProcedures.add(procedureId);
                LOGGER.info("Could NOT link procedure '{}' appropriatly.", procedureId, e);
            }
            finally {
                if (incomingResultAsStream != null) {
                    incomingResultAsStream.close();
                }
                futureTasks.remove(procedureId);
                LOGGER.debug("Still #{} responses to go ...", futureTasks.size());
            }
        }

        if ( !illegalProcedures.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Removed procedures: \n");
            for (String procedure : illegalProcedures) {
            	LOGGER.error("Does not happened!!!");
                sb.append("Removed ==> ");
                sb.append(procedure);
                sb.append("\n");
            }
            LOGGER.warn("#{} procedures are unavailable. {}", illegalProcedures.size(), sb.toString());
        }

        LOGGER.info("Retrieved #{} stations from SOS '{}'", metadata.getStations().size(), sosUrl);
        metadata.setHasDonePositionRequest(true);
    }

    private Collection<ParameterConstellation> getMatchingConstellations(
			Collection<ParameterConstellation> parameterConstellations,
			String procedure, String phenomenon) {
		Collection<ParameterConstellation> result = new ArrayList<ParameterConstellation>();
    	for (ParameterConstellation paraCon : parameterConstellations) {
    		if (paraCon.hasProcedure(procedure) && paraCon.hasPhenomenon(phenomenon)) {
				result.add(paraCon);
			}
		}
		return result;
	}

	protected Collection<String> getFoisByProcedure(SOSMetadata metadata, String sosUrl, String sosVersion, String procedure)
			throws OXFException {
		ArrayList<String> fois = new ArrayList<String>();
		try {
			ParameterContainer container = new ParameterContainer();
			container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
			container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
			container.addParameterShell("procedure", procedure);
			Operation operation = new Operation(GET_FEATURE_OF_INTEREST,
					sosUrl, sosUrl);
			SOSAdapter adapter = SosAdapterFactory.createSosAdapter(metadata);
			OperationResult result = adapter.doOperation(operation, container);
			XmlObject foiResponse = XmlObject.Factory.parse(result
					.getIncomingResultAsStream());
			if (foiResponse instanceof GetFeatureOfInterestResponseDocument) {
				GetFeatureOfInterestResponseDocument foiResDoc = (GetFeatureOfInterestResponseDocument) foiResponse;
				for (FeaturePropertyType featurePropertyType : foiResDoc
						.getGetFeatureOfInterestResponse()
						.getFeatureMemberArray()) {
					SFSamplingFeatureDocument samplingFeature = SFSamplingFeatureDocument.Factory
							.parse(featurePropertyType.xmlText());
					SFSamplingFeatureType sfSamplingFeature = samplingFeature
							.getSFSamplingFeature();
					fois.add(sfSamplingFeature.getIdentifier()
							.getStringValue());
				}
			} else {
				throw new OXFException("No valid GetFeatureOfInterestREsponse");
			}
		} catch (Exception e) {
			LOGGER.error("Error while send GetFeatureOfInterest: "
					+ e.getCause());
			throw new OXFException(e);

		}
		return fois;
	}

    
}
