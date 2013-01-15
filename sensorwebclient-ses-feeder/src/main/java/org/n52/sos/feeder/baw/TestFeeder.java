/****************************************************************************
 * Copyright (C) 2010
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
 * 
 * Author: Jan Schulte
 * Created: 17.05.2010
 *****************************************************************************/
package org.n52.sos.feeder.baw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.n52.server.util.TimeUtil;
import org.n52.sos.feeder.baw.hibernate.InitSessionFactory;
import org.n52.sos.feeder.baw.hibernate.SOS;
import org.n52.sos.feeder.baw.task.DescriptionTask;
import org.n52.sos.feeder.baw.task.ObservationsTask;
import org.n52.sos.feeder.baw.utils.Strings;

/**
 * Just to test the Feeder.
 */
@SuppressWarnings("unused")
public class TestFeeder {

    private static Configuration config;
    private static Timer capabilitiesTimer;

    /**
     * @param args
     */
    public static void main(String[] args) {

        SimpleDateFormat ISO8601FORMAT = TimeUtil.createIso8601Formatter();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime datetime = new DateTime(new Date());
        String print = fmt.print(datetime);
        System.out.println(print);
//        initConfiguration();
//        createSOS("http://v-sos.uni-muenster.de:8080/PegelOnlineSOSv2/sos");
        // createSOS("http://giv-sos.uni-muenster.de:8080/52nSOSv3/sos");
        // createSOS("http://v-swe.uni-muenster.de:8080/WeatherSOS/sos");
        // createSOS("http://ak1.uni-muenster.de:8080/52nSOSv3/sos");
//        testCapabilitiesTask();
//         testObservationsTask();
        // createRelation();
        // createRelationNew();

    }

    private static void initConfiguration() {
        try {
            FileInputStream fis =
                    new FileInputStream(new File(
                            "C:/Users/jansch/workspace/SosSesFeeder/WebContent/conf/configuration.xml"));
            config = Configuration.instance(fis);
        } catch (FileNotFoundException e) {
            //
        } catch (IOException e) {
            //
        }

    }

    private static void testCapabilitiesTask() {
        capabilitiesTimer = new Timer();
        capabilitiesTimer.schedule(new DescriptionTask(), 1, Configuration.getInstance().getCapTime());
    }
    
    private static void testObservationsTask() {
        ObservationsTask obsTask = new ObservationsTask(new Vector<String>());
//        obsTask.startObservationFeeds();
        // Timer timer = new Timer();
        // timer.schedule(new ObservationsTask(), 5000, Long.parseLong(config
        // .getValue(Configuration.KEY_OBSERVATIONS_TASK_PERIOD)));
    }


    private static SOS createSOS(String sosURL) {
        SOS sos = new SOS();
        sos.setUrl(sosURL);
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.save(sos);
        tx.commit();
        return sos;
    }

}