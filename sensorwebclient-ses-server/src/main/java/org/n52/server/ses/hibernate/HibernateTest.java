/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.hibernate;

import org.n52.server.ses.service.SesRulesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HibernateTest.
 * 
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 */
public class HibernateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateTest.class);
    
    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        try {
            new SesRulesServiceImpl().getRuleForEditing("fdghfgh");
        } catch (Exception e) {
            LOGGER.error("Could not get SES rule", e);
        }
    }

    /**
     * Adds the list of uses.
     * 
     * @param listOfUses
     *            the list of uses
     */
//    private static void addListOfUses(ListOfUses listOfUses) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        session.saveOrUpdate(listOfUses);
//        session.getTransaction().commit();
//    }

    /**
     * Gets the users.
     * 
     */
//    private static void getUsers() {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        Query query = session.createQuery(" from User user"); //$NON-NLS-1$
//        for (Iterator<?> iterator = query.iterate(); iterator.hasNext();) {
//            User user = (User) iterator.next();
//            LOGGER.info(user.getName() + user.getBasicRules() + user.getComplexRules());
//        }
//        session.getTransaction().commit();
//    }

    /**
     * Adds the complex rule.
     * 
     * @param complexRule
     *            the complex rule
     */
//    private static void addComplexRule(ComplexRule complexRule) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        complexRule.getOwner().getComplexRules().add(complexRule);
//        session.saveOrUpdate(complexRule.getOwner());
//        session.saveOrUpdate(complexRule);
//        session.getTransaction().commit();
//    }

    /**
     * Adds the basic rule.
     * 
     * @param basicRule
     *            the basic rule
     */
//    private static void addBasicRule(BasicRule basicRule) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        basicRule.getOwner().getBasicRules().add(basicRule);
//        session.saveOrUpdate(basicRule.getOwner());
//        session.saveOrUpdate(basicRule);
//        session.getTransaction().commit();
//    }

    /**
     * Adds the subscription.
     * 
     * @param subscription
     *            the subscription
     */
//    private static void addSubscription(Subscription subscription) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        session.saveOrUpdate(subscription);
//        session.getTransaction().commit();
//    }

    /**
     * Adds the sensor.
     * 
     * @param sensor
     *            the sensor
     */
//    private static void addSensor(Sensor sensor) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        session.saveOrUpdate(sensor);
//        session.getTransaction().commit();
//    }

    /**
     * Adds the user.
     * 
     * @param user
     *            the user
     */
//    private static void addUser(User user) {
//        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//        session.beginTransaction();
//        session.saveOrUpdate(user);
//        session.getTransaction().commit();
//    }

}
