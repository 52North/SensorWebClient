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
package org.n52.server.io;

import static java.lang.Double.parseDouble;
import java.text.ParseException;
import java.util.HashMap;
import java.util.TimeZone;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.joda.time.DateTime;
import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimePosition;
import static org.n52.server.mgmt.ConfigurationContext.FACADE_COMPRESSION;
import static org.n52.server.mgmt.ConfigurationContext.NO_DATA_VALUES;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeseriesFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesFactory.class);

    private final ObservationSeriesCollection collection;

    private TimeZone timezone = TimeZone.getTimeZone("UTC");

    public TimeseriesFactory(ObservationSeriesCollection collection) {
        this.collection = collection;
        ITimePosition[] index = this.collection.getSortedTimeArray();
        if (index.length > 0) {
            DateTime endTime = DateTime.parse(index[index.length - 1].toISO8601Format());
            this.timezone = endTime.getZone().toTimeZone();
        }
    }

    public TimeSeries createTimeSeries(SosTimeseries timeseries, String seriesType) {
        TimeSeries timeSeries = new TimeSeries(timeseries.getTimeseriesId());

        ITimePosition timeArray[] = collection.getSortedTimeArray();
        ObservedValueTuple prevObservation;
        ObservedValueTuple nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[0]);
        ObservedValueTuple observation = nextObservation;

        int counter = 0;
        Double sum = 0.0;

        // all obs
        LOGGER.debug("Compressionlevel none");
        for (int i = 0; i < timeArray.length; i++) {

            prevObservation = observation;
            observation = nextObservation;

            if (i + 1 < timeArray.length) {
                nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
            }

            // String obsVal = observation.getValue(0).toString();
            // String prevObsVal = prevObservation.getValue(0).toString();
            // String nextObsVal = nextObservation.getValue(0).toString();

            // if ((i == 0) || // first observation --> in
            // (i == timeArray.length - 1) || // last observation --> in
            // (!(prevObsVal.equals(obsVal) && nextObsVal.equals(obsVal)))) {

            counter++;

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

            ITimePosition timePos = (ITimePosition) observation.getTime();
            DateTime time = DateTime.parse(timePos.toISO8601Format());
            timeSeries.add(new FixedMillisecond(time.getMillis()), resultVal);
        }

        // }

        LOGGER.debug("Compressed observations from " + timeArray.length + " to " + counter);

        return timeSeries;

    }

    public TimeSeries compressToTimeSeries(SosTimeseries timeseries, boolean force, String seriesType) {

        TimeSeries timeSeries = new TimeSeries(timeseries.getTimeseriesId());

        ITimePosition timeArray[] = collection.getSortedTimeArray();
        ObservedValueTuple prevObservation;
        ObservedValueTuple nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[0]);
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
                    nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
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

                    ITimePosition timePos = (ITimePosition) observation.getTime();
                    DateTime time = DateTime.parse(timePos.toISO8601Format());
            timeSeries.add(new FixedMillisecond(time.getMillis()), resultVal);
                }

            }

        } else if (FACADE_COMPRESSION && timeArray.length > 4000) {
            // just %4
            LOGGER.debug("Compressionlevel 4");
            for (int i = 0; i < timeArray.length; i += 4) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
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

                    ITimePosition timePos = (ITimePosition) observation.getTime();
                    DateTime time = DateTime.parse(timePos.toISO8601Format());
            timeSeries.add(new FixedMillisecond(time.getMillis()), resultVal);
                }

            }

        } else if (FACADE_COMPRESSION && timeArray.length > 2000) {
            // just %2
            LOGGER.debug("Compressionlevel 2");
            for (int i = 0; i < timeArray.length; i += 2) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
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

                    ITimePosition timePos = (ITimePosition) observation.getTime();
                    DateTime time = DateTime.parse(timePos.toISO8601Format());
            timeSeries.add(new FixedMillisecond(time.getMillis()), resultVal);
                }
            }

        } else {
            // all obs
            LOGGER.debug("Compressionlevel none");
            for (int i = 0; i < timeArray.length; i++) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = collection.getTuple(new OXFFeature(timeseries.getFeatureId(), null), timeArray[i + 1]);
                }

                String obsVal = observation.getValue(0).toString();

                // if ((i == 0) || // first observation --> in
                // (i == timeArray.length - 1) || // last observation --> in
                // (!(prevObsVal.equals(obsVal) &&
                // nextObsVal.equals(obsVal)))
                // ) {

                counter++;

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

                ITimePosition timePos = (ITimePosition) observation.getTime();
                DateTime time = DateTime.parse(timePos.toISO8601Format());
                timeSeries.add(new FixedMillisecond(time.getMillis()), resultVal);
                // }

            }
        }

        LOGGER.debug("Compressed observations from " + timeArray.length + " to " + counter);

        return timeSeries;

    }

    private Double getValidData(String obsVal) {
        if (NO_DATA_VALUES.contains(obsVal)) {
            return null;
        }
        return parseDouble(obsVal);
    }

    public HashMap<Long, Double> compressToHashMap(String foiID,
            String phenID, String procID) throws ParseException {

        HashMap<Long, Double> data = new HashMap<Long, Double>();

        if (collection.getAllTuples().size() > 0) {

            //
            // now lets put in the date-value pairs.
            // ! But put it only in if it differs from the
            // previous one !
            //

            ITimePosition timeArray[] = collection.getSortedTimeArray();

            ObservedValueTuple prevObservation;
            ObservedValueTuple nextObservation =
            // FIXME aufräumen in der compression wenn benötigt
            collection.getTuple(new OXFFeature(foiID, null), timeArray[0]);
            ObservedValueTuple observation = nextObservation;

            int counter = 0;

            for (int i = 0; i < timeArray.length; i++) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = collection.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
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

                TimePosition timePos = (TimePosition) observation.getTime();
                DateTime time = DateTime.parse(timePos.toISO8601Format());
                data.put(time.getMillis(), obsVal);
                counter++;
            }
            // }

        }

        return data;

    }

}
