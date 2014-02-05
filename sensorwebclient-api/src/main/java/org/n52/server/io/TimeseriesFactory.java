/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import static java.lang.Double.parseDouble;
import static org.n52.server.mgmt.ConfigurationContext.FACADE_COMPRESSION;
import static org.n52.server.mgmt.ConfigurationContext.NO_DATA_VALUES;

import java.text.ParseException;
import java.util.HashMap;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimePosition;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeseriesFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesFactory.class);

    public static TimeSeries createTimeSeries(ObservationSeriesCollection seriesCollection, SosTimeseries timeseries, String seriesType) {

        LOGGER.debug("Starting compression");

        TimeSeries timeSeries = new TimeSeries(timeseries.getTimeseriesId(), Second.class);

        ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();
        ObservedValueTuple prevObservation;
        ObservedValueTuple nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[0]);
        ObservedValueTuple observation = nextObservation;

        int counter = 0;
        Double sum = 0.0;

        // all obs
        LOGGER.debug("Compressionlevel none");
        for (int i = 0; i < timeArray.length; i++) {

            prevObservation = observation;
            observation = nextObservation;

            if (i + 1 < timeArray.length) {
                nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
            }

            // String obsVal = observation.getValue(0).toString();
            // String prevObsVal = prevObservation.getValue(0).toString();
            // String nextObsVal = nextObservation.getValue(0).toString();

            // if ((i == 0) || // first observation --> in
            // (i == timeArray.length - 1) || // last observation --> in
            // (!(prevObsVal.equals(obsVal) && nextObsVal.equals(obsVal)))) {

            counter++;

            ITimePosition timePos = (ITimePosition) observation.getTime();
            Double resultVal = getValidData(observation.getValue(0).toString());

            if (seriesType.equals("1")) {
                // nothing
            } else if (seriesType.equals("2")) {
                if (resultVal != null) {
                    resultVal += sum;
                } else {
                    resultVal = sum;
                }
            } else {
                // nothing
            }

            sum = resultVal;

            timeSeries.add(new Second(new Float(timePos.getSecond()).intValue(), timePos.getMinute(), timePos
                    .getHour(), timePos.getDay(), timePos.getMonth(), new Long(timePos.getYear()).intValue()),
                    resultVal);
        }

        // }

        LOGGER.debug("Compressed observations from " + timeArray.length + " to " + counter);

        return timeSeries;

    }

    /**
     * Compress.
     * 
     * @param seriesCollection
     *            the series collection
     * @param foiID
     *            the foi parameterId
     * @param obsPropID
     *            the obs prop parameterId
     * @param procID
     *            the proc parameterId
     * @param force
     * @param seriesType
     * @return TimeSeries
     */
    public static TimeSeries compressToTimeSeries(ObservationSeriesCollection seriesCollection, SosTimeseries timeseries, boolean force, String seriesType) {

        LOGGER.debug("Starting compression");

        TimeSeries timeSeries = new TimeSeries(timeseries.getTimeseriesId(), Second.class);

        ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();
        ObservedValueTuple prevObservation;
        ObservedValueTuple nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[0]);
        ObservedValueTuple observation = nextObservation;

        int counter = 0;
        Double sum = 0.0;

        if (FACADE_COMPRESSION || (timeArray.length > 6000 && force)) {
            // just %6
            LOGGER.debug("Compressionlevel 6");
            for (int i = 0; i < timeArray.length; i += 6) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
                }

                String obsVal = observation.getValue(0).toString();
                String prevObsVal = prevObservation.getValue(0).toString();
                String nextObsVal = nextObservation.getValue(0).toString();

                if ((i == 0) || // first observation --> in
                        (i == timeArray.length - 1) || // last
                        // observation
                        // -->
                        // in
                        (!(prevObsVal.equals(obsVal)) || !(nextObsVal.equals(obsVal)))) {

                    counter++;

                    ITimePosition timePos = (ITimePosition) observation.getTime();
                    Double resultVal = getValidData(observation.getValue(0).toString());
                    // line or sumline
                    if (seriesType.equals("1")) {
                        // nothing
                    } else if (seriesType.equals("2")) {
                        if (resultVal != null) {
                            resultVal += sum;
                        } else {
                            resultVal = sum;
                        }
                    } else {
                        // nothing
                    }
                    sum = resultVal;

                    timeSeries.add(new Second(new Float(timePos.getSecond()).intValue(), timePos.getMinute(), timePos
                            .getHour(), timePos.getDay(), timePos.getMonth(), new Long(timePos.getYear()).intValue()),
                            resultVal);
                }

            }

        } else if (FACADE_COMPRESSION && timeArray.length > 4000) {
            // just %4
            LOGGER.debug("Compressionlevel 4");
            for (int i = 0; i < timeArray.length; i += 4) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
                }

                String obsVal = observation.getValue(0).toString();
                String prevObsVal = prevObservation.getValue(0).toString();
                String nextObsVal = nextObservation.getValue(0).toString();

                if ((i == 0) || // first observation --> in
                        (i == timeArray.length - 1) || // last
                        // observation
                        // -->
                        // in
                        (!(prevObsVal.equals(obsVal)) || !(nextObsVal.equals(obsVal)))) {

                    counter++;

                    ITimePosition timePos = (ITimePosition) observation.getTime();
                    Double resultVal = getValidData(observation.getValue(0).toString());

                    // line or sumline
                    if (seriesType.equals("1")) {
                        // nothing
                    } else if (seriesType.equals("2")) {
                        if (resultVal != null) {
                            resultVal += sum;
                        } else {
                            resultVal = sum;
                        }
                    } else {
                        // nothing
                    }
                    sum = resultVal;

                    timeSeries.add(new Second(new Float(timePos.getSecond()).intValue(), timePos.getMinute(), timePos
                            .getHour(), timePos.getDay(), timePos.getMonth(), new Long(timePos.getYear()).intValue()),
                            resultVal);
                }

            }

        } else if (FACADE_COMPRESSION && timeArray.length > 2000) {
            // just %2
            LOGGER.debug("Compressionlevel 2");
            for (int i = 0; i < timeArray.length; i += 2) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
                }

                String obsVal = observation.getValue(0).toString();
                String prevObsVal = prevObservation.getValue(0).toString();
                String nextObsVal = nextObservation.getValue(0).toString();

                if ((i == 0) || // first observation --> in
                        (i == timeArray.length - 1) || // last
                        // observation
                        // -->
                        // in
                        (!(prevObsVal.equals(obsVal)) || !(nextObsVal.equals(obsVal)))) {

                    counter++;

                    ITimePosition timePos = (ITimePosition) observation.getTime();
                    Double resultVal = getValidData(observation.getValue(0).toString());

                    // line or sumline
                    if (seriesType.equals("1")) {
                        // nothing
                    } else if (seriesType.equals("2")) {
                        if (resultVal != null) {
                            resultVal += sum;
                        } else {
                            resultVal = sum;
                        }
                    } else {
                        // nothing
                    }
                    sum = resultVal;

                    timeSeries.add(new Second(new Float(timePos.getSecond()).intValue(), timePos.getMinute(), timePos
                            .getHour(), timePos.getDay(), timePos.getMonth(), new Long(timePos.getYear()).intValue()),
                            resultVal);
                }
            }

        } else {
            // all obs
            LOGGER.debug("Compressionlevel none");
            for (int i = 0; i < timeArray.length; i++) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
                }

                String obsVal = observation.getValue(0).toString();

                // if ((i == 0) || // first observation --> in
                // (i == timeArray.length - 1) || // last observation --> in
                // (!(prevObsVal.equals(obsVal) &&
                // nextObsVal.equals(obsVal)))
                // ) {

                counter++;

                ITimePosition timePos = (ITimePosition) observation.getTime();
                Double resultVal = getValidData(observation.getValue(0).toString());

                // line or sumline
                if (seriesType.equals("1")) {
                    // nothing
                } else if (seriesType.equals("2")) {
                    if (resultVal != null) {
                        resultVal += sum;
                    } else {
                        resultVal = sum;
                    }
                } else {
                    // nothing
                }
                sum = resultVal;

                timeSeries.add(new Second(new Float(timePos.getSecond()).intValue(), timePos.getMinute(), timePos
                        .getHour(), timePos.getDay(), timePos.getMonth(), new Long(timePos.getYear()).intValue()),
                        resultVal);
                // }

            }
        }

        LOGGER.debug("Compressed observations from " + timeArray.length + " to " + counter);

        return timeSeries;

    }

    private static Double getValidData(String obsVal) {
        if (NO_DATA_VALUES.contains(obsVal)) {
            return null;
        }
        return parseDouble(obsVal);
    }

    public static HashMap<Long, Double> compressToHashMap(ObservationSeriesCollection coll, String foiID,
            String phenID, String procID) throws NumberFormatException, ParseException {

        HashMap<Long, Double> data = new HashMap<Long, Double>();

        if (coll.getAllTuples().size() > 0) {

            //
            // now lets put in the date-value pairs.
            // ! But put it only in if it differs from the
            // previous one !
            //

            ITimePosition timeArray[] = coll.getSortedTimeArray();

            ObservedValueTuple prevObservation;
            ObservedValueTuple nextObservation =
            // FIXME aufräumen in der compression wenn benötigt
            coll.getTuple(new OXFFeature(foiID, null), timeArray[0]);
            ObservedValueTuple observation = nextObservation;

            int counter = 0;

            for (int i = 0; i < timeArray.length; i++) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = coll.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
                }

                Double obsVal = null;
                try {
                    obsVal = getValidData(observation.getValue(0).toString());
                } catch (NullPointerException e) {
                    LOGGER.debug("Missing observation value: {}.", obsVal, e);
                    continue;
                } catch (NumberFormatException e) {
                    LOGGER.error("Not a number value: {}.", obsVal, e);
                    continue;
                }
                
                TimePosition timePosition = (TimePosition) observation.getTime();
                data.put(timePosition.getCalendar().getTimeInMillis(), obsVal);
                counter++;
            }
            // }

        }

        return data;

    }

}
