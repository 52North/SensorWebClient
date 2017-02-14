/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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



import static org.n52.oxf.feature.OXFAbstractObservationType.FEATURE_OF_INTEREST;
import static org.n52.oxf.feature.OXFAbstractObservationType.OBSERVED_PROPERTY;
import static org.n52.oxf.feature.OXFAbstractObservationType.PROCEDURE;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.dataTypes.OXFPhenomenonPropertyType;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.server.da.AccessException;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeseriesDataGenerator extends Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesDataGenerator.class);

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {
        LOGGER.debug("Starting producing representation with " + options);
        Map<String, OXFFeatureCollection> entireCollMap = new HashMap<String, OXFFeatureCollection>();
        try {
            entireCollMap = getFeatureCollectionFor(options, false);
        } catch (AccessException e) {
            throw new GeneratorException("Error creating TimeSeriesDataResponse.", e);
        }

        Collection<OXFFeatureCollection> observationCollList = entireCollMap.values();


        HashMap<String, HashMap<Long, Double>> allTimeSeries = new HashMap<String, HashMap<Long, Double>>();

        for (OXFFeatureCollection coll : observationCollList) {

            // List of FOI
            List<String> foiList = new ArrayList<String>();

            // List of phenomenon
            List<OXFPhenomenonPropertyType> phenomenonList = new ArrayList<OXFPhenomenonPropertyType>();

            // Map-association: foi --> list of procedures
            HashMap<String, List<String>> procMap = new HashMap<String, List<String>>();

            for (OXFFeature observation : coll) {

                // get FOI name
                String foi = ((OXFFeature) observation.getAttribute(FEATURE_OF_INTEREST)).toString();
                if (!foiList.contains(foi)) {
                    foiList.add(foi);
                }

                // get PROCEDURE name
                String proc = (String) observation.getAttribute(PROCEDURE);
                if (procMap.containsKey(foi) == false) {
                    procMap.put(foi, new ArrayList<String>());
                }
                if (!procMap.get(foi).contains(proc)) {
                    procMap.get(foi).add(proc);
                }

                // get PHENOMENON name
                OXFPhenomenonPropertyType obsProp = (OXFPhenomenonPropertyType) observation.getAttribute(OBSERVED_PROPERTY);
                if (!phenomenonList.contains(obsProp)) {
                    phenomenonList.add(obsProp);

                }
            }

            for (String foi : foiList) {
                for (String procedure : procMap.get(foi)) {

                    for (OXFPhenomenonPropertyType obsProp : phenomenonList) {

                        ObservationSeriesCollection txCollection =
                                new ObservationSeriesCollection(coll,
                                        new String[] { foi },
                                        new String[] { obsProp.getURN() }, false);

                        TimeseriesProperties selectedProperties = null;
                        for (TimeseriesProperties property : options.getProperties()) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug(property.getOffFoiProcPhenCombination());
                            }
                            if (property.getFeature().equals(foi)
                                    && property.getPhenomenon().equals(obsProp.getURN())
                                    && property.getProcedure().equals(procedure)) {
                                selectedProperties = property;
                                String phenomenonId = selectedProperties.getPhenomenon();
                                try {
                                    TimeseriesFactory factory = new TimeseriesFactory(txCollection);
                                    HashMap<Long, Double> data = factory.compressToHashMap(foi, phenomenonId, procedure);
                                    allTimeSeries.put(selectedProperties.getTimeseriesId(), data);
                                }
                                catch (ParseException e) {
                                    throw new GeneratorException("Could not parse data.", e);
                                }
                                break;
                            } else {
                                if (LOGGER.isDebugEnabled()) {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Ignore unknown property constellation: [");
                                    sb.append("foiId: ").append(foi);
                                    sb.append(", phenId: ").append(obsProp.getURN());
                                    sb.append(", procId: ").append(procedure);
                                    sb.append(" ]. Expected timeseries properties: [");
                                    sb.append("foiId: ").append(property.getFeature());
                                    sb.append(", phenId: ").append(property.getPhenomenon());
                                    sb.append(", procId: ").append(property.getProcedure());
                                    sb.append(" ]");
                                    LOGGER.debug(sb.toString());
                                }
                            }
                        }
                    }
                }
            }
        }

        // check if some TS did not get data and fill blank spots
        for (TimeseriesProperties prop : options.getProperties()) {
            if (!allTimeSeries.containsKey(prop.getTimeseriesId())) {
                allTimeSeries.put(prop.getTimeseriesId(), new HashMap<Long, Double>());
            }
        }

        return new TimeSeriesDataResponse(allTimeSeries);
//            return new TimeSeriesDataResponse(new HashMap<String, HashMap<Long,Double>>());



    }

}
