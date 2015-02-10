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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.server.io;

import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.is;
import org.jfree.data.time.TimeSeries;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class TimeseriesFactoryTest {

    private static final String FICTIVE_SOS_URL = "http://localhost/sos";

    private static final String GET_OBSERVATION_RESPONSE_CONTAINING_DAYLIGHT_SAVING_TIMESHIFT = "/files/getObservationResponse_with_daylight_saving_timeshift.xml";

    @Before public void setUp() {
        ConfigurationContext.NO_DATA_VALUES = new ArrayList<String>();
    }

    @Test
    public void shouldNotAddValuesTwiceWhenCreatingTimeseriesDuringDaylightSavingSwitch() throws Exception {
        SosTimeseries timeseries = createSosTimeseries();
        GetObservationResponseToOxfFeatureCollectionReader reader = createReader();

        String[] foiIds = new String[]{timeseries.getFeatureId()};
        String[] procedureIds = new String[]{timeseries.getProcedureId()};
        String[] observedPropertyIds = new String[]{timeseries.getPhenomenonId()};
        OXFFeatureCollection obsColl = reader.getFeatureCollection();
        ObservationSeriesCollection seriesCollection = new ObservationSeriesCollection(obsColl, foiIds, observedPropertyIds, procedureIds, true);

        assertThat(seriesCollection.getAllTuples().size(), is(277));

//        ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();
//        for (ITimePosition iTimePosition : timeArray) {
//            ITimePosition timePos = (ITimePosition) observation.getTime();
//            DateTime time = DateTime.parse(timePos.toISO8601Format());
//        }

        TimeseriesFactory factory = new TimeseriesFactory(seriesCollection);
        TimeSeries chartTimeseries = factory.createTimeSeries(timeseries, "1");
        assertThat(chartTimeseries.getItemCount(), is(277));
    }

    private GetObservationResponseToOxfFeatureCollectionReader createReader() throws Exception {
        return new GetObservationResponseToOxfFeatureCollectionReader(GET_OBSERVATION_RESPONSE_CONTAINING_DAYLIGHT_SAVING_TIMESHIFT);
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
}
