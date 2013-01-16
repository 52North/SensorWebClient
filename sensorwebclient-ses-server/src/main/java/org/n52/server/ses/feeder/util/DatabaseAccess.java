package org.n52.server.ses.feeder.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.n52.server.ses.feeder.hibernate.InitSessionFactory;
import org.n52.server.ses.feeder.hibernate.ObservedProperty;
import org.n52.server.ses.feeder.hibernate.Offering;
import org.n52.server.ses.feeder.hibernate.SOS;
import org.n52.server.ses.feeder.hibernate.SensorToFeed;
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
    public static synchronized void saveState(SOS sos, SensorToFeed sensor) {
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
     * If set to false, it resets the field the last updated value in database.
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
        Criteria crit = session.createCriteria(SensorToFeed.class);
        List<?> sensorList = crit.add(Restrictions.eq("procedure", id)).list();
        for (Object object : sensorList) {
            SensorToFeed sensor = (SensorToFeed) object;
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
        Query query = session.createQuery("from SOS sos");
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            SOS sos = (SOS) it.next();
            log.info("Read SOS out of Database: " + sos.getUrl() + " with " + sos.getSensors().size() + " procedures");
            SOSes.add(sos);
            for (SensorToFeed sensor : sos.getSensors()) {
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
    public static synchronized List<SensorToFeed> getUsedSensors() {
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Criteria crit = session.createCriteria(SensorToFeed.class);
        List<SensorToFeed> sensors = crit.add(Restrictions.eq("used", true)).list();
        // lazy loading
        for (SensorToFeed sensor : sensors) {
            for (Offering offering : sensor.getOfferings()) {
                offering.getObservedProperties().size();
            }
        }
        transaction.commit();
        return sensors;
    }

    public static List<SensorToFeed> getAllSensors() {
        List<SensorToFeed> sensors = new ArrayList<SensorToFeed>();
        Session session = InitSessionFactory.getInstance().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Query query = session.createQuery("from Sensor sensor");
        // lazy loading
        for (Iterator<?> it = query.iterate(); it.hasNext();) {
            SensorToFeed sensor = (SensorToFeed) it.next();
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
        Criteria crit = session.createCriteria(SensorToFeed.class);
        List<SensorToFeed> sensors = crit.add(Restrictions.eq("procedure", procedure)).list();
        for (SensorToFeed sensor : sensors) {
            if (sensor.getSos().getUrl().equals(sos)) {
                check = false;
                break;
            }
        }
        transaction.commit();
        return check;
    }

}
