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

import static org.n52.server.oxf.util.ConfigurationContext.getSOSMetadata;
import static org.n52.server.oxf.util.access.DescribeSensorAccessor.getSensorDescriptionAsSensorML;

import java.util.HashMap;

import org.apache.xmlbeans.XmlObject;
import org.n52.client.service.SensorMetadataService;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.parser.DescribeSensorParser;
import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorMetadataServiceImpl implements SensorMetadataService {
    
    private static final Logger LOG = LoggerFactory.getLogger(SensorMetadataServiceImpl.class);

    @Override
    public SensorMetadataResponse getSensorMetadata(TimeSeriesProperties tsProperties) throws Exception {
        try {
            LOG.debug("Request -> GetSensorMetadata");
            String sosUrl = tsProperties.getSosUrl();
            SOSMetadata metadata = getSOSMetadata(sosUrl);
            String procedureId = tsProperties.getProcedure().getId();
            String phenomenonId = tsProperties.getPhenomenon().getId();
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParamtersLookup();
            Procedure procedure = lookup.getProcedure(procedureId);

            XmlObject sml = getSensorDescriptionAsSensorML(procedureId, metadata);
            DescribeSensorParser parser = new DescribeSensorParser(sml.newInputStream(), metadata);
            tsProperties.setMetadataUrl(parser.buildUpSensorMetadataHtmlUrl(procedureId, sosUrl));
            tsProperties.setStationName(parser.buildUpSensorMetadataStationName());
            tsProperties.setUOM(parser.buildUpSensorMetadataUom(phenomenonId));
            
            HashMap<String, ReferenceValue> refvalues = parser.parseCapsDataFields();
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
    public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(String serviceURL, String procedure) throws Exception {
        try {
            LOG.debug("Request -> getProcedureDetailsUrl");
            SOSMetadata metadata = ConfigurationContext.getSOSMetadata(serviceURL);
            XmlObject sml = getSensorDescriptionAsSensorML(procedure, metadata);
            DescribeSensorParser parser = new DescribeSensorParser(sml.newInputStream(), metadata);
            String url = parser.buildUpSensorMetadataHtmlUrl(procedure, serviceURL);
            return new GetProcedureDetailsUrlResponse(url);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
