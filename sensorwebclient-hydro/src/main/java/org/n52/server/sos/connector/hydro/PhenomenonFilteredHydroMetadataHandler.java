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
package org.n52.server.sos.connector.hydro;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_FOI_VERSION_PARAMETER;
import static org.n52.oxf.sos.adapter.SOSAdapter.GET_FEATURE_OF_INTEREST;
import static org.n52.server.mgmt.ConfigurationContext.SERVER_TIMEOUT;
import static org.n52.server.sos.connector.hydro.SOSwithSoapAdapter.GET_DATA_AVAILABILITY;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.parser.GetFeatureOfInterestParser;
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

public class PhenomenonFilteredHydroMetadataHandler extends HydroMetadataHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenomenonFilteredHydroMetadataHandler.class);

    private Map<String, List<String>> procOff = new HashMap<String, List<String>>();

    public PhenomenonFilteredHydroMetadataHandler(SOSMetadata metadata) {
        super(metadata);
    }

    protected void collectTimeseries(SOSMetadata metadata) throws OXFException,
            InterruptedException,
            ExecutionException,
            TimeoutException,
            XmlException,
            IOException {

        Collection<SosTimeseries> observingTimeseries = createObservingTimeseries(metadata);

        Map<String, FutureTask<OperationResult>> getDataAvailabilityTasks = new HashMap<String, FutureTask<OperationResult>>();
        Map<String, FutureTask<OperationResult>> getFoiAccessTasks = new HashMap<String, FutureTask<OperationResult>>();

        // create tasks by iteration over procedures
        for (SosTimeseries timeserie : observingTimeseries) {
            String phenomenonID = timeserie.getPhenomenonId();
            getFoiAccessTasks.put(phenomenonID,
                                  new FutureTask<OperationResult>(createGetFoiAccess(metadata.getServiceUrl(),
                                                                                     metadata.getVersion(),
                                                                                     phenomenonID)));
            getDataAvailabilityTasks.put(phenomenonID,
                                         new FutureTask<OperationResult>(createGDAAccess(metadata.getServiceUrl(),
                                                                                         metadata.getVersion(),
                                                                                         timeserie)));
        }

        // create list of timeseries of GDA requests
        Collection<SosTimeseries> timeseries = executeGDATasks(getDataAvailabilityTasks, metadata, observingTimeseries);

        // iterate over tasks of getFOI and add them to metadata
        executeFoiTasks(getFoiAccessTasks, metadata);

        // iterate over timeseries and add them to station with according feature id
        for (SosTimeseries timeserie : timeseries) {
            String feature = timeserie.getFeatureId();
            Station station = metadata.getStation(feature);
            if (station != null) {
                station.addTimeseries(timeserie);
            }
            else {
                LOGGER.warn("{} not added! No station for feature '{}'.", timeserie, feature);
            }
        }

        infoLogServiceSummary(metadata);
        metadata.setHasDonePositionRequest(true);
    }

    protected Collection<SosTimeseries> createObservingTimeseries(SOSMetadata metadata)
    		throws OXFException {
    	Contents contents = getServiceDescriptor().getContents();
    	Collection<SosTimeseries> allObservedTimeseries = new ArrayList<SosTimeseries>();

    	Set<String> phenomena = new HashSet<String>();
    	Set<String> offerings = new HashSet<String>();
    	Set<String> procedures = new HashSet<String>();
    	Set<String> features = new HashSet<String>();

        LOGGER.info("# of offering entries: " + contents.getDataIdentificationCount());
		for (int i = 0; i < contents.getDataIdentificationCount(); i++) {
			if (i % 100 == 0) {
        		LOGGER.info("loop in offering entries: " + i);
        	}
			ObservationOffering offering = (ObservationOffering) contents.getDataIdentification(i);
			String offeringID = offering.getIdentifier();
			String[] phenomenonIDs = offering.getObservedProperties();
			String[] procedureIDs = offering.getProcedures();
			String[] featuresIDs = offering.getFeatureOfInterest();
			phenomena.addAll(Arrays.asList(phenomenonIDs));
			offerings.add(offeringID);
			procedures.addAll(Arrays.asList(procedureIDs));
			features.addAll(Arrays.asList(featuresIDs));
			for (String procedureID : procedureIDs) {
				if (procOff.containsKey(procedureID)) {
					procOff.get(procedureID).add(offeringID);
				} else {
					ArrayList<String> offIds = new ArrayList<String>();
					offIds.add(offeringID);
					procOff.put(procedureID, offIds);
				}
			}
		}

		LOGGER.info("create possible time series by observed property");
    	for (String phenomenon : phenomena) {
    		SosTimeseries timeseries = new SosTimeseries();
            timeseries.setPhenomenon(new Phenomenon(phenomenon, metadata.getServiceUrl()));
            timeseries.setSosService(new SosService(metadata.getServiceUrl(), metadata.getVersion()));
            timeseries.getSosService().setLabel(metadata.getTitle());
            allObservedTimeseries.add(timeseries);
		}

    	LOGGER.info("create lookup table");
    	TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
    	for (String feature : features) {
			lookup.addFeature(new Feature(feature, metadata.getServiceUrl()));
		}
    	for (String phenomenon : phenomena) {
			lookup.addPhenomenon(new Phenomenon(phenomenon, metadata.getServiceUrl()));
		}
    	for (String procedure : procedures) {
			lookup.addProcedure(new Procedure(procedure, metadata.getServiceUrl()));
		}
    	for (String offering : offerings) {
			lookup.addOffering(new Offering(offering, metadata.getServiceUrl()));
		}
    	return allObservedTimeseries;
    }

    private Collection<SosTimeseries> executeGDATasks(Map<String, FutureTask<OperationResult>> getDataAvailabilityTasks,
                                                      SOSMetadata metadata, Collection<SosTimeseries> observingTimeseries) throws InterruptedException,
            ExecutionException,
            TimeoutException,
            XmlException,
            IOException {
        int counter = getDataAvailabilityTasks.size();
        LOGGER.debug("Sending " + counter + " GetDataAvailability requests");
        Collection<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
        for (String phenomenon : getDataAvailabilityTasks.keySet()) {
            LOGGER.debug("Sending #{} GetDataAvailability request for phenomenon " + phenomenon,
                         counter--);
            FutureTask<OperationResult> futureTask = getDataAvailabilityTasks.get(phenomenon);
            AccessorThreadPool.execute(futureTask);
            OperationResult result = null;
			try {
				result = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
			} catch (Exception e) {
				LOGGER.error("Get no result for GetDataAvailability with parameter constellation: " + phenomenon + "!");
			}
            if (result == null) {
                LOGGER.error("Get no result for GetDataAvailability with parameter constellation: " + phenomenon + "!");
            } else {
            	XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsStream());
                timeseries.addAll(getAvailableTimeseries(result_xb, phenomenon, metadata, observingTimeseries));
            }
        }
        return timeseries;
    }

    private void executeFoiTasks(Map<String, FutureTask<OperationResult>> getFoiAccessTasks, SOSMetadata metadata) throws InterruptedException,
            ExecutionException,
            XmlException,
            IOException,
            OXFException {
        int counter;
        counter = getFoiAccessTasks.size();
        LOGGER.debug("Sending {} GetFeatureOfInterest requests", counter);
        for (String phenomenonID : getFoiAccessTasks.keySet()) {
            LOGGER.debug("Sending #{} GetFeatureOfInterest request for procedure '{}'", counter--, phenomenonID);
            FutureTask<OperationResult> futureTask = getFoiAccessTasks.get(phenomenonID);
            AccessorThreadPool.execute(futureTask);
            try {
                OperationResult opsRes = futureTask.get(SERVER_TIMEOUT, MILLISECONDS);
                GetFeatureOfInterestParser getFoiParser = new GetFeatureOfInterestParser(opsRes, metadata);
                getFoiParser.createFeatures();
            }
            catch (TimeoutException e) {
                LOGGER.error("Timeout occured.", e);
            }
        }
    }

    private Collection<SosTimeseries> getAvailableTimeseries(XmlObject result_xb,
                                                             String phenomenon,
                                                             SOSMetadata metadata,
                                                             Collection<SosTimeseries> observingTimeseries) throws XmlException, IOException {
        ArrayList<SosTimeseries> timeseries = new ArrayList<SosTimeseries>();
        String queryExpression = "declare namespace gda='http://www.opengis.net/sosgda/1.0'; $this/gda:GetDataAvailabilityResponse/gda:dataAvailabilityMember";
        XmlObject[] response = result_xb.selectPath(queryExpression);
        for (XmlObject xmlObject : response) {
            String feature = getAttributeOfChildren(xmlObject, "featureOfInterest", "href").trim();
            String procedure = getAttributeOfChildren(xmlObject, "procedure", "href").trim();
            for (SosTimeseries obsTimeseries : observingTimeseries) {
				if (obsTimeseries.getPhenomenonId().equals(phenomenon)) {
					if (procOff.containsKey(procedure)) {
						for (String offering : procOff.get(procedure)) {
							SosTimeseries addedtimeserie = new SosTimeseries();
							addedtimeserie.setFeature(new Feature(feature, metadata.getServiceUrl()));
				            addedtimeserie.setPhenomenon(new Phenomenon(phenomenon, metadata.getServiceUrl()));
				            addedtimeserie.setProcedure(new Procedure(procedure, metadata.getServiceUrl()));
				            addedtimeserie.setOffering(new Offering(offering, metadata.getServiceUrl()));
				            // create the category for every parameter constellation out of phenomenon and procedure
				            String category = getLastPartOf(phenomenon) + " (" + getLastPartOf(procedure) + ")";
				            addedtimeserie.setCategory(new Category(category, metadata.getServiceUrl()));
				            addedtimeserie.setSosService(new SosService(metadata.getServiceUrl(), metadata.getVersion()));
				            addedtimeserie.getSosService().setLabel(metadata.getTitle());
							timeseries.add(addedtimeserie);
						}
					} else {
						LOGGER.warn("Procedure " + procedure + " doesn't exist in capabilities document");
					}
				}
			}
        }
        return timeseries;
    }

    private Callable<OperationResult> createGetFoiAccess(String sosUrl, String sosVersion, String phenomenonID) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell(GET_FOI_SERVICE_PARAMETER, "SOS");
        container.addParameterShell(GET_FOI_VERSION_PARAMETER, sosVersion);
        container.addParameterShell("observedProperty", phenomenonID);
        Operation operation = new Operation(GET_FEATURE_OF_INTEREST, sosUrl, sosUrl);
        return new OperationAccessor(getSosAdapter(), operation, container);
    }

    private Callable<OperationResult> createGDAAccess(String sosUrl, String version, SosTimeseries timeserie) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell("observedProperty", timeserie.getPhenomenonId());
        container.addParameterShell("version", version);
        Operation operation = new Operation(GET_DATA_AVAILABILITY, sosUrl, sosUrl);
        return new OperationAccessor(getSosAdapter(), operation, container);
    }

}

