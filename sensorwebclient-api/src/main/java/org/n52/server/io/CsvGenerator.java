/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.server.io;

import org.n52.shared.exceptions.GeneratorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvGenerator extends Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvGenerator.class);

    private boolean zip;

    // XXX zip is unused ATM
    public CsvGenerator(boolean zip, String folder) {
        this.folderPostfix = folder;
    }

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {

        LOGGER.debug("Starting producing representation with " + options);
        Map<String, OXFFeatureCollection> entireCollMap = new HashMap<String, OXFFeatureCollection>();
        try {
            entireCollMap = getFeatureCollectionFor(options, false);
        }
        catch (Exception e) {
            throw new GeneratorException("Error creating Csv data.", e);
        }

        Collection<OXFFeatureCollection> observationCollList = entireCollMap.values();

        if (observationCollList.size() > 1) {
            throw new IllegalArgumentException(
                                               "Just ONE observation collection for ONE SOS-offering allowed.");
        }

        if (observationCollList.size() == 1) {
        }

        TimeseriesProperties pc = options.getProperties().get(0);
        File csv = JavaHelper.genRndFile(ConfigurationContext.GEN_DIR + "/" + folderPostfix,
                                         pc.getProcedure().replaceAll("/", "_") + "_"
                                                 + formatDate(new Date(options.getBegin())) + "_"
                                                 + formatDate(new Date(options.getEnd())) + "_",
                                         "csv");
        OutputStream out;
        try {
            out = new FileOutputStream(csv);
        }
        catch (FileNotFoundException e1) {
            LOGGER.error("Could not produce presentation.", e1);
            return null;
        }

        try {
            String csvString = "";

            // set header:
            csvString += "Sensor Station;Sensor Phenomenon;Date;Value\n";

            // fill cells:
            for (TimeseriesProperties prop : options.getProperties()) {

                TimeseriesParametersLookup lookup = getParameterLookup(prop.getServiceUrl());
                String featureId = prop.getFeature();
                String phenomenonId = prop.getPhenomenon();

                OXFFeatureCollection entireColl = (OXFFeatureCollection) observationCollList.toArray()[0];
                if ( !entireColl.isEmpty()) {
                    ObservationSeriesCollection seriesCollection = new ObservationSeriesCollection(
                                                                                                   entireColl,
                                                                                                   new String[] {featureId},
                                                                                                   new String[] {phenomenonId},
                                                                                                   false);
                    ITimePosition timeArray[] = seriesCollection
                            .getSortedTimeArray();

                    if (timeArray.length > 0) {
                        ObservedValueTuple nextObservation = seriesCollection
                                .getTuple(
                                          new OXFFeature(featureId, entireColl
                                                  .getFeatureType()), timeArray[0]);
                        ObservedValueTuple observation = nextObservation;

                        for (int i = 0; i < timeArray.length; i++) {

                            observation = nextObservation;

                            if (i + 1 < timeArray.length) {
                                nextObservation = seriesCollection.getTuple(
                                                                            new OXFFeature(featureId, null),
                                                                            timeArray[i + 1]);
                            }

                            csvString += lookup.getFeature(featureId).getLabel() + ";";
                            csvString += lookup.getPhenomenon(phenomenonId).getLabel()
                                    + " (" + prop.getUnitOfMeasure() + ")"
                                    + ";";
                            csvString += observation.getTime().toISO8601Format()
                                    + ";";
                            csvString += observation.getValue(0).toString() + "\n";
                            // csvString +=
                            // observation.getValue(0).toString().replace(".", ",")
                            // + "\n";
                        }
                    }
                }

            }
            out.write(csvString.getBytes());
        }
        catch (Exception e) {
            throw new GeneratorException(e.getMessage(), e);
        }
        finally {
            try {
                out.flush();
                out.close();
            }
            catch (IOException e) {
                LOGGER.error("Could not produce presentation.", e);
            }
        }
        JavaHelper.cleanUpDir(ConfigurationContext.GEN_DIR, ConfigurationContext.FILE_KEEPING_TIME);
        LOGGER.debug("Produced CSV file url: " + ConfigurationContext.GEN_URL + "/" + csv.getName());
        return new FileResponse(ConfigurationContext.GEN_URL + "/" + csv.getName());
    }
}