/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.service;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.Station;

public class StationPositionsServiceImplTest {

    private static final int COUNT_STATIONS = 5;
    private QueryServiceImpl service;
    private ArrayList<Station> stations;

    @Before
    public void setUp() throws Exception {
        service = new QueryServiceImpl();
        stations = new ArrayList<Station>();
        for (int i = 0; i < COUNT_STATIONS; i++) {
            stations.add(new Station("test", "url"));
        }
    }

    @Test
    public void testIsFinished() {
        ConfigurationContext.IS_DEV_MODE = true;
//        assertTrue(service.isFinished(2000, stations));
//        assertTrue(service.isFinished(0, stations));
//        ConfigurationContext.IS_DEV_MODE = false;
//        assertFalse(service.isFinished(0, stations));
    }

}
