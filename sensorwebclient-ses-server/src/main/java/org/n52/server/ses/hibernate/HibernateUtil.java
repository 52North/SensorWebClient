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
package org.n52.server.ses.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.Sensor;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HibernateUtil. This class manages all interactions with a database
 * 
 * @author <a href="mailto:j.schulte@52north.de">Jan Schulte</a>
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class HibernateUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

    /** The Constant sessionFactory. */
    private static SessionFactory sessionFactory;

    /**
     * Gets the session factory.
     * 
     * @return the session factory
     */
    public static SessionFactory getSessionFactory(){
        if (sessionFactory == null) {
            // Create the SessionFactory from hibernate.cfg.xml
            try {
                sessionFactory = new Configuration().configure().buildSessionFactory();
            } catch (Exception e) {
                LOGGER.error("Initial SessionFactory creation failed.", e);
            }
        }
        return sessionFactory;
    }

    /**
     * Saves given user in database.
     * 
     * @param user
     *            saved user
     */
    public static void addUser(User user) {
        user.setActive(true);
        user.setEmailVerified(true);
        user.setPasswordChanged(false);
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(user);
        session.getTransaction().commit();
    }

    /**
     * Check if the Database already contains the given userName.
     * 
     * @param userName
     *            the user name
     * @return true if username exists
     */
    public static boolean existsUserName(String userName) {
        boolean userNameExists = false;
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        if (crit.add(Restrictions.eq("userName", userName)).list().size() > 0) {
            userNameExists = true;
        }
        session.getTransaction().commit();
        return userNameExists;
    }

    /**
     * Check if the Database already contains the given eMail.
     * 
     * @param eMail
     *            the e mail
     * @return true if eMail exists
     */
    public static boolean existsEMail(String eMail) {
        boolean eMailExists = false;
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        if (crit.add(Restrictions.eq("eMail", eMail)).list().size() > 0) {
            eMailExists = true;
        }
        session.getTransaction().commit();
        return eMailExists;
    }
    
    /**
     * @param handy
     * @return true if handy exists
     */
    public static boolean existsHandy(String handy) {
        boolean handyExists = false;
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        if (crit.add(Restrictions.eq("handyNr", handy)).list().size() > 0) {
            handyExists = true;
        }
        session.getTransaction().commit();
        return handyExists;
    }

    /**
     * Change user role.
     * 
     * @param userID
     *            the user parameterId
     * @param role
     *            the role
     * @return true, if successful
     */
    @SuppressWarnings("unchecked")
    public static boolean changeUserRole(int userID, UserRole role) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("parameterId", userID)).list();
        if (users.size() != 1) {
            return false;
        }
        User user = users.get(0);
        user.setRole(role);
        session.saveOrUpdate(user);
        session.getTransaction().commit();
        return true;
    }

    /**
     * 
     * @param userID
     * @param status
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean changeUserActivation(int userID, boolean status) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("parameterId", userID)).list();
        if (users.size() != 1) {
            return false;
        }
        User user = users.get(0);
        user.setActivated(status);
        session.saveOrUpdate(user);
        session.getTransaction().commit();
        return true;
    }

    /**
     * Gets the user by parameterId.
     * 
     * @param userID
     *            the user parameterId
     * @return the user by parameterId
     */
    @SuppressWarnings("unchecked")
    public static User getUserByID(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("parameterId", userID)).list();
        User user = users.get(0);
        session.getTransaction().commit();
        return user;
    }

    /**
     * Gets the user by given register ID
     * 
     * @param registerID
     *            the user register ID
     * @return the user by register ID
     */
    @SuppressWarnings("unchecked")
    public static User getUserByRegisterID(String registerID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("registerID", registerID)).list();
        User user;
        if (users.size() == 1) {
            user = users.get(0);
        } else {
            user = null;
        }
        session.getTransaction().commit();
        return user;
    }

    /**
     * Update user.
     * 
     * @param user
     *            the user
     */
    public static void updateUser(User user) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
    }

    /**
     * Delete user by parameterId.
     * 
     * @param userID
     *            the user parameterId
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean deleteUserByID(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("parameterId", userID)).list();
        if (users.size() == 1) {
            User user = users.get(0);
            session.delete(user);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    /**
     * Gets the user by name.
     * 
     * @param userName
     *            the user name
     * @return the user by name
     */
    @SuppressWarnings("unchecked")
    public static User getUserByName(String userName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("userName", userName)).list();
        if (users.size() != 1) {
            return null;
        }
        User user = users.get(0);
        session.getTransaction().commit();
        return user;
    }

    /**
     * Returns all users of the database
     * 
     * @return list of all users
     */
    @SuppressWarnings("unchecked")
    public static List<User> getAllUsers() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("activated", true)).list();

        session.getTransaction().commit();
        return users;
    }

    /**
     * Add a new basic rule to database
     * 
     * @param rule
     *            the rule
     */
    public static void addBasicRule(BasicRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(rule);
        session.getTransaction().commit();
    }
    
    /**
     * 
     * @param rule
     */
    public static void addCopiedBasicRule(BasicRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(rule);
        session.getTransaction().commit();
    }
    
    /**
     * 
     * @param rule
     */
    public static void addCopiedComplexRule(ComplexRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(rule);
        session.getTransaction().commit();
    }
    
    /**
     * Returns a list of all basic rules to a given user parameterId
     * 
     * @param userID
     *            the user parameterId
     * @return the list of basic rules
     */
    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllOwnBasicRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("ownerID", Integer.valueOf(userID))).list();
        session.getTransaction().commit();
        return rules;
    }

    /**
     * Returns a list of all complex rules to a given user parameterId
     * 
     * @param userID
     *            the user parameterId
     * @return the list of complex rules
     */
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllOwnComplexRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq("ownerID", Integer.valueOf(userID))).list();
        session.getTransaction().commit();
        return rules;
    }

    /**
     * @param ruleName
     * @return {@link BasicRule}
     */
    @SuppressWarnings("unchecked")
    public static BasicRule getBasicRuleByName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("name", ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            return rules.get(0);
        }
        return null;
    }

    /**
     * 
     * @param ruleName
     * @return {@link ComplexRule}
     */
    @SuppressWarnings("unchecked")
    public static ComplexRule getComplexRuleByName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq("name", ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            return rules.get(0);
        }
        return null;
    }

    /**
     * @param subscription
     */
    public static void addSubscription(Subscription subscription) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(subscription);
        session.getTransaction().commit();
    }

    /**
     * @param userID
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllOtherBasicRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.not(Restrictions.eq("ownerID", Integer.valueOf(userID)))).list();
        session.getTransaction().commit();
        return rules;
    }
    
    /**
     * @param userID
     * @return {@link List}
     */
    public static List<BasicRule> getAllOtherPublishedBasicRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.and(Restrictions.not(Restrictions.eq("ownerID", Integer.valueOf(userID))), Restrictions.eq("release", true))).list();
        session.getTransaction().commit();
        return rules;
    }
    
    /**
     * 
     * @param userID
     * @return {@link List}
     */
    public static List<ComplexRule> getAllOtherPublishedComplexRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.and(Restrictions.not(Restrictions.eq("ownerID", Integer.valueOf(userID))), Restrictions.eq("release", true))).list();
        session.getTransaction().commit();
        return rules;
    }
    
    
    /**
     * @param ruleName
     * @param newStatus
     */
    public static void updateBasicRuleSubscribtion(String ruleName, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("name", ruleName)).list();
        if (rules.size() == 1) {
            BasicRule rule = rules.get(0);
            rule.setSubscribed(newStatus);
            session.saveOrUpdate(rule);
        }
        session.getTransaction().commit();
    }
    
    /**
     * 
     * @param ruleName
     * @param newStatus
     */
    public static void updateComplexRuleSubscribtion(String ruleName, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq("name", ruleName)).list();
        if (rules.size() == 1) {
            ComplexRule rule = rules.get(0);
            rule.setSubscribed(newStatus);
            session.saveOrUpdate(rule);
        }
        session.getTransaction().commit();
    }

    /**
     * 
     * @param userID
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllOtherComplexRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.not(Restrictions.eq("ownerID", Integer.valueOf(userID)))).list();
        session.getTransaction().commit();
        return rules;
    }

    /**
     * Adds the sensor.
     * 
     * @param sensor
     *            the sensor
     */
    public static void addSensor(Sensor sensor) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(sensor);
        session.getTransaction().commit();
    }

    /**
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<Sensor> getSensors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.list();
        session.getTransaction().commit();

        return sensors;
    }
    
    /**
     * 
     * @param sensorID
     * @return {@link List}
     */
    public static Sensor getSensorByID(String sensorID) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("sensorID", sensorID)).list();
        session.getTransaction().commit();
        
        if (sensors.size() != 0) {
            return sensors.get(0);
        }
        return null;
    }

    /**
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<Sensor> getActiveSensors() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("activated", true)).list();
        session.getTransaction().commit();

        return sensors;
    }

    /**
     * @param sensorID
     * @param newStatus
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean updateSensor(String sensorID, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("sensorID", sensorID)).list();

        if (sensors.size() == 1) {
            Sensor sensor = sensors.get(0);
            sensor.setActivated(newStatus);
            session.saveOrUpdate(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    /**
     * @param ruleName
     * @param value 
     * @return {@link Boolean}
     */
    public static boolean publishRule(String ruleName, boolean value) {
        BasicRule basicRule = getBasicRuleByName(ruleName);
        ComplexRule complexRule = getComplexRuleByName(ruleName);

        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        if (basicRule != null) {
            basicRule.setRelease(value);
            session.update(basicRule);
            session.getTransaction().commit();
            return true;
        } else if (complexRule != null) {
            complexRule.setRelease(value);
            session.update(complexRule);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    /**
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllBasicRules() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.list();
        session.getTransaction().commit();

        return rules;
    }

    /**
     * 
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllComplexRules() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.list();
        session.getTransaction().commit();

        return rules;
    }

    /**
     * @param ruleID
     * @param medium 
     * @param format 
     * @param userID 
     * @return {@link String}
     */
    @SuppressWarnings("unchecked")
    public static String getSubscriptionID(int ruleID, String medium, String format, int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> museID = crit.add(Restrictions.and(Restrictions.and(Restrictions.eq("ruleID", ruleID), Restrictions.eq("medium", medium)), Restrictions.and(Restrictions.eq("format", format), Restrictions.eq("userID", userID)))).list();
        if (museID.size() == 1) {
            return museID.get(0).getSubscriptionID();
        }
        return null;
    }

    /**
     * @param sensorID
     * @param newStatus if true --> increment; false = decrement
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean updateSensorCount(String sensorID, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("sensorID", sensorID)).list();

        if (sensors.size() == 1) {
            Sensor sensor = sensors.get(0);
            if (newStatus) {
                // increment count
                sensor.setInUse(sensor.getInUse()+1);
            } else {
                // decrement count
                sensor.setInUse(sensor.getInUse()-1);
            }
            
            session.saveOrUpdate(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    /**
     * @param ruleName 
     * @return {@link Boolean}
     */
    public static boolean deleteRule(String ruleName) {
        BasicRule basicRule = HibernateUtil.getBasicRuleByName(ruleName);
        ComplexRule complexRule = HibernateUtil.getComplexRuleByName(ruleName);

        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        if (basicRule != null) {
            session.delete(basicRule);
            session.getTransaction().commit();
            return true;
        } else if (complexRule != null) {
            session.delete(complexRule);
            session.getTransaction().commit();
            return true;
        }
        return false;  
    }

    /**
     * @param sensorID
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean deleteSensorByID(String sensorID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("sensorID", sensorID)).list();

        if (sensors.size() == 1) {
            Sensor sensor = sensors.get(0);
            if (sensor.getInUse() > 0) {
                return false;
            }
            session.delete(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    /**
     * @return {@link List}
     * 
     */
    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllPublishedBR() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("release", true)).list();
        session.getTransaction().commit();
        return rules;
    }

    /**
     * @return {@link List}
     * 
     */
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllPublishedCR() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq("release", true)).list();
        session.getTransaction().commit();
        return rules;
    }

    /**
     * @param parameterId
     * @param ruleID
     * @return is rule subscribed to user parameterId
     */
    @SuppressWarnings("unchecked")
    public static boolean isSubscribed(String id, int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add(Restrictions.and(Restrictions.eq("userID", Integer.valueOf(id)), Restrictions.eq("ruleID", ruleID))).list();

        if (subscription.size() == 1) {
            return true;
        }
        return false;
    }

    /**
     * @param subscriptionID 
     * @param userID
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean deleteSubscription(String subscriptionID, String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.and(Restrictions.eq("userID", Integer.valueOf(userID)), Restrictions.eq("subscriptionID", subscriptionID))).list();

        if (subscriptions.size() == 1) {
            Subscription subscription = subscriptions.get(0);
            session.delete(subscription);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    /**
     * @param ruleName
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean existsBasicRuleName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("name", ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @param ruleName
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean existsComplexRuleName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq("name", ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * @param userID
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static List<Subscription> getUserSubscriptions(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.eq("userID", Integer.valueOf(userID))).list();
        session.getTransaction().commit();

        return subscriptions;
    }
    
    /**
     * 
     * @param ruleID
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<Subscription> getSubscriptionsFromRuleID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.eq("ruleID", ruleID)).list();
        session.getTransaction().commit();

        return subscriptions;
    }

    /**
     * @param ruleID
     * @return {@link BasicRule}
     */
    @SuppressWarnings("unchecked")
    public static BasicRule getBasicRuleByID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("parameterId", ruleID)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            BasicRule rule = rules.get(0);
            return rule;
        }
        return null;
    }
    
    public static List<BasicRule> getBasicRulesByID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq("parameterId", ruleID)).list();
        session.getTransaction().commit();
        return rules;
    }

    /**
     * @param ruleID
     * @return {@link ComplexRule}
     */
    @SuppressWarnings("unchecked")
    public static ComplexRule getComplexRuleByID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq("parameterId", ruleID)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            ComplexRule rule = rules.get(0);
            return rule;
        }
        return null;
    }

    /**
     * @param complexRule
     */
    public static void addComplexRule(ComplexRule complexRule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(complexRule);
        session.getTransaction().commit();
    }

    /**
     * @param sensorID
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean existsSensor(String sensorID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Sensor.class);
        List<Sensor> sensors = crit.add(Restrictions.eq("sensorID", sensorID)).list();
        session.getTransaction().commit();
        if (sensors.size() != 0) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @param userID
     * @return List
     */
    @SuppressWarnings("unchecked")
    public static List<Subscription> getSubscriptionfromUserID(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.eq("userID", userID)).list();
        session.getTransaction().commit();
        return subscriptions;
    }

    /**
     * @param userID 
     * @return otherAdminsExists
     */
    @SuppressWarnings("unchecked")
    public static boolean otherAdminsExist(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.and(Restrictions.not(Restrictions.eq("parameterId", userID)), Restrictions.eq("role", UserRole.ADMIN))).list();

        if (users.size() >= 1) {
            return true;
        }
        return false;
    }

    /**
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<User> deleteUnregisteredUser() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq("role", UserRole.NOT_REGISTERED_USER)).list();
        
        return users;
    }
    
    /**
     * @param ruleID
     * @return {@link Boolean}
     */
    @SuppressWarnings("unchecked")
    public static boolean ruleIsSubscribed(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add(Restrictions.eq("ruleID", ruleID)).list();

        if (subscription.size() >= 1) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static List<Subscription> getAllSubscriptions() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.list();
        session.getTransaction().commit();
        return subscriptions;
    }
    
    /**
     * 
     * @param ruleID 
     * @param medium
     * @param format
     * @param userID 
     * @return true if such subscription already exists
     */
    @SuppressWarnings("unchecked")
    public static boolean existsSubscription(int ruleID, String medium, String format, int userID){
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add(Restrictions.and(Restrictions.and(Restrictions.eq("ruleID", ruleID), Restrictions.eq("medium", medium)), Restrictions.and(Restrictions.eq("format", format), Restrictions.eq("userID", userID)))).list();

        if (subscription.size() >= 1) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param ruleID
     * @return true if other subscriptions of this rule exist
     */
    @SuppressWarnings("unchecked")
    public static boolean existsOtherSubscriptions(int ruleID){
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add((Restrictions.eq("ruleID", ruleID))).list();

        if (subscription.size() >= 1) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param row
     * @param text
     * @return {@link List}
     */
    public static List<BasicRule> searchBasic(String row, String text) {
        text = "%" + text + "%";
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.and(Restrictions.eq("release", true), Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }
    
    /**
     * 
     * @param userID
     * @param row
     * @param text
     * @return {@link List}
     */
    public static List<BasicRule> searchOwnBasic(String userID, String row, String text) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.and(Restrictions.eq("ownerID", Integer.valueOf(userID)), Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }
    
    /**
     * 
     * @param row
     * @param text
     * @return {@link List}
     */
    public static List<ComplexRule> searchComplex(String row, String text) {
        text = "%" + text + "%";
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.and(Restrictions.eq("release", true), Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }
    
    /**
     * 
     * @param userID
     * @param row
     * @param text
     * @return {@link List}
     */
    public static List<ComplexRule> searchOwnComplex(String userID, String row, String text) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.and(Restrictions.eq("ownerID", Integer.valueOf(userID)), Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }
}