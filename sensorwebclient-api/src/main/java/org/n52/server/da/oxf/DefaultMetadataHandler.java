/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License version 2 as publishedby the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the following licenses, the combination of
 * the program with the linked library is not considered a "derivative work" of the program:
 *
 * - Apache License, version 2.0 - Apache Software License, version 1.0 - GNU Lesser General Public License, version 3 -
 * Mozilla Public License, versions 1.0, 1.1 and 2.0 - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under the aforementioned licenses, is
 * permitted by the copyright holders if the distribution is compliant with both the GNU General Public License version
 * 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.server.da.oxf;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.da.oxf.DescribeSensorAccessor.getSensorDescriptionAsSensorML;
import static org.n52.server.mgmt.ConfigurationContext.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.MetadataHandler;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public class DefaultMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMetadataHandler.class);

    public DefaultMetadataHandler(final SOSMetadata metadata) {
        super(metadata);
    }

    /*
     * Assembles timeseries' metadata by performing DescribeSensor request and parsing the returned  SensorML.
     *
     * @see org.n52.server.da.MetadataHandler#assembleTimeseriesMetadata(org.n52.shared.serializable.pojos.TimeseriesProperties)
     */
    @Override
    public void assembleTimeseriesMetadata(final TimeseriesProperties properties) throws Exception {
        final SosTimeseries timeseries = properties.getTimeseries();
        final SOSMetadata sosMetadata = getSOSMetadata(timeseries.getServiceUrl());
        final XmlObject sml = getSensorDescriptionAsSensorML(timeseries.getProcedureId(), sosMetadata);
        final DescribeSensorParser parser = new DescribeSensorParser(sml.newInputStream(), sosMetadata);

        final String phenomenonId = timeseries.getPhenomenonId();
        properties.setUnitOfMeasure(parser.buildUpSensorMetadataUom(phenomenonId));
        final String url = parser.buildUpSensorMetadataHtmlUrl(properties.getTimeseries());
        properties.addAllRefValues(parser.parseReferenceValues());
        properties.setMetadataUrl(url);
    }

    @Override
    public SOSMetadata performMetadataCompletion() throws OXFException,
            InterruptedException,
            XMLHandlingException {

        final SOSMetadata sosMetadata = initMetadata();
        final Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(getServiceVersion());

        normalizeDefaultCategories(observingTimeseries);

        // TODO check version 2.0.0 sos's
        // XXX hack to get conjunctions between procedures and fois
        if (!sosMetadata.hasDonePositionRequest()) {
            try {
                performMetadataInterlinking(observingTimeseries);
            } catch (final IOException e) {
                LOGGER.warn("Could not retrieve relations between procedures and fois", e);
            }
        }
        sosMetadata.setInitialized(true);
        return sosMetadata;
    }

    /**
     * Performs a DescribeSensor request for every offered procedure of an SOS. This is intended to obtain the concrete
     * references between each offering, procedure, featureOfInterest and observedProperty (aka phenomenon). <br>
     * <br>
     * <bf>Note:</bf> to create the associations between these key items the Sensor Web Client assumes that the selected
     * SOS provides the 52°North discovery profile.
     *
     * @param observingTimeseries all timeseries being observed.
     * @throws OXFException when request creation fails.
     * @throws InterruptedException if getting the DescribeSensor result gets interrupted.
     * @throws XMLHandlingException if parsing the DescribeSensor result fails
     * @throws IOException if reading the DescribeSensor result fails.
     * @throws IllegalStateException if SOS version is not supported.
     */
    private void performMetadataInterlinking(final Collection<SosTimeseries> observingTimeseries) throws OXFException,
            InterruptedException,
            XMLHandlingException,
            IOException {
        final String sosUrl = getServiceUrl();
        final SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosUrl);
        final TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        final ArrayList<Procedure> procedures = lookup.getProcedures();

        // do describe sensor for each procedure
        final String sosVersion = metadata.getSosVersion();
        final String smlVersion = metadata.getSensorMLVersion();
        final Map<String, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<String, FutureTask<OperationResult>>();
        for (final Procedure proc : procedures) {
            final OperationAccessor opAccessorCallable = createDescribeSensorAccessor(
                    sosUrl, sosVersion, smlVersion, proc);
            futureTasks.put(proc.getProcedureId(), new FutureTask<OperationResult>(opAccessorCallable));
        }

        int i = 1;
        final List<String> illegalProcedures = new ArrayList<String>();
        LOGGER.debug("Going to send #{} DescribeSensor requests.", futureTasks.size());
        for (final String procedureId : futureTasks.keySet()) {
            ByteArrayInputStream incomingResultAsStream = null;
            try {
                LOGGER.trace("Sending request {}", i++);
                final FutureTask<OperationResult> futureTask = futureTasks.get(procedureId);
                AccessorThreadPool.execute(futureTask);
                final OperationResult opResult = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
                if (opResult == null) {
                    illegalProcedures.add(procedureId);
                    LOGGER.debug("Got NO sensor description for '{}'", procedureId);
                } else {
                    incomingResultAsStream = opResult.getIncomingResultAsStream();
                    final DescribeSensorParser parser = new DescribeSensorParser(incomingResultAsStream, metadata);

                    final Procedure procedure = lookup.getProcedure(procedureId);
                    procedure.addAllRefValues(parser.parseReferenceValues());
                    final List<String> phenomenons = parser.getPhenomenons();
                    final List<String> fois = parser.parseFOIReferences();
                    final Point point = parser.buildUpSensorMetadataPosition();

                    if (fois.isEmpty()) {
                        final Collection<Feature> features = lookup.getFeatures();
                        LOGGER.warn("No FOI references found for procedure '{}'.", procedureId);
                        LOGGER.warn("==> Reference all ({}) available.", features.size());
                        for (final Feature foi : features) {
                            fois.add(foi.getFeatureId());
                        }
                    }

                    for (final String featureId : fois) {
                        // FOIs are optional in SOS 2.0 capabilities
                        if ("2.0.0".equals(metadata.getVersion())) {
                            if (!lookup.containsFeature(featureId)) {
                                lookup.addFeature(new Feature(featureId, sosUrl));
                            }
                        }
                        Feature feature = lookup.getFeature(featureId);
                        Station station = metadata.getStationByFeature(feature);
                        if (station == null) {
                            station = new Station(feature);
                            station.setLocation(point);
                            metadata.addStation(station);
                        }

                        for (final String phenomenon : phenomenons) {
                            final String uom = parser.buildUpSensorMetadataUom(phenomenon);
                            final Phenomenon lokupPhen = lookup.getPhenomenon(phenomenon);
                            if (lokupPhen != null) {
                                lokupPhen.setUnitOfMeasure(uom);
                            } else {
                                LOGGER.error("Could not find matching phenomenon in internal 'lookup' storage for '{}'", phenomenon);
                            }
                            final Collection<SosTimeseries> paramConstellations = getMatchingConstellations(observingTimeseries,
                                    procedureId,
                                    phenomenon);

                            station.setLocation(point);
                            for (final SosTimeseries timseries : paramConstellations) {
                                if (!lookup.containsFeature(featureId)) {
                                    lookup.addFeature(new Feature(featureId, sosUrl));
                                }
                                timseries.setFeature(new Feature(featureId, sosUrl));
                                station.addTimeseries(timseries);
                            }
                        }
                    }
                    LOGGER.trace("Got Procedure data for '{}'.", procedure);
                }
            } catch (final TimeoutException e) {
                LOGGER.warn("Could NOT connect to SOS '{}'.", sosUrl, e);
                illegalProcedures.add(procedureId);
            } catch (final ExecutionException e) {
                LOGGER.warn("Could NOT get OperationResult from SOS for '{}'.", procedureId, e.getCause());
                illegalProcedures.add(procedureId);
            } catch (final XmlException e) {
                LOGGER.warn("Could NOT parse OperationResult from '{}'", sosUrl, e);
                if (LOGGER.isDebugEnabled()) {
                    try {
                        final XmlObject response = XmlObject.Factory.parse(incomingResultAsStream);
                        final StringBuilder sb = new StringBuilder();
                        sb.append(String.format("Error sending DescribeSensor for '%s'\n", procedureId));
                        sb.append(String.format("Could NOT parse incoming OperationResult:\n %s", response));
                        LOGGER.debug(sb.toString(), e);
                        illegalProcedures.add(procedureId);
                    } catch (final XmlException ex) {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(incomingResultAsStream));
                        LOGGER.warn("No XML response for procedure '{}'.", procedureId);
                        LOGGER.debug("First line of response: {}", reader.readLine());
                    } catch (final IOException ex) {
                        LOGGER.warn("Could read result", ex);
                    }
                }
            } catch (final IOException e) {
                illegalProcedures.add(procedureId);
                LOGGER.info("Could NOT parse sensorML for procedure '{}'.", procedureId, e);
            } catch (final IllegalStateException e) {
                illegalProcedures.add(procedureId);
                LOGGER.info("Could NOT link procedure '{}' appropriatly.", procedureId, e);
            } catch (final FactoryException e) {
                LOGGER.info("Could not create intern CRS to transform coordinates.", e);
            } catch (final TransformException e) {
                LOGGER.info("Could not transform to intern CRS.", e);
            } finally {
                if (incomingResultAsStream != null) {
                    incomingResultAsStream.close();
                }
                futureTasks.remove(procedureId);
                LOGGER.debug("Still #{} responses to go ...", futureTasks.size());
            }
        }

        if (!illegalProcedures.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Removed procedures: \n");
            for (final String procedure : illegalProcedures) {
                lookup.removeProcedure(procedure);
                sb.append("Removed ==> ");
                sb.append(procedure);
                sb.append("\n");
            }
            LOGGER.warn("#{} procedures are unavailable. {}", illegalProcedures.size(), sb.toString());
        }

        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
    }

    private OperationAccessor createDescribeSensorAccessor(final String sosUrl,
            final String sosVersion, final String smlVersion, final Procedure proc)
            throws OXFException {
        final Operation operation = new Operation(SOSAdapter.DESCRIBE_SENSOR, sosUrl, sosUrl);
        final ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
        paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER, sosVersion);
        paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER, proc.getProcedureId());
        if (SosUtil.isVersion100(sosVersion)) {
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT, smlVersion);
        } else if (SosUtil.isVersion200(sosVersion)) {
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, smlVersion);
        } else {
            throw new IllegalStateException("SOS Version (" + sosVersion + ") is not supported!");
        }
        final OperationAccessor opAccessorCallable = new OperationAccessor(getSosAdapter(), operation, paramCon);
        return opAccessorCallable;
    }

    private Collection<SosTimeseries> getMatchingConstellations(final Collection<SosTimeseries> observingTimeseries,
            final String procedure,
            final String phenomenon) {
        final Collection<SosTimeseries> result = new ArrayList<SosTimeseries>();
        for (final SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.matchesProcedure(procedure) && timeseries.matchesPhenomenon(phenomenon)) {
                result.add(timeseries);
            }
        }
        return result;
    }

    @Override
    public SOSMetadata updateMetadata(final SOSMetadata metadata) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
