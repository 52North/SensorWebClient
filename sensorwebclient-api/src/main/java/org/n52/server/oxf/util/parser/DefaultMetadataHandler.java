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
import static org.n52.server.oxf.util.ConfigurationContext.SERVER_TIMEOUT;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.n52.oxf.ows.ServiceDescriptor;
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
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMetadataHandler.class);

    public SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws OXFException, InterruptedException, XMLHandlingException {
    	SOSMetadata sosMetadata = ConfigurationContext.getServiceMetadatas().get(sosUrl);
    	SOSAdapter adapter = SosAdapterFactory.createSosAdapter(sosMetadata);  
    	
        ServiceDescriptor serviceDesc = ConnectorUtils.getServiceDescriptor(sosUrl, adapter);

        String sosTitle = serviceDesc.getServiceIdentification().getTitle();
        String omFormat = ConnectorUtils.getOMFormat(serviceDesc);
        String smlVersion = ConnectorUtils.getSMLVersion(serviceDesc, sosVersion);
        ConnectorUtils.setVersionNumbersToMetadata(sosUrl, sosTitle, sosVersion, omFormat, smlVersion);

        Map<String, SOSMetadata> sosMetadatas = ConfigurationContext.getServiceMetadatas();
        
        //
        // build up associations:
        //

        // association: Offering - FOIs
        Map<String, String[]> offeringFoiMap = new HashMap<String, String[]>();

        // association: Offering - Procedures
        Map<String, String[]> offeringProcMap = new HashMap<String, String[]>();

        // association: Offering - Phenomenons
        Map<String, String[]> offeringPhenMap = new HashMap<String, String[]>();

        IBoundingBox sosBbox = null;
        HashSet<String> featureIds = new HashSet<String>();
        Contents contents = serviceDesc.getContents();
        SOSMetadata meta = (SOSMetadata) sosMetadatas.get(sosUrl);

        for (int i = 0; i < contents.getDataIdentificationCount(); i++) {
            ObservationOffering offering = (ObservationOffering) contents.getDataIdentification(i);

            sosBbox = ConnectorUtils.createBbox(sosBbox, offering);

            try {
                if ( sosBbox != null && !sosBbox.getCRS().startsWith("EPSG")) {
                    String tmp = "EPSG:" + sosBbox.getCRS().split(":")[sosBbox.getCRS().split(":").length - 1];
                    meta.setSrs(tmp);
                }
                else {
                    meta.setSrs(sosBbox.getCRS());
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
            		foiArray = getFoisByProcedure(adapter, sosUrl, sosVersion, procedure).toArray(foiArray);
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
            meta.addFeature(new FeatureOfInterest(featureId));
        }

        // FOI -> Procedure -> Phenomenon
        for (String offeringId : offeringFoiMap.keySet()) {
            for (String procedure : offeringProcMap.get(offeringId)) {
                if (procedure.contains("urn:ogc:generalizationMethod:")) {
                    meta.setCanGeneralize(true);
                }
                else {
                    for (String phenomenon : offeringPhenMap.get(offeringId)) {
                        /*
                         * add a station for a procedure expecting that there is only one for each right now.
                         * Further stations may be added later when additional information is parsed from
                         * getFeatureOfInterest of describeSensor operations.
                         */
                        Station station = new Station();
                        station.setPhenomenon(phenomenon);
                        station.setProcedure(procedure);
                        station.setOffering(offeringId);
                        meta.addStation(station);
                    }
                    // add procedures
                    meta.addProcedure(new Procedure(procedure));
                    for (String phenomenonId : offeringPhenMap.get(offeringId)) {
                        meta.addPhenomenon(new Phenomenon(phenomenonId));
                    }
                }
            }
            // add offering
            Offering offering = new Offering(offeringId);
            meta.addOffering(offering);
        }

        meta.setInitialized(true);

        // XXX hack to get conjunctions between procedures and fois
        if ( !meta.hasDonePositionRequest()) {
            try {
                performMetadataInterlinking(sosUrl);
            }
            catch (IOException e) {
                LOGGER.warn("Could not retrieve relations between procedures and fois", e);
            }
        }

        LOGGER.debug("Got metadata for SOS " + meta.getId());
        return new SOSMetadataResponse(meta);
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
     * @throws OXFException
     *         when request creation fails.
     * @throws InterruptedException if getting the DescribeSensor result gets interrupted.
     * @throws XMLHandlingException if parsing the DescribeSensor result fails
     * @throws IOException if reading the DescribeSensor result fails.
     * @throws IllegalStateException
     *         if SOS version is not supported.
     */
    private void performMetadataInterlinking(String sosUrl) throws OXFException, InterruptedException, XMLHandlingException, IOException {
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

                    // TODO
//                    Station station = new Station();
//                    station.setLocation(eastingNorthing, srs);

                    if (fois.isEmpty()) {
                        Collection<FeatureOfInterest> features = metadata.getFeatures();
                        LOGGER.warn("No FOI references found for procedure '{}'.", procedureId);
                        LOGGER.warn("==> Reference all ({}) available.", features.size());
                        for (FeatureOfInterest foi : features) {
                            fois.add(foi.getId());
                        }
                    }

                    Set<Station> stations = metadata.getStationsByProcedure(procedure.getId());
                    if (fois.size() == stations.size()) {
                        /*
                         * Amount of sampling locations already matches amount of stations. A station update
                         * is sufficient.
                         */
                        for (String foi : fois) {
                            for (String phenomenon : phenomenons) {
                                for (Station station : stations) {
                                    if (station.isPhenomenonEqual(phenomenon)) {
                                        if (metadata.getFeatureHashMap().containsKey(foi)) {
                                            // TODO
//                                            ParameterConstellation constellation = new ParameterConstellation();
//                                            constellation.setFeatureOfInterest(foi);
//                                            constellation.setPhenomenon(phenomenon);
//                                            constellation.setProcedure(procedureId);
//                                            constellation.setOffering(offering);
//                                            station.addParameterConstellation(constellation);
                                            station.setFeature(foi);
                                            station.setLocation(eastingNorthing, srs);
                                        }
                                    }
                                }
                            }
                            LOGGER.trace("Got Procedure data for '{}'.", procedure);
                        }
                    }
                    else {
                        /*
                         * Not all stations linked with the procedure are created yet. (Re-)Create new
                         * stations for each new parameter constellation coming from the linked FOIs parsed
                         * from sensorML.
                         */
                        for (Station station : stations) {
                            station.setLocation(eastingNorthing, srs);
                            for (String foi : fois) {
                                if (station.getFeature() == null) {
                                    station.setFeature(foi);
                                    metadata.addStation(station.clone());
                                } else {
                                    Station clone = station.clone();
                                    clone.setFeature(foi);
                                    metadata.addStation(clone);
                                }
                            }
                        }
                    }
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

        removeIncompleteStations(metadata);

        if ( !illegalProcedures.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Removed procedures: \n");
            for (String procedure : illegalProcedures) {
                metadata.removeProcedure(procedure);
                sb.append("Removed ==> ");
                sb.append(procedure);
                sb.append("\n");
            }
            LOGGER.warn("#{} procedures are unavailable. {}", illegalProcedures.size(), sb.toString());
        }

        LOGGER.info("Retrieved #{} stations from SOS '{}'", metadata.getStations().size(), sosUrl);
        metadata.setHasDonePositionRequest(true);

    }

    /**
     * Cleans up incomplete information containers from metadata. <br>
     * <br>
     * Incomplete stations do not necessarily indicate false service modelling. Because of linking parameter
     * information over several steps (GetCapabilities, DescribeSensor) some information snippets are used to
     * complete parameter linking but also for using such snippets as a template for more complex paramater
     * setups (like 1:n relationships between procesure and FOI).
     * 
     * @param metadata
     *        the stations containing metadata
     */
    private static void removeIncompleteStations(SOSMetadata metadata) {
    	// TODO check why the stations are not really removed (Belgium-SOS)
        Collection<Station> stations = metadata.getStations();
        Iterator<Station> iterator = stations.iterator();
        while (iterator.hasNext()) {
            Station station = iterator.next();
            if ( !station.hasAllEntries()) {
                iterator.remove();
            }
        }
    }
    
	protected Collection<String> getFoisByProcedure(SOSAdapter adapter,
			String sosUrl, String sosVersion, String procedure)
			throws OXFException {
		ArrayList<String> fois = new ArrayList<String>();
		try {
			ParameterContainer container = new ParameterContainer();
			container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
			container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
			container.addParameterShell("procedure", procedure);
			Operation operation = new Operation(GET_FEATURE_OF_INTEREST,
					sosUrl, sosUrl);
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
