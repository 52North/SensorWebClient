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
