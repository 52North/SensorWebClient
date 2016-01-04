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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.series.api.proxy.v1.srv.QueryParameterAdapter.createQueryParameters;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType.Description;

import org.apache.xmlbeans.XmlObject;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.sensorweb.v1.spi.ParameterService;
import org.n52.sensorweb.v1.spi.RawDataService;
import org.n52.series.api.proxy.v1.io.ProcedureConverter;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.oxf.OperationAccessor;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.BadRequestException;
import org.n52.web.InternalServerException;
import org.n52.web.OptionNotSupported;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProcedureOutputAdapter implements ParameterService<ProcedureOutput>, RawDataService {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ProcedureOutputAdapter.class);

	@Override
	public ProcedureOutput[] getExpandedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
		List<ProcedureOutput> allProcedures = new ArrayList<ProcedureOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    ProcedureConverter converter = new ProcedureConverter(metadata);
			allProcedures.addAll(converter.convertExpanded(filter(metadata, query)));
		}
		return allProcedures.toArray(new ProcedureOutput[0]);
	}

	@Override
    public ProcedureOutput[] getCondensedParameters(IoParameters map) {
        QueryParameters query = createQueryParameters(map);
        List<ProcedureOutput> allProcedures = new ArrayList<ProcedureOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            ProcedureConverter converter = new ProcedureConverter(metadata);
            allProcedures.addAll(converter.convertCondensed(filter(metadata, query)));
        }
        return allProcedures.toArray(new ProcedureOutput[0]);
    }

    private Procedure[] filter(SOSMetadata metadata, QueryParameters query) {
        Set<Procedure> allProcedures = new HashSet<Procedure>();
        for (SosTimeseries timeseries : metadata.getMatchingTimeseries(query)) {
          allProcedures.add(timeseries.getProcedure());
        }
        return allProcedures.toArray(new Procedure[0]);
    }

	@Override
    public ProcedureOutput[] getParameters(String[] procedureIds) {
	    return getParameters(procedureIds, IoParameters.createDefaults());
    }

    @Override
    public ProcedureOutput[] getParameters(String[] procedureIds,IoParameters query) {
        List<ProcedureOutput> selectedProcedures = new ArrayList<ProcedureOutput>();
        for (String procedureId : procedureIds) {
            ProcedureOutput procedure = getParameter(procedureId);
            if (procedure != null) {
                selectedProcedures.add(procedure);
            }
        }
        return selectedProcedures.toArray(new ProcedureOutput[0]);
    }

    @Override
	public ProcedureOutput getParameter(String procedureId) {
		return getParameter(procedureId, IoParameters.createDefaults());
	}

    @Override
    public ProcedureOutput getParameter(String procedureId, IoParameters query) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
            for (Procedure procedure : lookup.getProcedures()) {
                if (procedure.getGlobalId().equals(procedureId)) {
                    ProcedureConverter converter = new ProcedureConverter(metadata);
                    return converter.convertExpanded(procedure);
                }
            }
        }
        return null;
    }

	@Override
	public InputStream getRawData(String id, IoParameters query) {
		checkRawDataFormat(query.getRawFormat(), id);
		SOSMetadata metadata = getMetadataForProcedure(id);
		try {
			FutureTask<OperationResult> futureTask = new FutureTask<OperationResult>(createDescribeSensorRequest(metadata, id, query.getRawFormat()));
	        AccessorThreadPool.execute(futureTask);
	        OperationResult result = futureTask.get(metadata.getTimeout(), TimeUnit.MILLISECONDS);
			if (result == null) {
	            LOGGER.error("Get no result for DescribeSensor request");
	            return null;
	        }
			return getInputStreamFromOperationResult(result);
		} catch (Exception e) {
			throw new InternalServerException(String.format("Could not get raw procedure data for procedure '%s' and rawFormat '%s'!", id, query.getRawFormat()), e);
		}
	}

	@Override
	public InputStream getRawData(UndesignedParameterSet parameters) {
		throw new OptionNotSupported("The raw data query for procedure and UndesignedParameterSet is not supported!");
	}

	@Override
	public boolean supportsRawData() {
		return true;
	}

	private void checkRawDataFormat(String rawFormat, String id) {
		if (rawFormat != null && !rawFormat.isEmpty()) {
			for (SOSMetadata metadata : getSOSMetadatas()) {
				TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
				for (Procedure procedure : lookup.getProcedures()) {
					if (procedure.getGlobalId().equals(id)) {
						boolean valid = false;
						for (String format : metadata.getProcedureFormats()) {
							if (format.equals(rawFormat)) {
								valid = true;
							}
						}
						if (!valid) {
							throw new OptionNotSupported(String.format("Requested rawFormat '%s' is not supported by procedure '%s'!", rawFormat, id));
						}
					}
				}
			}
		} else {
			throw new BadRequestException("The parameter 'rawFormat' is not set or empty!");
		}
	}
	
	private SOSMetadata getMetadataForProcedure(String id) {
		for (SOSMetadata metadata : getSOSMetadatas()) {
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			for (Procedure procedure : lookup.getProcedures()) {
				if (procedure.getGlobalId().equals(id)) {
					return metadata;
				}
			}
		}
		throw new BadRequestException(String.format("The procedure '%s' is not supported!", id));
	}
	
	private Procedure getProcedure(SOSMetadata metadata, String procedureId) {
		TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
		for (Procedure procedure : lookup.getProcedures()) {
			if (procedure.getGlobalId().equals(procedureId)) {
				return procedure;
			}
		}
		return null;
	}
	
	private Callable<OperationResult> createDescribeSensorRequest(SOSMetadata metadata, String procedureId, String format) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell("procedure", getProcedure(metadata, procedureId).getProcedureId());
        String formatParameter = "procedureDescriptionFormat";
        if (SosUtil.isVersion100(metadata.getVersion())) {
        	formatParameter = "outputFormat";
        } else if (SosUtil.isVersion200(metadata.getVersion())) {
        	formatParameter = "procedureDescriptionFormat";
        }
        container.addParameterShell(formatParameter, format);
        container.addParameterShell("version", metadata.getVersion());
        container.addParameterShell("service", "SOS");
        Operation operation = new Operation("DescribeSensor", metadata.getServiceUrl(), metadata.getServiceUrl());
        return new OperationAccessor(SosAdapterFactory.createSosAdapter(metadata), operation, container);
    }
	
    private InputStream getInputStreamFromOperationResult(OperationResult result) {
		try {
			XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsAutoCloseStream());
			if (result_xb instanceof DescribeSensorResponseDocument) {
				DescribeSensorResponseDocument dsrd = (DescribeSensorResponseDocument)result_xb;
				if (dsrd.getDescribeSensorResponse().getDescriptionArray() != null) {
					int length = dsrd.getDescribeSensorResponse().getDescriptionArray().length;
					if (length > 1) {
						LOGGER.warn(String.format("The DescribeSensor response contains '%i' descriptions! Only the first is returned!", length));
					}
					// TODO how to handle more than 
					Description description = dsrd.getDescribeSensorResponse().getDescriptionArray(0);
					XmlObject parse = XmlObject.Factory.parse(getNodeFromNodeList(description.getSensorDescription().getData().getDomNode().getChildNodes()));
					return parse.newInputStream();
				}
			}
		} catch (Exception e) {
			LOGGER.debug("Returned procedure description is not XML formatted!");
		}
		return result.getIncomingResultAsAutoCloseStream();
	}
    
    private Node getNodeFromNodeList(final NodeList nodeList) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    return nodeList.item(i);
                }
            }
        }
        return null;
    }

}
