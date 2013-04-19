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
package org.n52.server.oxf.util.generator;



import static org.n52.oxf.feature.OXFAbstractObservationType.FEATURE_OF_INTEREST;
import static org.n52.oxf.feature.OXFAbstractObservationType.OBSERVED_PROPERTY;
import static org.n52.oxf.feature.OXFAbstractObservationType.PROCEDURE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.oxf.feature.OXFAbstractObservationType;
import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.dataTypes.OXFPhenomenonPropertyType;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.server.oxf.util.parser.TimeseriesFactory;
import org.n52.shared.exceptions.ServerException;
import org.n52.shared.exceptions.TimeoutException;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSeriesDataGenerator extends Generator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSeriesDataGenerator.class);

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {
        try {
            LOGGER.debug("Starting producing representation with " + options);
            Map<String, OXFFeatureCollection> entireCollMap = getFeatureCollectionFor(options, false);
            Collection<OXFFeatureCollection> observationCollList = entireCollMap.values();

            HashMap<String, HashMap<Long, String>> allTimeSeries = new HashMap<String, HashMap<Long, String>>();

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

                            TimeSeriesProperties selectedProperties = null;
                            for (TimeSeriesProperties property : options.getProperties()) {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug(property.getOffFoiProcPhenCombination());
                                }
                                if (property.getFoi().getId().equals(foi)
                                        && property.getPhenomenon().getId().equals(obsProp.getURN())
                                        && property.getProcedure().getId().equals(procedure)) {
                                    selectedProperties = property;
                                    Phenomenon phenomenon = selectedProperties.getPhenomenon();
                                    String phenomenonId = phenomenon.getId();
                                    HashMap<Long, String> data = TimeseriesFactory.compressToHashMap(txCollection, foi, phenomenonId, procedure);
                                    allTimeSeries.put(selectedProperties.getTsID(), data);
                                    break;
                                } else {
                                    if (LOGGER.isDebugEnabled()) {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("Could not produce representation: ");
                                        sb.append("TimeSeries properties do not match [");
                                        sb.append("foiId: ").append(property.getFoi().getId());
                                        sb.append(", phenId: ").append(property.getPhenomenon().getId());
                                        sb.append(", procId: ").append(property.getProcedure().getId());
                                        sb.append("]");
                                        LOGGER.debug(sb.toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // check if some TS did not get data and fill blank spots
            for (TimeSeriesProperties prop : options.getProperties()) {
                if (!allTimeSeries.containsKey(prop.getTsID())) {
                    allTimeSeries.put(prop.getTsID(), new HashMap<Long, String>());
                }
            }

            return new TimeSeriesDataResponse(allTimeSeries);
//            return new TimeSeriesDataResponse(new HashMap<String, HashMap<Long,Double>>());

        } catch (Exception e) {
            throw new GeneratorException("Error creating TimeSeriesDataResponse", e);
        }

    }

}
