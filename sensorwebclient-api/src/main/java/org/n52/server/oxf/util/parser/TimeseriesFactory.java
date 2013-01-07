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
package org.n52.server.oxf.util.parser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.server.oxf.util.ConfigurationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeseriesFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesFactory.class);

    public static TimeSeries createTimeSeries(ObservationSeriesCollection seriesCollection, String foiID,
            String obsPropID, String procID, String seriesType) {

        LOGGER.debug("Starting compression");

        TimeSeries timeSeries = new TimeSeries(foiID + "___" + obsPropID + "___" + procID, Second.class);

        ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();
        ObservedValueTuple prevObservation;
        ObservedValueTuple nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[0]);
        ObservedValueTuple observation = nextObservation;

        int counter = 0;
        Double sum = 0.0;

        // all obs
        LOGGER.debug("Compressionlevel none");
        for (int i = 0; i < timeArray.length; i++) {

            prevObservation = observation;
            observation = nextObservation;

            if (i + 1 < timeArray.length) {
                nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
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
    public static TimeSeries compressToTimeSeries(ObservationSeriesCollection seriesCollection, String foiID,
            String obsPropID, String procID, boolean force, String seriesType) {

        LOGGER.debug("Starting compression");

        TimeSeries timeSeries = new TimeSeries(foiID + "___" + obsPropID + "___" + procID, Second.class);

        ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();
        ObservedValueTuple prevObservation;
        ObservedValueTuple nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[0]);
        ObservedValueTuple observation = nextObservation;

        int counter = 0;
        Double sum = 0.0;

        if (ConfigurationContext.FACADE_COMPRESSION || (timeArray.length > 6000 && force)) {
            // just %6
            LOGGER.debug("Compressionlevel 6");
            for (int i = 0; i < timeArray.length; i += 6) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
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

        } else if (ConfigurationContext.FACADE_COMPRESSION && timeArray.length > 4000) {
            // just %4
            LOGGER.debug("Compressionlevel 4");
            for (int i = 0; i < timeArray.length; i += 4) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
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

        } else if (ConfigurationContext.FACADE_COMPRESSION && timeArray.length > 2000) {
            // just %2
            LOGGER.debug("Compressionlevel 2");
            for (int i = 0; i < timeArray.length; i += 2) {

                prevObservation = observation;
                observation = nextObservation;

                if (i + 1 < timeArray.length) {
                    nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
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
                    nextObservation = seriesCollection.getTuple(new OXFFeature(foiID, null), timeArray[i + 1]);
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

    /**
     * @param obsVal
     * @return
     */
    private static Double getValidData(String obsVal) {
        // XXX no data as double? double comparison via equals? switch to NO_DATA value string
        Double tmp = null;
        try {
            tmp = new Double(obsVal);
            for (Double noData : ConfigurationContext.NO_DATA_VALUES) {
                if (tmp.equals(noData)) {
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Not a double or integer value " + obsVal, e);
        }
        return tmp;
    }

    public static HashMap<Long, String> compressToHashMap(ObservationSeriesCollection coll, String foiID,
            String phenID, String procID) throws NumberFormatException, ParseException {

        HashMap<Long, String> data = new HashMap<Long, String>();

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

                String obsVal;
                try {
                    obsVal = getValidData(observation.getValue(0).toString()).toString();
                } catch (NullPointerException e) {
                    obsVal = "no Data available"; // TODO finish
                }
                // String prevObsVal = prevObservation.getValue(0).toString();
                // String nextObsVal = nextObservation.getValue(0).toString();

                // if ((i == 0) || // first observation --> in
                // (i == timeArray.length - 1) || // last
                // // observation
                // // -->
                // // in
                // (!(prevObsVal.equals(obsVal) && nextObsVal.equals(obsVal))))
                // {

                // insert here
                DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssZ");
                // TODO dirty fix
                String tmp = observation.getTime().toISO8601Format() + "00";

                // DateTime d =
                // DateTimeFormat.forPattern("yyyy-MM-dd'T'kk:mm:ssZ").
                // parseDateTime(observation.getTime().toISO8601Format());
                data.put(f.parse(tmp).getTime(), obsVal);
                counter++;
            }
            // }

        }

        return data;

    }

}
