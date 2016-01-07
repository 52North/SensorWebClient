/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
