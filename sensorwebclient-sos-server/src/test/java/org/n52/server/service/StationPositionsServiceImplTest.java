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
