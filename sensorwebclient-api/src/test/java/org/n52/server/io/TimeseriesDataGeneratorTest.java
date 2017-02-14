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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static junit.framework.Assert.fail;
import org.apache.xmlbeans.XmlObject;
import static org.hamcrest.CoreMatchers.is;
import org.joda.time.DateTime;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.sos.feature.SOSObservationStore;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadXmlFileViaClassloader;
import org.n52.server.da.AccessException;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class TimeseriesDataGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesDataGeneratorTest.class);

    private static final String FICTIVE_SOS_URL = "http://localhost/sos";

    private static final String GET_OBSERVATION_RESPONSE_CONTAINING_DAYLIGHT_SAVING_TIMESHIFT = "/files/getObservationResponse_with_daylight_saving_timeshift.xml";

    @Before
    public void setup() throws Exception {
        ConfigurationContext.NO_DATA_VALUES = new ArrayList<String>();
    }

    @Test
    public void shouldCreateTimeseries() throws Exception {
        long begin = DateTime.parse("2007-10-27T10:00:00.000+02:00").getMillis();
        long end = DateTime.parse("2007-10-28T09:00:00.000+01:00").getMillis();
        TimeseriesDataGenerator generator = new TimeseriesDataGeneratorSeam();
        SosTimeseries timeseries = createSosTimeseries();
        DesignOptions options = new DesignOptions(createTimeseriesProperties(timeseries), begin, end, true);
        TimeSeriesDataResponse response = (TimeSeriesDataResponse) generator.producePresentation(options);
        HashMap<String, HashMap<Long, Double>> data = response.getPayloadData();
        HashMap<Long, Double> timeseriesData = data.get(timeseries.getTimeseriesId());
        assertThat(timeseriesData.size(), is(277));
    }

    private ArrayList<TimeseriesProperties> createTimeseriesProperties(SosTimeseries timeseries) {
        ArrayList<TimeseriesProperties> properties = new ArrayList<TimeseriesProperties>();

        Station station = new Station("station", FICTIVE_SOS_URL);
        properties.add(new TimeseriesProperties(timeseries, station, -1, -1));
        return properties;
    }

    private SosTimeseries createSosTimeseries() {
        SosTimeseries timeseries = new SosTimeseries();
        timeseries.setOffering(new Offering("http://localhost/offering/na", FICTIVE_SOS_URL));
        timeseries.setProcedure(new Procedure("http://localhost/sensors/testsensor", FICTIVE_SOS_URL));
        timeseries.setFeature(new Feature("http://localhost/featureOfInterest/testsensor", FICTIVE_SOS_URL));
        timeseries.setPhenomenon(new Phenomenon("Abfluss", FICTIVE_SOS_URL));
        timeseries.setCategory(new Category("Abfluss", FICTIVE_SOS_URL));
        return timeseries;
    }

    private static class TimeseriesDataGeneratorSeam extends TimeseriesDataGenerator {

        @Override
        protected Map<String, OXFFeatureCollection> getFeatureCollectionFor(DesignOptions options, boolean generalize) throws AccessException {
            try {
                Map<String, OXFFeatureCollection> collections = new HashMap<String, OXFFeatureCollection>();
                collections.put("collectionContainingTimeshift", createReader().getFeatureCollection());
                return collections;
            } catch (Exception e) {
                LOGGER.error("Could not create feature collection.", e);
                fail("Could not create feature collection from " + GET_OBSERVATION_RESPONSE_CONTAINING_DAYLIGHT_SAVING_TIMESHIFT);
                return null;
            }
        }

        private GetObservationResponseToOxfFeatureCollectionReader createReader() throws Exception {
            return new GetObservationResponseToOxfFeatureCollectionReader(GET_OBSERVATION_RESPONSE_CONTAINING_DAYLIGHT_SAVING_TIMESHIFT);
        }

    }
}
