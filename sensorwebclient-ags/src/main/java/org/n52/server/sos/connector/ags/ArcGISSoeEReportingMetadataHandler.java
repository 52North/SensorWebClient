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

import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.DESCRIBE_SENSOR;
import static org.n52.server.util.XmlHelper.getRelatedFeatures;
import static org.n52.server.util.XmlHelper.getRelatedPhenomena;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.opengis.sensorML.x101.ComponentType;

import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.da.MetadataHandler;
import org.n52.server.util.XmlHelper;
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

    private static final String smlVersion = "http://www.opengis.net/sensorML/1.0.1";

    /**
     * Component descriptions parsed from a sensor network. Each contain information how to associate
     * timeseries parameters. Perform a DescribeSensor on each instance to get more detailed information.
     */
    private Map<String, ComponentType> sensorDescriptions;

    private Map<String, Point> featureLocations;

    private String sosVersion = "2.0.0";

    private String serviceUrl;

    public ArcGISSoeEReportingMetadataHandler(SOSMetadata metadata) {
        super(metadata);
        this.sosVersion = metadata.getVersion();
        this.serviceUrl = metadata.getServiceUrl();
        this.sensorDescriptions = new HashMap<String, ComponentType>();
        this.featureLocations = new HashMap<String, Point>();
    }

    @Override
    public SOSMetadata performMetadataCompletion(String sosUrl, String sosVersion) throws Exception {
        SOSMetadata metadata = initMetadata(sosUrl, sosVersion);

        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(sosUrl);

        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        for (SosTimeseries timeseries : observingTimeseries) {
            Procedure procedure = timeseries.getProcedure();
            ComponentType component = sensorDescriptions.get(procedure.getProcedureId());
            Phenomenon[] phenomena = getRelatedPhenomena(component.getOutputs());
            if (!relatesToPhenomena(timeseries, phenomena)) {
                continue;
            }

            // get feature relations
            if (component.getCapabilitiesArray().length > 0) {
                String[] fois = getRelatedFeatures(component.getCapabilitiesArray(0));
                for (String featureId : fois) {
                    Station station = metadata.getStation(featureId);
                    if (station == null) {
                        Point location = featureLocations.get(featureId);
                        station = new Station(featureId, sosUrl);
                        station.setLocation(location);
                        metadata.addStation(station);
                    }
                    
                    String label = featureId;
//                    if (sfSamplingFeature.getNameArray().length > 0) {
//                        label = sfSamplingFeature.getNameArray(0).getStringValue();
//                    } 
                    
                    Feature feature = new Feature(featureId, sosUrl);
                    feature.setLabel(label);
                    lookup.addFeature(feature);
                    
                    SosTimeseries tmp = timeseries.clone();
                    tmp.setFeature(new Feature(featureId, sosUrl));
                    station.addTimeseries(tmp);
                }
                
            } else {
                LOGGER.info("Procedure '{}' does not link to any feature.", procedure.getProcedureId());
            }

            // get phenomenona descriptions

            // get aggregation types
            

        }

        // TODO Auto-generated method stub
        
        
        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
        return metadata;
    }

    private boolean relatesToPhenomena(SosTimeseries timeseries, Phenomenon[] phenomena) {
        return Arrays.binarySearch(phenomena, timeseries.getPhenomenon()) >= 0;
    }

    @Override
    protected String[] getProceduresFor(ObservationOffering offering) {
        try {
            // SOS 2.0.0 has just one mandatory procedure id
            performDescribeSensor(offering.getProcedures()[0]);
            return sensorDescriptions.keySet().toArray(new String[0]);
        } catch (Exception e) {
            LOGGER.error("Could not get procedure description for offering {}", offering.getIdentifier(), e);
            return new String[0];
        }
    }

    /**
     * Performs a DescribeSensor request and caches the procedure description via {@link #networkParser}. If
     * procedure is already known no further request is being sent. Get the procedure description from the
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
            paramCon.addParameterShell(DESCRIBE_SENSOR_VERSION_PARAMETER, sosVersion);
            paramCon.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_PARAMETER, procedure);
            paramCon.addParameterShell(DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, smlVersion);
            Operation operation = new Operation(DESCRIBE_SENSOR, serviceUrl, serviceUrl);
            OperationResult result = getSosAdapter().doOperation(operation, paramCon);

            SensorNetworkParser networkParser = new SensorNetworkParser();
            sensorDescriptions = networkParser.parseSensorDescriptions(result.getIncomingResultAsStream());
        }
    }

    public boolean isCached(String procedure) {
        return sensorDescriptions.containsKey(procedure);
    }
    
    protected void performGetFeatureOfInterest(String procedure) throws OXFException, ExceptionReport {
        if ( !isCached(procedure)) {
            ParameterContainer paramCon = new ParameterContainer();
            paramCon.addParameterShell(ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER, "SOS");
            paramCon.addParameterShell(ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER, sosVersion);
            Operation operation = new Operation("GetFeatureOfInterest", serviceUrl, serviceUrl);
            OperationResult result = getSosAdapter().doOperation(operation, paramCon);
            
            CRSUtils crsHelper = CRSUtils.createEpsgStrictAxisOrder();
            FeatureParser featureParser = new FeatureParser(crsHelper);
            featureLocations = featureParser.parseFeatures(result.getIncomingResultAsStream());
        }
    }

    @Override
    public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

}
