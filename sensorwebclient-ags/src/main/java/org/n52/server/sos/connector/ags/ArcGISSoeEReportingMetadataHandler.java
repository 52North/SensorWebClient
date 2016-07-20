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
package org.n52.server.sos.connector.ags;

import com.vividsolutions.jts.geom.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ComponentDocument;
import net.opengis.sensorML.x101.ComponentType;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import org.apache.xmlbeans.XmlObject;
import static org.n52.io.crs.CRSUtils.createEpsgStrictAxisOrder;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.DESCRIBE_SENSOR;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.da.MetadataHandler;
import static org.n52.server.da.oxf.DescribeSensorAccessor.getSensorDescriptionAsSensorML;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;
import org.n52.server.util.PropertiesToHtml;
import org.n52.server.util.XmlHelper;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosService;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcGISSoeEReportingMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArcGISSoeEReportingMetadataHandler.class);

    private static final String SML_NAMESPACE = "http://www.opengis.net/sensorML/1.0.1";

    private static final Map<String, String> namespaceDeclarations = new HashMap<String, String>();

    static {
        namespaceDeclarations.put("sml", SML_NAMESPACE);
        namespaceDeclarations.put("swe", "http://www.opengis.net/swe/1.0.1");
        namespaceDeclarations.put("aqd", "http://aqd.ec.europa.eu/aqd/0.3.7c");
        namespaceDeclarations.put("base", "http://inspire.ec.europa.eu/schemas/base/3.3rc3/");
    }

    private final XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);

    /**
     * Component descriptions parsed from a sensor network. Each contain information how to associate
     * timeseries parameters. Perform a DescribeSensor on each instance to get more detailed information.
     */
    private final Map<String, ComponentType> sensorDescriptions;

    /**
     * Holds all network procedures referenced in the capabilities document. Holding networks is needed
     * so that features can be requested network for network to avoid one gigantic GetFoi request.
     */
    private final Map<String, Network> networks = new HashMap<String, Network>();

    public ArcGISSoeEReportingMetadataHandler(SOSMetadata metadata) {
        super(metadata);
        this.sensorDescriptions = new HashMap<String, ComponentType>();
    }

    @Override
    public void assembleTimeseriesMetadata(TimeseriesProperties properties) throws Exception {
        SosTimeseries timeseries = properties.getTimeseries();
        SOSMetadata sosMetadata = getSOSMetadata(timeseries.getServiceUrl());

        Map<String, String> tsMetadata = new HashMap<String, String>();
        XmlObject sml = getSensorDescriptionAsSensorML(timeseries.getProcedureId(), sosMetadata);
        ArcGISSoeDescribeSensorParser parser = new ArcGISSoeDescribeSensorParser(sml);

        String phenomenonId = timeseries.getPhenomenonId();
        String uom = parser.getUomFor(phenomenonId);
        String shortName = parser.getShortName();

        tsMetadata.put("UOM", uom);
        tsMetadata.put("Name", shortName);
        PropertiesToHtml toHtml = PropertiesToHtml.createFromProperties(tsMetadata);

        properties.setMetadataUrl(toHtml.create(timeseries));
        properties.setUnitOfMeasure(uom);
    }

    @Override
    public SOSMetadata performMetadataCompletion() throws Exception {
        String sosUrl = getServiceUrl();
        SOSMetadata metadata = initMetadata();

        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(sosUrl);
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        Map<Feature, Point> featureLocations = performGetFeatureOfInterest(lookup);

        // TODO check integrety before adding them to metadata
        List<Station> stations = new ArrayList<Station>();

        LOGGER.debug("Destillate timeseries from #{} potential. This may take a while.", observingTimeseries.size());
        for (Network network : networks.values()) {
            LOGGER.trace("############# PROCESS NETWORK #################");
            LOGGER.debug("Build up cache for sensor network '{}'", network);

            String offeringId = network.getOffering();
            for (String procedureId : network.getMembers()) {
                ComponentType component = sensorDescriptions.get(procedureId);
                completeProcedure(lookup.getProcedure(procedureId), component);

                if (component.getCapabilitiesArray() == null) {
                    LOGGER.trace("No related features in capabilities block => Link all features available!");
                    LOGGER.warn("Not yet implemented.");

                    // TODO link all features available

                    continue;
                }

                SosTimeseries timeseries = new SosTimeseries();
                timeseries.setOffering(lookup.getOffering(offeringId));
                timeseries.setProcedure(lookup.getProcedure(procedureId));

                // get phenomenon/category labels
                Outputs outputs = component.getOutputs();
                Capabilities sensorCapabilties = component.getCapabilitiesArray(0);
                for (String phenomenonId : xmlHelper.getRelatedPhenomena(outputs)) {
                    Phenomenon phenomenon = lookup.getPhenomenon(phenomenonId);
                    timeseries.setPhenomenon(phenomenon);

                    OutputList outputList = outputs.getOutputList();
                    if (outputList.getOutputArray().length > 0) {
                        ArcGISSoeDescribeSensorParser parser = createSensorMLParser(component);
                        String uom = parser.getUomFor(phenomenonId);
						phenomenon.setUnitOfMeasure(uom); // actually wrong but stay backward compatible
						timeseries.setUom(uom);
                        String name = outputList.getOutputArray(0).getName();
                        timeseries.setCategory(new Category(parseCategory(name), sosUrl));
                        phenomenon.setLabel(name);
                    }

                    // get feature relations
                    String[] fois = xmlHelper.getRelatedFeatures(sensorCapabilties);
                    for (String featureId : fois) {
                        if ( !lookup.containsFeature(featureId)) {
                            // orphaned timeseries (i.e. no station)
                            continue;
                        }
                        Feature feature = lookup.getFeature(featureId);
                        Station station = metadata.getStation(featureId);
                        if (station == null) {
                            Point location = featureLocations.get(feature);
                            LOGGER.trace("Create Station '{}' at '{}'.", featureId, location);
                            station = new Station(featureId, sosUrl);
                            station.setLocation(location);
                            metadata.addStation(station);
                        }
                        // get existing station
                        station = metadata.getStation(featureId);

                        SosTimeseries copy = timeseries.clone();
                        copy.setFeature(new Feature(featureId, sosUrl));

                        String service = metadata.getServiceUrl();
                        String version = metadata.getVersion();
                        copy.setSosService(new SosService(service, version));

                        LOGGER.trace("+++++++++++++ NEW TIMESERIES +++++++++++++++++");
                        LOGGER.trace("New Timeseries: '{}'.", copy.toString());
                        LOGGER.trace("Timeseries with procedure '{}'.", lookup.getProcedure(procedureId));
                        LOGGER.trace("Relate with phenomenon '{}'.", lookup.getPhenomenon(phenomenonId));
                        LOGGER.trace("Relate with offering '{}'.", lookup.getOffering(offeringId));
                        LOGGER.trace("Relate with feature '{}'.", lookup.getFeature(featureId));
                        LOGGER.trace("Relate with service '{}' ({}).", service, version);
                        LOGGER.trace("With category '{}'.", copy.getCategory());
                        LOGGER.trace("Add to station '{}'.", station.getLabel());
                        LOGGER.trace("++++++++++++++++++++++++++++++++++++++++++++++");
                        station.addTimeseries(copy);
                    }
                }
            }

            LOGGER.trace("##############################################");
        }

//        for (Iterator<SosTimeseries> it = observingTimeseries.iterator(); it.hasNext();) {
//            SosTimeseries timeseries = it.next();
//            if ( !timeseries.parametersComplete()) {
//                LOGGER.trace("Remove timeseries '{}' as it is incomplete.", timeseries.toString());
//                it.remove();
//            }
//        }

        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
        return metadata;
    }

    private ArcGISSoeDescribeSensorParser createSensorMLParser(ComponentType component) {
        SensorMLDocument smlTemp = SensorMLDocument.Factory.newInstance();
        Member smlMember = smlTemp.addNewSensorML().addNewMember();
        smlMember.setProcess(component);
        return new ArcGISSoeDescribeSensorParser(smlTemp);
    }

    /**
     * Parses content of the last group of braces, i.e. <code>(content)</code>. If no braces are available at
     * all, the whole passed parameter is returned.
     *
     * @param phenomenonLabel
     *        to parse content from.
     * @return the content of the last brace group.
     */
    protected String parseCategory(String phenomenonLabel) {
        Pattern pattern = Pattern.compile("\\((.*)\\)$"); // (<category>)
        Matcher matcher = pattern.matcher(parseLastBraceGroup(phenomenonLabel));
        return matcher.find() ? matcher.group(1) : phenomenonLabel;
    }

    private String parseLastBraceGroup(String phenomenonLabel) {
        int groupStart = phenomenonLabel.lastIndexOf("(");
        if (groupStart < 0) {
            return phenomenonLabel;
        }
        int groupEnd = phenomenonLabel.length();
        return phenomenonLabel.substring(groupStart, groupEnd);
    }

    private void completeProcedure(Procedure procedure, ComponentType component) {
        Identification identification = component.getIdentificationArray(0);
        procedure.setLabel(xmlHelper.getShortName(identification.getIdentifierList()));
    }

    private boolean relatesToPhenomena(SosTimeseries timeseries, String[] phenomena) {
        return Arrays.asList(phenomena).contains(timeseries.getPhenomenonId());
    }

    @Override
    protected String[] getProceduresFor(ObservationOffering theOffering) {
        String offering = theOffering.getIdentifier();
        try {
            // SOS 2.0.0 has just one mandatory procedure id
            String network = theOffering.getProcedures()[0];
            Set<String> members = describeSensorNetwork(network);
            if (members.size() > 0) {
                Network theNetwork = new Network(offering, network);
                theNetwork.setMembers(members.toArray(new String[0]));
                networks.put(offering, theNetwork);
            }
            return members.toArray(new String[0]);
        }
        catch (OXFException e) {
            LOGGER.error("Could not get procedure description for offering {}", offering, e);
            return new String[0];
        } catch (ExceptionReport e) {
            LOGGER.error("Could not get procedure description for offering {}", offering, e);
            return new String[0];
        }
    }

    /**
     * Performs a DescribeSensor request and caches the procedure description within
     * {@link #sensorDescriptions}. If procedure is already known no further DescribeSensor request will be
     * sent.
     *
     * @param procedure
     *        the procedure id which SensorDescription is needed.
     * @return the procedure id(s).
     * @throws OXFException
     *         when request preparation failed.
     * @throws ExceptionReport
     *         when request processing failed.
     */
    protected Set<String> describeSensorNetwork(String procedure) throws OXFException, ExceptionReport {
        ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell(DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
        paramCon.addParameterShell(DESCRIBE_SENSOR_VERSION_PARAMETER, getServiceVersion());
        paramCon.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_PARAMETER, procedure);
        paramCon.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, SML_NAMESPACE);
        Operation operation = new Operation(DESCRIBE_SENSOR, getServiceUrl(), getServiceUrl());
        OperationResult result = getSosAdapter().doOperation(operation, paramCon);

        InputStream stream = result.getIncomingResultAsStream();
        SensorNetworkParser networkParser = new SensorNetworkParser();
        Map<String, ComponentType> descriptions = networkParser.parseSensorDescriptions(stream);
        sensorDescriptions.putAll(descriptions);
        return descriptions.keySet();
    }

    protected Map<Feature, Point> performGetFeatureOfInterest(TimeseriesParametersLookup lookup) throws OXFException,
            ExceptionReport {
        HashMap<Feature, Point> features = new HashMap<Feature, Point>();
        for (Network network : networks.values()) {
            ParameterContainer paramCon = new ParameterContainer();
            paramCon.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
            paramCon.addParameterShell(GET_FOI_VERSION_PARAMETER, getServiceVersion());
            paramCon.addParameterShell("procedure", network.getNetwork());
            Operation operation = new Operation("GetFeatureOfInterest", getServiceUrl(), getServiceUrl());
            
            try {
            	OperationResult result = getSosAdapter().doOperation(operation, paramCon);

                FeatureParser parser = new FeatureParser(getServiceUrl(), createEpsgStrictAxisOrder());
                features.putAll(parser.parseFeatures(result.getIncomingResultAsStream()));
            }
            catch (OXFException e) {
            	LOGGER.warn("Exception in OXF layer while executing operation", e);
            }
            catch (ExceptionReport e) {
                // TODO probably we do have to handle an ExceedsSizeLimitException here
            	LOGGER.warn("Service returned an ExceptionReport", e);
            }
        }
        for (Feature feature : features.keySet()) {
            lookup.addFeature(feature);
        }
        return features;
    }

    public boolean isCached(String procedure) {
        return sensorDescriptions.containsKey(procedure);
    }

    @Override
    public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

}
