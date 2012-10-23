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

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
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
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.server.oxf.util.access.OperationAccessor;
import org.n52.server.oxf.util.access.oxfExtensions.SOSAdapter_OXFExtension;
import org.n52.server.oxf.util.access.oxfExtensions.SOSRequestBuilderFactory_OXFExtension;
import org.n52.server.oxf.util.connector.SOSConnector;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.ServiceMetadata;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSosConnector implements SOSConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSosConnector.class);

    public DefaultSosConnector() {
        // do nothin
    }

    public SOSMetadataResponse buildUpServiceMetadata(String sosUrl, String sosVersion) throws Exception {
        ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.generateRequestBuilder(sosVersion);
        SOSAdapter adapter = new SOSAdapter_OXFExtension(sosVersion, requestBuilder);
        ServiceDescriptor serviceDesc = ConnectorUtils.getServiceDescriptor(sosUrl, adapter);

        String sosTitle = serviceDesc.getServiceIdentification().getTitle();
        String omFormat = ConnectorUtils.getOMFormat(serviceDesc);
        String smlVersion = ConnectorUtils.getSMLVersion(serviceDesc, sosVersion);
        ConnectorUtils.setVersionNumbersToMetadata(sosUrl, sosTitle, sosVersion, omFormat, smlVersion);

        Map<String, ServiceMetadata> sosMetadatas = ConfigurationContext.getServiceMetadatas();

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
        for (int i = 0; i < contents.getDataIdentificationCount(); i++) {
            ObservationOffering offering = (ObservationOffering) contents.getDataIdentification(i);

            sosBbox = ConnectorUtils.createBbox(sosBbox, offering);

            String offeringID = offering.getIdentifier();

            // associate:
            String[] foiArray = offering.getFeatureOfInterest();
            offeringFoiMap.put(offeringID, foiArray);

            // associate:
            String[] procArray = offering.getProcedures();
            offeringProcMap.put(offeringID, procArray);

            // associate:
            String[] phenArray = offering.getObservedProperties();
            offeringPhenMap.put(offeringID, phenArray);

            // iterate over fois to delete double entries for the request
            for (int j = 0; j < foiArray.length; j++) {
                featureIds.add(foiArray[j]);
            }

        }
        LOGGER.debug("There are " + featureIds.size() + " FOIs registered in the SOS");

        SOSMetadata meta = (SOSMetadata) sosMetadatas.get(sosUrl);

        // add fois
        for (String featureId : featureIds) {
            meta.addFeature(new FeatureOfInterest(featureId));
        }

        try {
            if ( !sosBbox.getCRS().startsWith("EPSG")) {
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
     * <bf>Note:</bf> to create the associations between these key items the ThinSweClient assumes that the
     * selected SOS provides the 52°North discovery profile.
     * 
     * @param sosUrl
     *        the SOS service URL.
     * @throws Exception
     *         when request creation fails.
     */
    private void performMetadataInterlinking(String sosUrl) throws Exception {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosUrl);
        ArrayList<Procedure> procedures = metadata.getProcedures();

        // do describe sensor for each procedure
        String sosVersion = metadata.getSosVersion();
        String smlVersion = metadata.getSensorMLVersion();
        Map<String, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<String, FutureTask<OperationResult>>();
        for (Procedure proc : procedures) {
            ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.generateRequestBuilder(sosVersion);
            SOSAdapter_OXFExtension adapter = new SOSAdapter_OXFExtension(sosVersion, requestBuilder);
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
                TimeUnit unit = TimeUnit.MILLISECONDS;
                long timeout = ConfigurationContext.SERVER_TIMEOUT;
                OperationResult opResult = futureTask.get(timeout, unit);
                if (opResult == null) {
                    illegalProcedures.add(procedureId);
                    LOGGER.debug("Got NO sensor description for '{}'", procedureId);
                }
                else {
                    incomingResultAsStream = opResult.getIncomingResultAsStream();
                    DescribeSensorParser parser = new DescribeSensorParser(incomingResultAsStream, sosVersion);
                    Procedure procedure = metadata.getProcedure(procedureId);
                    Set<Station> stations = metadata.getStationsByProcedure(procedure.getId());
                    procedure.addAllRefValues(parser.parseCapsDataFields());
                    List<String> phenomenons = parser.getPhenomenons();
                    List<String> fois = parser.parseFOIReferences();
                    ParsedPoint point = parser.buildUpSensorMetadataPosition();
                    double lat = Double.parseDouble(point.getLat());
                    double lng = Double.parseDouble(point.getLon());
                    EastingNorthing coords = new EastingNorthing(lng, lat);
                    String srs = point.getSrs();

                    if (fois.isEmpty()) {
                        Collection<FeatureOfInterest> features = metadata.getFeatures();
                        LOGGER.warn("No FOI references found for procedure '{}'.", procedureId);
                        LOGGER.warn("==> Reference all ({}) available.", features.size());
                        for (FeatureOfInterest foi : features) {
                            fois.add(foi.getId());
                        }
                    }
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
                                            station.setFeature(foi);
                                            station.setLocation(coords, srs);
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
                            station.setLocation(coords, srs);
                            for (String foi : fois) {
                                Station stationClone = station.clone();
                                stationClone.setFeature(foi);
                                metadata.addStation(stationClone);
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
                        LOGGER.debug("DescribeSensor Response for '{}' is no XML.", procedureId);
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
                sb.append(" | \n");
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
        Collection<Station> stations = metadata.getStations();
        Iterator<Station> iterator = stations.iterator();
        while (iterator.hasNext()) {
            Station station = iterator.next();
            if ( !station.hasAllEntries()) {
                iterator.remove();
            }
        }
    }

}
