package org.n52.server.ses.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HibernateDaoUtil {
    
    // TODO currently used by feeder AND ses-client
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDaoUtil.class);
    
    private static SessionFactory sessionFactory;
    
    protected static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            }
            catch (Exception e) {
                LOGGER.error("Initial SessionFactory creation failed.", e);
            }
        }
        return sessionFactory;
    }

    /**
     * Encapsulates hibernate session management including retrieval of current {@link Session} and committing
     * and flushing it respectively.
     * 
     * @param criteria
     *        the criteria to execute on a current {@link Session}.
     * @return the expected result (can be a {@link Void} to indicate that nothing is expected).
     */
    public static <T> T execute(CriteriaExecution<T> criteria) {
        Session session = getCurrentSession();
        try {
            session.beginTransaction();
            return criteria.execute(session);
        }
        finally {
            if (session != null) {
                session.getTransaction().commit();
            }
        }
    }

    protected static Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }
    
    /**
     * Closes database session factory.
     */
    public static void closeDatabaseSessionFactory() {
        getSessionFactory().close();
    }
}
