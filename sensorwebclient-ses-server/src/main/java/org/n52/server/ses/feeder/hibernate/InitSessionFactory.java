package org.n52.server.ses.feeder.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Initialize a connection to the database.
 * @author Jan Schulte
 * 
 */
public class InitSessionFactory {
    
    /** The session factory. */
    private static SessionFactory sessionFactory;

    /**
     * Gets the single instance of InitSessionFactory.
     *
     * @return The SessionFactory
     */
    public static SessionFactory getInstance() {
        if (sessionFactory == null) {
            final Configuration cfg = new Configuration().configure();
            sessionFactory = cfg.buildSessionFactory();
        }
        return sessionFactory;
    }
}
