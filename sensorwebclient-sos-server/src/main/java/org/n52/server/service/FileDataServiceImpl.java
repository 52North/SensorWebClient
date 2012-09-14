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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.n52.client.service.FileDataService;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.util.IOHelper;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.AccessorThreadPool;
import org.n52.server.oxf.util.access.OperationAccessor;
import org.n52.server.oxf.util.access.oxfExtensions.SOSAdapter_OXFExtension;
import org.n52.server.oxf.util.access.oxfExtensions.SOSRequestBuilderFactory_OXFExtension;
import org.n52.server.oxf.util.generator.CsvGenerator;
import org.n52.server.oxf.util.generator.Generator;
import org.n52.server.oxf.util.generator.PdfGenerator;
import org.n52.server.oxf.util.generator.XlsGenerator;
import org.n52.server.oxf.util.generator.ZipGenerator;
import org.n52.shared.exceptions.TimeoutException;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileDataServiceImpl implements FileDataService {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileDataServiceImpl.class);

    @Override
    public RepresentationResponse getPDF(TimeSeriesDataRequest req) throws Exception {
        try {
            PdfGenerator gen = new PdfGenerator(false, "");
            return gen.producePresentation(req.getOptions());
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public RepresentationResponse getXLS(TimeSeriesDataRequest req) throws Exception {
        try {
            XlsGenerator gen = new XlsGenerator(false, "");
            return gen.producePresentation(req.getOptions());
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public RepresentationResponse getCSV(TimeSeriesDataRequest req) throws Exception {
        try {
            CsvGenerator gen = new CsvGenerator(false, "");
            return gen.producePresentation(req.getOptions());
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public RepresentationResponse getPDFzip(TimeSeriesDataRequest req) throws Exception {
        try {
            Date d = new Date();
            String folder = "zip"+d.getTime()+"/";
            PdfGenerator gen = new PdfGenerator(true, folder);
            // generate all timeseries on their own
            return generateZipPresentation(req, gen);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public RepresentationResponse getXLSzip(TimeSeriesDataRequest req) throws Exception {
        try {
            Date d = new Date();
            String folder = "zip" + d.getTime();
            XlsGenerator gen = new XlsGenerator(true, folder);
            // generate all timeseries on their own
            return generateZipPresentation(req, gen);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public RepresentationResponse getCSVzip(TimeSeriesDataRequest req) throws Exception {
        try {
            Date d = new Date();
            String folder = "zip"+d.getTime();
            CsvGenerator gen = new CsvGenerator(true, folder);
            // generate all timeseries on their own
            return generateZipPresentation(req, gen);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private RepresentationResponse generateZipPresentation(TimeSeriesDataRequest req, Generator gen) throws Exception {
        try {
            for (TimeSeriesProperties prop : req.getOptions().getProperties()) {
                generateSensorMLFile(sendDescSens(prop), prop, gen.getFolderPostfix());
                ArrayList<TimeSeriesProperties> props = new ArrayList<TimeSeriesProperties>();
                props.add(prop);
                gen.producePresentation(new DesignOptions(props, req.getOptions()
                        .getBegin(), req.getOptions().getEnd(), req.getOptions().getGrid()));
            }
            // so we got our files in the right folder, now zippem
            ZipGenerator zipgen = new ZipGenerator(gen.getFolderPostfix());
            return zipgen.producePresentation(req.getOptions());
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
    

    private void generateSensorMLFile(OperationResult sendDescSens, TimeSeriesProperties prop, String folderPostfix) throws Exception {
        try {
            File f = JavaHelper.genFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, 
                    "SensorML_" + prop.getProcedure().getId().replaceAll("/", "_"), "xml");
            IOHelper.saveFile(f, sendDescSens.getIncomingResultAsStream());
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private OperationResult sendDescSens(TimeSeriesProperties prop) throws Exception {
        try {
            String sosUrl = prop.getSosUrl();
            SOSMetadata meta = (SOSMetadata)ConfigurationContext.getServiceMetadata(sosUrl);
            
            OperationResult opResult = null;
            // build up parameterContainer for DescribeSensor operation:
            ParameterContainer paramCon = new ParameterContainer();
    
            String sosVersion = meta.getSosVersion();
            String procedureId = prop.getProcedure().getId();
            String smlVersion = meta.getSensorMLVersion();
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER, sosVersion);
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER, procedureId);
            if (SosUtil.isVersion100(sosVersion)) {
                paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT, smlVersion);
            } else if (SosUtil.isVersion200(sosVersion)) {
                paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, smlVersion);
            } else {
                throw new IllegalStateException("SOS Version (" + sosVersion + ") is not supported!");
            }
    
            ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.generateRequestBuilder(sosVersion);
            SOSAdapter_OXFExtension adapter = new SOSAdapter_OXFExtension(sosVersion, requestBuilder);
            Operation descSensorOperation = new Operation(SOSAdapter.DESCRIBE_SENSOR, sosUrl, sosUrl);
            OperationAccessor callable = new OperationAccessor(adapter, descSensorOperation, paramCon);
            FutureTask<OperationResult> t = new FutureTask<OperationResult>(callable);
            try {
                AccessorThreadPool.execute(t);
                opResult = t.get(ConfigurationContext.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (java.util.concurrent.TimeoutException e) {
                throw new TimeoutException("Server did not respond in time", e);
            }
            return opResult;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
