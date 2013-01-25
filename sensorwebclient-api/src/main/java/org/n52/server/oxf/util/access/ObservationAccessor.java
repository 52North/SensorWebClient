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

package org.n52.server.oxf.util.access;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_EVENT_TIME_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_FEATURE_OF_INTEREST_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_OBSERVED_PROPERTY_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_OFFERING_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_PROCEDURE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_RESULT_MODEL_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_SERVICE_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_TEMPORAL_FILTER_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_OBSERVATION_VERSION_PARAMETER;
import static org.n52.server.oxf.util.ConfigurationContext.SERVER_TIMEOUT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ows.capabilities.Parameter;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.feature.SOSObservationStore;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.valueDomains.time.TemporalValueDomain;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.generator.RequestConfig;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.Constants;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationAccessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationAccessor.class);

    /**
     * @param requests
     *        the requests to send
     * @return a List of OXFFeatureCollection objects. Each of those objects is the result of ONE SOS request.
     * @throws AccessException
     *         if accessing or processing features failed.
     */
    public Map<String, OXFFeatureCollection> sendRequests(List<RequestConfig> requests) throws AccessException {
    	
        try {
			Map<String, OXFFeatureCollection> entireCollMap = new HashMap<String, OXFFeatureCollection>();
			for (RequestConfig request : requests) {

			    String sosUrl = request.getSosURL();
			    String key = createObservationCollectionKey(request, sosUrl);
			    OperationResult opResult = sendRequest(request);
			    SOSObservationStore featureStore = new SOSObservationStore(opResult);
			    OXFFeatureCollection featureColl = featureStore.unmarshalFeatures();
			    LOGGER.debug("Received " + featureColl.size() + " observations for " + key);
			    if (featureColl != null) {
			        if (entireCollMap.containsKey(key)) {
			            OXFFeatureCollection existingFeatureColl = entireCollMap.get(key);
			            existingFeatureColl.add(featureColl.toList());
			        }
			        else {
			            entireCollMap.put(key, featureColl);
			        }
			    }
			}
			return entireCollMap;
		} catch (OXFException e) {
			throw new AccessException("Could not process observations.", e);
		}
    }
    
    public OperationResult sendRequest(RequestConfig request) throws AccessException {
        try {
            String sosUrl = request.getSosURL();
            SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosUrl);
            String sosVersion = metadata.getSosVersion();
            boolean waterML = metadata.isWaterML();

            ParameterContainer paramters = createParameterContainer(request, sosVersion, waterML);
            Operation operation = new Operation(SOSAdapter.GET_OBSERVATION, sosUrl + "?", sosUrl);
            SOSAdapter adapter = SosAdapterFactory.createSosAdapter(metadata);
            OperationAccessor callable = new OperationAccessor(adapter, operation, paramters);
            FutureTask<OperationResult> task = new FutureTask<OperationResult>(callable);
            AccessorThreadPool.execute(task);

            OperationResult opResult = task.get(SERVER_TIMEOUT, MILLISECONDS);
            return opResult;
        }
        catch (OXFException e) {
            throw new AccessException("Could not process observations.", e);
        }
        catch (TimeoutException e) {
            throw new AccessException("GetObservation request timed out.", e);
        }
        catch (InterruptedException e) {
            throw new AccessException("Thread got interrupted during GetObservation request.", e);
        }
        catch (ExecutionException e) {
            throw new AccessException("Could not execute GetObservation request.", e.getCause());
        }
    }

    private String createObservationCollectionKey(RequestConfig request, String sosUrl) {
        return request.getOfferingID() + "@" + sosUrl;
    }

    /**
     * @param request
     * @param sosVersion
     * @param waterML
     * @return ParameterContainer
     * @throws OXFException
     */
    private ParameterContainer createParameterContainer(RequestConfig request, String sosVersion, boolean waterML) throws OXFException {
        String offering = request.getOfferingID();
        String format = request.getResponseFormat();
        String[] fois = getArray(request.getStationsSet());
        String[] procedures = getArray(request.getProcedureSet());
        String[] observedProperties = getArray(request.getPhenomenonsSet());

        ParameterContainer params = new ParameterContainer();
        params.addParameterShell(GET_OBSERVATION_SERVICE_PARAMETER, "SOS");
        params.addParameterShell(GET_OBSERVATION_VERSION_PARAMETER, sosVersion);
        params.addParameterShell(GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER, format);
        params.addParameterShell(GET_OBSERVATION_FEATURE_OF_INTEREST_PARAMETER, fois);
        params.addParameterShell(GET_OBSERVATION_PROCEDURE_PARAMETER, procedures);
        params.addParameterShell(GET_OBSERVATION_OBSERVED_PROPERTY_PARAMETER, observedProperties);
        params.addParameterShell(GET_OBSERVATION_OFFERING_PARAMETER, new String[] {offering});

        if (request.getTime() == null) {
            // TODO investigate if case sensitivity is needed here
            // timeParam = new Parameter(GET_OBSERVATION_FIRST_LAST_PARAMETER,
            // true,
            // new StringValueDomain(request.getFirstLastParam()),
            // Parameter.COMMON_NAME_TIME);
        }
        else {
            if (sosVersion.equals(Constants.SOS_VERSION_100)) {
                Parameter timeParam = new Parameter(GET_OBSERVATION_EVENT_TIME_PARAMETER,
                                                    true,
                                                    new TemporalValueDomain(request.getTime()),
                                                    Parameter.COMMON_NAME_TIME);
                ParameterShell timeParamShell = new ParameterShell(timeParam, request.getTime());
                params.addParameterShell(timeParamShell);
            }
            else if (sosVersion.equals(Constants.SOS_VERSION_200)) {
                Parameter timeParam = new Parameter(GET_OBSERVATION_TEMPORAL_FILTER_PARAMETER,
                                                    true,
                                                    new TemporalValueDomain(request.getTime()),
                                                    Parameter.COMMON_NAME_TIME);
                ParameterShell timeParamShell = new ParameterShell(timeParam, request.getTime());
                params.addParameterShell(timeParamShell);
            }
        }

        if (waterML) {
            params.addParameterShell(GET_OBSERVATION_RESULT_MODEL_PARAMETER, "TimeseriesObservation");
        }

        return params;
    }

    private String[] getArray(List<String> list) {
        String[] elements = JavaHelper.toStringArray(list.toArray());
        return elements;
    }

}