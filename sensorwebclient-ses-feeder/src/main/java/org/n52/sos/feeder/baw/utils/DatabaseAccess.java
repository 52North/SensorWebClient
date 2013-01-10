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
 * Created: 01.07.2010
 *****************************************************************************/
package org.n52.sos.feeder.baw.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.feeder.baw.hibernate.InitSessionFactory;
import org.n52.sos.feeder.baw.hibernate.ObservedProperty;
import org.n52.sos.feeder.baw.hibernate.Offering;
import org.n52.sos.feeder.baw.hibernate.SOS;
import org.n52.sos.feeder.baw.hibernate.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the Database access.
 * 
 * @author Jan Schulte
 * 
 */
public class DatabaseAccess {

    // TODO close sessions after committing transaction 
    
    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(DatabaseAccess.class);

    /**
     * Saves the state of a sensor and the belonging SOS.
     * 
     * @param sos
     *            The SOS to be saved
     * @param sensor
     *            The sensor to be saved
     */
    public static synchronized void saveState(SOS sos, Sensor sensor) {
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(sos);
        session.saveOrUpdate(sensor);
        for (Offering offering : sensor.getOfferings()) {
            for (ObservedProperty obsProp : offering.getObservedProperties()) {
                session.saveOrUpdate(obsProp);
                obsProp.setOffering(offering);
            }
            session.saveOrUpdate(offering);
            offering.setSensor(sensor);
        }
        sensor.setSos(sos);
        sos.getSensors().add(sensor);
        transaction.commit();
    }

    /**
     * Saves a new sos in the database.
     * 
     * @param sosUrl
     *            the sos url
     */
    public static synchronized void saveNewSOS(String sosUrl) {
        if (sosExists(sosUrl)) {
            Session session = InitSessionFactory.getInstance().getCurrentSession();
            Transaction transaction = session.beginTransaction();
            SOS sos = new SOS();
            sos.setUrl(sosUrl);
            session.saveOrUpdate(sos);
            transaction.commit();
        } else {
            log.debug("SOS already exists in Database");
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean sosExists(String sosUrl) {
        boolean check = true;
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Criteria crit = session.createCriteria(SOS.class);
        List<SOS> soses = crit.add(Restrictions.eq("url", sosUrl)).list();
        if (soses.size() > 0) {
            check = false;
        }
        transaction.commit();
        return check;

    }

    /**
     * Load sensor from database and save with the new usage status.
     * <br>
     * If set to false, it resets the field <code>Strings.getString("Database.tsensor.lastUpdate")</code>!
     * 
     * 
     * @param id
     *            the id
     * @param used
     *            the used
     */
    public static synchronized void saveSensorUsage(String id, boolean used) {
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<?> sensorList = crit.add(Restrictions.eq(Strings.getString("Database.tsensor.procedure"), id)).list();
        for (Object object : sensorList) {
            Sensor sensor = (Sensor) object;
            sensor.setUsed(used);
            // if sensor is not used anymore, reset the last update field!
            if(!used) {
            	sensor.setLastUpdate(null);
            }
            session.update(sensor);
        }
        transaction.commit();
    }

    /**
     * Load list of all SOSes.
     * 
     * @return the list
     */
    public static synchronized List<SOS> loadSOS() {
        List<SOS> SOSes = new ArrayList<SOS>();
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery(Strings.getString("Database.fromSOS"));
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            SOS sos = (SOS) it.next();
            log.info("Read SOS out of Database: " + sos.getUrl() + " with " + sos.getSensors().size() + " procedures");
            SOSes.add(sos);
            for (Sensor sensor : sos.getSensors()) {
                for (Offering offering : sensor.getOfferings()) {
                    offering.getObservedProperties().size();
                }
            }
        }
        transaction.commit();
        return SOSes;
    }

    /**
     * Gets the used sensors.
     * 
     * @return the used sensors
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<Sensor> getUsedSensors() {
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq(Strings.getString("Database.tsensor.used"), true)).list();
        // lazy loading
        for (Sensor sensor : sensors) {
            for (Offering offering : sensor.getOfferings()) {
                offering.getObservedProperties().size();
            }
        }
        transaction.commit();
        return sensors;
    }

    public static List<Sensor> getAllSensors() {
        List<Sensor> sensors = new ArrayList<Sensor>();
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("from Sensor sensor");
        // lazy loading
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            Sensor sensor = (Sensor) it.next();
            sensors.add(sensor);
            for (Offering offering : sensor.getOfferings()) {
                offering.getObservedProperties().size();
            }
        }
        transaction.commit();
        return sensors;
    }

    @SuppressWarnings("unchecked")
    public static boolean existsProcedureSOS(String procedure, String sos) {
        boolean check = true;
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("procedure", procedure)).list();
        for (Sensor sensor : sensors) {
            if (sensor.getSos().getUrl().equals(sos)) {
                check = false;
                break;
            }
        }
        transaction.commit();
        return check;
    }

}
