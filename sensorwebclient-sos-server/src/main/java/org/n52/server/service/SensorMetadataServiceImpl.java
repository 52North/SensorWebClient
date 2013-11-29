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
package org.n52.server.service;

import static org.n52.server.da.oxf.DescribeSensorAccessor.getSensorDescriptionAsSensorML;
import static org.n52.server.mgmt.ConfigurationContext.createSosMetadataHandler;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.n52.client.service.SensorMetadataService;
import org.n52.io.v1.data.ReferenceValueOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.TimeseriesValue;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.da.MetadataHandler;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.parser.DescribeSensorParser;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorMetadataServiceImpl implements SensorMetadataService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SensorMetadataServiceImpl.class);

    @Override
    public SensorMetadataResponse getSensorMetadata(TimeseriesProperties tsProperties) throws Exception {
        try {
            LOG.debug("Request -> GetSensorMetadata");
            
            SosTimeseries timeseries = tsProperties.getTimeseries();
            SOSMetadata sosMetadata = getSOSMetadata(timeseries.getServiceUrl());
            
            

//            MetadataHandler metadataHandler = createSosMetadataHandler(sosMetadata);
//            TimeseriesMetadataOutput timeseriesMetadata = metadataHandler.getTimeseriesMetadata(timeseries);
//            String uom = timeseriesMetadata.getUom();
//            
//            ReferenceValueOutput[] referenceValues = timeseriesMetadata.getReferenceValues();
//            Map<String, ReferenceValue> refValues = new HashMap<String, ReferenceValue>();
//            for (ReferenceValueOutput referenceValue : referenceValues) {
//                String label = referenceValue.getLabel();
//                TimeseriesValue lastValue = referenceValue.getLastValue();
//                refValues.put(label, new ReferenceValue(label, lastValue.getValue()));
//            }
            
            
            
            

            // TODO use different request strategy to obtain metadata/uom when SOS supports HydroProfile
            // (HyProfile must request an Observation (without timestamp we get the last value))
            // ==> move metadata obtaining strategy to MetadataHandler class: a different strategy can
            // be used by overriding the default (metadata via SensorML)
            

            
            XmlObject sml = getSensorDescriptionAsSensorML(timeseries.getProcedureId(), sosMetadata);
            DescribeSensorParser parser = new DescribeSensorParser(sml.newInputStream(), sosMetadata);
            String url = parser.buildUpSensorMetadataHtmlUrl(tsProperties.getTimeseries());
            tsProperties.setMetadataUrl(url);



            String procedureId = tsProperties.getProcedure();
            String phenomenonId = tsProperties.getPhenomenon();
            TimeseriesParametersLookup lookup = sosMetadata.getTimeseriesParametersLookup();
            Procedure procedure = lookup.getProcedure(procedureId);
            
//            tsProperties.setStationName(parser.buildUpSensorMetadataStationName());
            tsProperties.setUnitOfMeasure(parser.buildUpSensorMetadataUom(phenomenonId));
            HashMap<String, ReferenceValue> refvalues = parser.parseReferenceValues();
            
            
            tsProperties.addAllRefValues(refvalues);
            procedure.addAllRefValues(refvalues);
    
            SensorMetadataResponse response = new SensorMetadataResponse(tsProperties);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Parsed SensorMetadata: {}", response.toDebugString());
            }
    
            JavaHelper.cleanUpDir(ConfigurationContext.XSL_DIR, ConfigurationContext.FILE_KEEPING_TIME, "xml");
            return response;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(SosTimeseries timeseries) throws Exception {
        try {
            LOG.debug("Request -> getProcedureDetailsUrl");
            SOSMetadata metadata = ConfigurationContext.getSOSMetadata(timeseries.getServiceUrl());
            XmlObject sml = getSensorDescriptionAsSensorML(timeseries.getProcedureId(), metadata);
            DescribeSensorParser parser = new DescribeSensorParser(sml.newInputStream(), metadata);
            String url = parser.buildUpSensorMetadataHtmlUrl(timeseries);
            return new GetProcedureDetailsUrlResponse(url);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

	@Override
	public SOSMetadataResponse getUpdatedSOSMetadata() {
		Map<String, SOSMetadata> updateSOSMetadata = ConfigurationContext.updateSOSMetadata();
		return new SOSMetadataResponse(updateSOSMetadata);
	}

}
