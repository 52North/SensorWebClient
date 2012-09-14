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

package org.n52.server.ses.service;

import java.util.ArrayList;
import java.util.List;

import org.n52.client.service.SesDataSourceService;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.shared.serializable.pojos.Sensor;
import org.n52.shared.serializable.pojos.TestRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SesDataSourceServiceImpl implements SesDataSourceService {

    private static final Logger LOG = LoggerFactory.getLogger(SesDataSourceServiceImpl.class);

    List<TestRecord> list;

    @Override
    public List<TestRecord> fetch() throws Exception {
        try {
            List<Sensor> sensors = HibernateUtil.getSensors();
            list = new ArrayList<TestRecord>();

            for (int i = 0; i < sensors.size(); i++) {
                Sensor sensor = sensors.get(i);
                String sensorId = sensor.getSensorID();
                String sensorInUse = Boolean.toString(isSensorInUse(sensor));
                String activeSensor = Boolean.toString(sensor.isActivated());
                list.add(new TestRecord(sensorId, activeSensor, sensorInUse));
            }
            return list;
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private boolean isSensorInUse(Sensor sensor) {
        return sensor.getInUse() != 0;
    }

    @Override
    public TestRecord add(TestRecord record) throws Exception {
        try {
            list.add(record);
            return record;
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public TestRecord update(TestRecord record) throws Exception {
        try {
            String recordId = record.getName();
            if (recordId != null) {
                int index = -1;
                for (int i = 0; i < list.size(); i++) {
                    if (recordId.equals(list.get(i).getName())) {
                        index = i;
                        break;
                    }
                }
                if (index >= 0) {
                    list.set(index, record);
                    return record;
                }
            }
            return null;
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public void remove(TestRecord record) throws Exception {
        try {
            // Integer recordId = record.getId ();
            // if (recordId != null) {
            // int index = -1;
            // for (int i = 0; i < list.size (); i++) {
            // if (recordId.equals (list.get (i).getId ())) {
            // index = i;
            // break;
            // }
            // }
            // if (index >= 0) {
            // list.remove (index);
            // }
            // }
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
