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

package org.n52.server.da.oxf;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;

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
import org.n52.server.parser.utils.ParsedPoint;
import org.n52.shared.serializable.pojos.EastingNorthing;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMetadataHandler extends MetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMetadataHandler.class);

    public SOSMetadata performMetadataCompletion(String sosUrl, String sosVersion) throws OXFException,
            InterruptedException,
            XMLHandlingException {

        SOSMetadata sosMetadata = initMetadata(sosUrl, sosVersion);

        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries();

        normalizeDefaultCategories(observingTimeseries);

        // TODO check version 2.0.0 sos's

        // XXX hack to get conjunctions between procedures and fois
        if ( !sosMetadata.hasDonePositionRequest()) {
            try {
                performMetadataInterlinking(sosUrl, observingTimeseries);
            }
            catch (IOException e) {
                LOGGER.warn("Could not retrieve relations between procedures and fois", e);
            }
        }
        sosMetadata.setInitialized(true);
        return sosMetadata;
    }

    /**
     * Performs a DescribeSensor request for every offered procedure of an SOS. This is intended to obtain the
     * concrete references between each offering, procedure, featureOfInterest and observedProperty (aka
     * phenomenon). <br>
     * <br>
     * <bf>Note:</bf> to create the associations between these key items the Sensor Web Client assumes that
     * the selected SOS provides the 52°North discovery profile.
     * 
     * @param sosUrl
     *        the SOS service URL.
     * @param observingTimeseries
     *        all timeseries being observed.
     * @throws OXFException
     *         when request creation fails.
     * @throws InterruptedException
     *         if getting the DescribeSensor result gets interrupted.
     * @throws XMLHandlingException
     *         if parsing the DescribeSensor result fails
     * @throws IOException
     *         if reading the DescribeSensor result fails.
     * @throws IllegalStateException
     *         if SOS version is not supported.
     */
    private void performMetadataInterlinking(String sosUrl, Collection<SosTimeseries> observingTimeseries) throws OXFException,
            InterruptedException,
            XMLHandlingException,
            IOException {
        SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosUrl);
        TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
        ArrayList<Procedure> procedures = lookup.getProcedures();

        // do describe sensor for each procedure
        String sosVersion = metadata.getSosVersion();
        String smlVersion = metadata.getSensorMLVersion();
        Map<String, FutureTask<OperationResult>> futureTasks = new ConcurrentHashMap<String, FutureTask<OperationResult>>();
        for (Procedure proc : procedures) {
            OperationAccessor opAccessorCallable = createDescribeSensorAccessor(
                                                                                sosUrl, sosVersion, smlVersion, proc);
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

                    Procedure procedure = lookup.getProcedure(procedureId);
                    procedure.addAllRefValues(parser.parseReferenceValues());
                    List<String> phenomenons = parser.getPhenomenons();
                    List<String> fois = parser.parseFOIReferences();
                    ParsedPoint point = parser.buildUpSensorMetadataPosition();

                    double lat = Double.parseDouble(point.getLat());
                    double lng = Double.parseDouble(point.getLon());

                    String srs = point.getSrs();
                    EastingNorthing eastingNorthing = new EastingNorthing(lng, lat, srs);

                    if (fois.isEmpty()) {
                        Collection<Feature> features = lookup.getFeatures();
                        LOGGER.warn("No FOI references found for procedure '{}'.", procedureId);
                        LOGGER.warn("==> Reference all ({}) available.", features.size());
                        for (Feature foi : features) {
                            fois.add(foi.getId());
                        }
                    }

                    for (String foi : fois) {
                        Station station = metadata.getStation(foi);
                        if (station == null) {
                            station = new Station(foi);
                            station.setLocation(eastingNorthing);
                            metadata.addStation(station);
                        }

                        for (String phenomenon : phenomenons) {
                            Collection<SosTimeseries> paramConstellations = getMatchingConstellations(observingTimeseries,
                                                                                                      procedureId,
                                                                                                      phenomenon);

                            station.setLocation(eastingNorthing);
                            for (SosTimeseries timseries : paramConstellations) {
                                timseries.setFeature(foi);
                                station.addTimeseries(timseries);
                            }
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

    private OperationAccessor createDescribeSensorAccessor(String sosUrl,
                                                           String sosVersion, String smlVersion, Procedure proc)
            throws OXFException {
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
        OperationAccessor opAccessorCallable = new OperationAccessor(getSosAdapter(), operation, paramCon);
        return opAccessorCallable;
    }

    private Collection<SosTimeseries> getMatchingConstellations(Collection<SosTimeseries> observingTimeseries,
                                                                String procedure,
                                                                String phenomenon) {
        Collection<SosTimeseries> result = new ArrayList<SosTimeseries>();
        for (SosTimeseries timeseries : observingTimeseries) {
            if (timeseries.matchesProcedure(procedure) && timeseries.matchesPhenomenon(phenomenon)) {
                result.add(timeseries);
            }
        }
        return result;
    }

	@Override
	public SOSMetadata updateMetadata(SOSMetadata metadata) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
