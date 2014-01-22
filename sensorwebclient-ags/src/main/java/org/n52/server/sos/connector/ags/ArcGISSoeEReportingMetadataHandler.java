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

import static org.n52.io.crs.CRSUtils.createEpsgStrictAxisOrder;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.DESCRIBE_SENSOR;
import static org.n52.server.da.oxf.DescribeSensorAccessor.getSensorDescriptionAsSensorML;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.ComponentType;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.OutputsDocument.Outputs;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;

import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.da.MetadataHandler;
import org.n52.server.util.PropertiesToHtml;
import org.n52.server.util.XmlHelper;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public class ArcGISSoeEReportingMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArcGISSoeEReportingMetadataHandler.class);

    private static final String SML_NAMESPACE = "http://www.opengis.net/sensorML/1.0.1";

    private static final Map<String, String> namespaceDeclarations = new HashMap<String, String>();

    {
        namespaceDeclarations.put("sml", SML_NAMESPACE);
        namespaceDeclarations.put("swe", "http://www.opengis.net/swe/1.0.1");
        namespaceDeclarations.put("aqd", "http://aqd.ec.europa.eu/aqd/0.3.7c");
        namespaceDeclarations.put("base", "http://inspire.ec.europa.eu/schemas/base/3.3rc3/");
    }

    private XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);

    /**
     * Component descriptions parsed from a sensor network. Each contain information how to associate
     * timeseries parameters. Perform a DescribeSensor on each instance to get more detailed information.
     */
    private Map<String, ComponentType> sensorDescriptions;

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

        for (SosTimeseries timeseries : observingTimeseries) {
            Procedure procedure = timeseries.getProcedure();
            ComponentType component = sensorDescriptions.get(procedure.getProcedureId());
            completeProcedure(procedure, component);

            /*
             * TODO phenomenon relations has to be checked as MetadataHandler creates a timeseries
             * offering-procedure-phenomenon relation for each phenomenon in an offering (this however is not
             * true in all cases).
             */
            Outputs outputs = component.getOutputs();
            String[] phenomena = xmlHelper.getRelatedPhenomena(outputs);
            if ( !relatesToPhenomena(timeseries, phenomena)) {
                continue;
            }

            // get phenomenon/category labels
            for (String phenomenonId : phenomena) {
                OutputList outputList = outputs.getOutputList();
                Phenomenon phenomenon = lookup.getPhenomenon(phenomenonId);
                if (outputList.getOutputArray().length > 0) {
                    String name = outputList.getOutputArray(0).getName();
                    timeseries.setCategory(parseCategory(name));
                    phenomenon.setLabel(name);
                }
            }

            // get feature relations
            if (component.getCapabilitiesArray().length > 0) {
                Capabilities sensorCapabilties = component.getCapabilitiesArray(0);
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
                        station = new Station(feature.getLabel(), sosUrl);
                        station.setLocation(location);
                        metadata.addStation(station);
                    }

                    SosTimeseries tmp = timeseries.clone();
                    tmp.setFeature(new Feature(featureId, sosUrl));
                    station.addTimeseries(tmp);
                }
            }
            else {
                LOGGER.info("Procedure '{}' does not link to any feature.", procedure.getProcedureId());
            }
        }

        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
        return metadata;
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
    protected String[] getProceduresFor(ObservationOffering offering) {
        try {
            // SOS 2.0.0 has just one mandatory procedure id
            performDescribeSensor(offering.getProcedures()[0]);
            return sensorDescriptions.keySet().toArray(new String[0]);
        }
        catch (Exception e) {
            LOGGER.error("Could not get procedure description for offering {}", offering.getIdentifier(), e);
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
     * @throws OXFException
     *         when request preparation failed.
     * @throws ExceptionReport
     *         when request processing failed.
     */
    protected void performDescribeSensor(String procedure) throws OXFException, ExceptionReport {
        if ( !isCached(procedure)) {
            ParameterContainer paramCon = new ParameterContainer();
            paramCon.addParameterShell(DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
            paramCon.addParameterShell(DESCRIBE_SENSOR_VERSION_PARAMETER, getServiceVersion());
            paramCon.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_PARAMETER, procedure);
            paramCon.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, SML_NAMESPACE);
            Operation operation = new Operation(DESCRIBE_SENSOR, getServiceUrl(), getServiceUrl());
            OperationResult result = getSosAdapter().doOperation(operation, paramCon);

            SensorNetworkParser networkParser = new SensorNetworkParser();
            sensorDescriptions = networkParser.parseSensorDescriptions(result.getIncomingResultAsStream());
        }
    }

    protected Map<Feature, Point> performGetFeatureOfInterest(TimeseriesParametersLookup lookup) throws OXFException,
            ExceptionReport {
        ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
        paramCon.addParameterShell(GET_FOI_VERSION_PARAMETER, getServiceVersion());
        Operation operation = new Operation("GetFeatureOfInterest", getServiceUrl(), getServiceUrl());
        OperationResult result = getSosAdapter().doOperation(operation, paramCon);

        FeatureParser parser = new FeatureParser(getServiceUrl(), createEpsgStrictAxisOrder());
        Map<Feature, Point> features = parser.parseFeatures(result.getIncomingResultAsStream());
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
