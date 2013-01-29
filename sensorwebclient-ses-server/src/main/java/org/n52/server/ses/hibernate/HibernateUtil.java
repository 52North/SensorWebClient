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

import static org.hibernate.FetchMode.JOIN;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;
import static org.n52.shared.serializable.pojos.UserRole.NOT_REGISTERED_USER;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    private static final String ROLE = "role";

    private static final String SUBSCRIPTION_ID = "subscriptionID";

    private static final String USER_ID = "userID";

    private static final String FORMAT = "format";

    private static final String MEDIUM = "medium";

    private static final String RULE_ID = "ruleID";

    private static final String TIMESERIES_ID = "timeseriesId";

    private static final String PUBLISHED = "published";

    private static final String RULE_NAME = "name";

    private static final String OWNER_ID = "ownerID";

    private static final String ACTIVE = "active";

    private static final String REGISTER_ID = "registerID";

    private static final String ID = "id";

    private static final String MOBILE_NR = "handyNr";

    private static final String E_MAIL = "eMail";

    private static final String USER_NAME = "userName";

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
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

    public static void saveUser(User user) {
        user.setActive(true);
        user.setEmailVerified(true);
        user.setPasswordChanged(false);
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(user);
        session.getTransaction().commit();
    }

    public static boolean existsUserName(String userName) {
        boolean userNameExists = false;
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        if (crit.add(Restrictions.eq(USER_NAME, userName)).list().size() > 0) {
            userNameExists = true;
        }
        session.getTransaction().commit();
        return userNameExists;
    }

    public static boolean existsEMail(String eMail) {
        boolean eMailExists = false;
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        if (crit.add(Restrictions.eq(E_MAIL, eMail)).list().size() > 0) {
            eMailExists = true;
        }
        session.getTransaction().commit();
        return eMailExists;
    }

    @Deprecated
    public static boolean existsHandy(String handy) {
        boolean handyExists = false;
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        if (crit.add(Restrictions.eq(MOBILE_NR, handy)).list().size() > 0) {
            handyExists = true;
        }
        session.getTransaction().commit();
        return handyExists;
    }

    @SuppressWarnings("unchecked")
    public static boolean updateUserRole(int userId, UserRole role) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(ID, userId)).list();
        if (users.size() != 1) {
            return false;
        }
        User user = users.get(0);
        user.setRole(role);
        session.saveOrUpdate(user);
        session.getTransaction().commit();
        return true;
    }

    @SuppressWarnings("unchecked")
    public static boolean updateUserStatus(int userID, boolean active) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(ID, userID)).list();
        if (users.size() != 1) {
            return false;
        }
        User user = users.get(0);
        user.setActivated(active);
        session.saveOrUpdate(user);
        session.getTransaction().commit();
        return true;
    }

    @SuppressWarnings("unchecked")
    public static User getUserBy(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(ID, userID)).list();
        User user = users.get(0);
        session.getTransaction().commit();
        return user;
    }

    @SuppressWarnings("unchecked")
    public static User getUserBy(String registerID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(REGISTER_ID, registerID)).list();
        User user;
        if (users.size() == 1) {
            user = users.get(0);
        }
        else {
            user = null;
        }
        session.getTransaction().commit();
        return user;
    }

    public static void updateUser(User user) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.update(user);
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public static boolean deleteUserBy(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(ID, userID)).list();
        if (users.size() == 1) {
            User user = users.get(0);
            session.delete(user);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static User findUserBy(String userName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(USER_NAME, userName)).list();
        if (users.size() != 1) {
            return null;
        }
        User user = users.get(0);
        session.getTransaction().commit();
        return user;
    }

    @SuppressWarnings("unchecked")
    public static List<User> getAllUsers() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(ACTIVE, true)).list();

        session.getTransaction().commit();
        return users;
    }
    
    
    

    public static void saveBasicRule(BasicRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(rule);
        session.getTransaction().commit();
    }

    
    /**
     * @deprecated no sharing anymore
     */
    public static void saveCopiedBasicRule(BasicRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(rule);
        session.getTransaction().commit();
    }

    
    /**
     * @deprecated no sharing anymore
     */
    public static void saveCopiedComplexRule(ComplexRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(rule);
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllBasicRulesBy(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(OWNER_ID, Integer.valueOf(userID))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllComplexRulesBy(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(OWNER_ID, Integer.valueOf(userID))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static BasicRule getBasicRuleByName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            return rules.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static ComplexRule getComplexRuleByName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            return rules.get(0);
        }
        return null;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void saveSubscription(Subscription subscription) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(subscription);
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllOtherBasicRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.not(Restrictions.eq(OWNER_ID, Integer.valueOf(userID)))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllOtherPublishedBasicRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.and(Restrictions.not(Restrictions.eq(OWNER_ID,
                                                                                           Integer.valueOf(userID))),
                                                          Restrictions.eq(PUBLISHED, true))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllOtherPublishedComplexRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.and(Restrictions.not(Restrictions.eq(OWNER_ID,
                                                                                             Integer.valueOf(userID))),
                                                            Restrictions.eq(PUBLISHED, true))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static void updateBasicRuleSubscribtion(String ruleName, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        if (rules.size() == 1) {
            BasicRule rule = rules.get(0);
            rule.setSubscribed(newStatus);
            session.saveOrUpdate(rule);
        }
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public static void updateComplexRuleSubscribtion(String ruleName, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        if (rules.size() == 1) {
            ComplexRule rule = rules.get(0);
            rule.setSubscribed(newStatus);
            session.saveOrUpdate(rule);
        }
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllOtherComplexRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.not(Restrictions.eq(OWNER_ID, Integer.valueOf(userID)))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<TimeseriesFeed> getTimeseriesFeeds() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class);
        List<TimeseriesFeed> timeseriesFeeds = crit.list();
        session.getTransaction().commit();
        return timeseriesFeeds;
    }

    @SuppressWarnings("unchecked")
    public static TimeseriesFeed getTimeseriesFeedById(String timeseriesId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class)
                            .add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
        List<TimeseriesFeed> timeseriesFeeds = crit.list();
        session.getTransaction().commit();
        if (timeseriesFeeds.size() != 0) {
            return timeseriesFeeds.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<TimeseriesFeed> getActiveTimeseriesFeeds() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class);
        List<TimeseriesFeed> timeseriesFeeds = crit.add(Restrictions.eq(ACTIVE, true)).list();
        session.getTransaction().commit();
        return timeseriesFeeds;
    }

    @SuppressWarnings("unchecked")
    public static boolean updateTimeseriesFeed(String timeseriesId, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class)
                            .setFetchMode("TimeseriesMetadata", JOIN)
                            .add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
        List<TimeseriesFeed> sensors = crit.list();

        if (sensors.size() == 1) {
            TimeseriesFeed sensor = sensors.get(0);
            sensor.setActive(newStatus);
            session.saveOrUpdate(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    
    
    
    
    
    
    
    
    
    
    
    public static boolean publishRule(String ruleName, boolean value) {
        BasicRule basicRule = getBasicRuleByName(ruleName);
        ComplexRule complexRule = getComplexRuleByName(ruleName);
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        if (basicRule != null) {
            basicRule.setPublished(value);
            session.update(basicRule);
            session.getTransaction().commit();
            return true;
        }
        else if (complexRule != null) {
            complexRule.setPublished(value);
            session.update(complexRule);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllBasicRules() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.list();
        session.getTransaction().commit();

        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllComplexRules() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.list();
        session.getTransaction().commit();

        return rules;
    }

    
    
    
    
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    public static String getSubscriptionID(int ruleID, String medium, String format, int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> museID = crit.add(Restrictions.and(Restrictions.and(Restrictions.eq(RULE_ID, ruleID),
                                                                               Restrictions.eq(MEDIUM, medium)),
                                                              Restrictions.and(Restrictions.eq(FORMAT, format),
                                                                               Restrictions.eq(USER_ID, userID)))).list();
        if (museID.size() == 1) {
            return museID.get(0).getSubscriptionID();
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static boolean updateSensorCount(String timeseriesId, boolean newStatus) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class);
        List<TimeseriesFeed> sensors = crit.add(Restrictions.eq(TIMESERIES_ID, timeseriesId)).list();
        if (sensors.size() == 1) {
            TimeseriesFeed sensor = sensors.get(0);
            if (newStatus) {
                // increment count
                sensor.setInUse(sensor.getInUse() + 1);
            }
            else {
                // decrement count
                sensor.setInUse(sensor.getInUse() - 1);
            }

            session.saveOrUpdate(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    public static boolean deleteRule(String ruleName) {
        BasicRule basicRule = HibernateUtil.getBasicRuleByName(ruleName);
        ComplexRule complexRule = HibernateUtil.getComplexRuleByName(ruleName);
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();

        if (basicRule != null) {
            session.delete(basicRule);
            session.getTransaction().commit();
            return true;
        }
        else if (complexRule != null) {
            session.delete(complexRule);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }
    
    
    
    
    
    
    
    
    

    @SuppressWarnings("unchecked")
    public static boolean deleteTimeseriesFeed(String timeseriesId) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class)
                            .add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
        List<TimeseriesFeed> sensors = crit.list();

        if (sensors.size() == 1) {
            TimeseriesFeed sensor = sensors.get(0);
            if (sensor.getInUse() > 0) {
                return false;
            }
            session.delete(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllPublishedBR() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(PUBLISHED, true)).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllPublishedCR() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(PUBLISHED, true)).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static boolean isSubscribed(String id, int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add(Restrictions.and(Restrictions.eq(USER_ID, Integer.valueOf(id)),
                                                                    Restrictions.eq(RULE_ID, ruleID))).list();

        if (subscription.size() == 1) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean deleteSubscription(String subscriptionID, String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.and(Restrictions.eq(USER_ID, Integer.valueOf(userID)),
                                                                     Restrictions.eq(SUBSCRIPTION_ID, subscriptionID))).list();

        if (subscriptions.size() == 1) {
            Subscription subscription = subscriptions.get(0);
            session.delete(subscription);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean existsBasicRuleName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() != 0) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean existsComplexRuleName(String ruleName) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        session.getTransaction().commit();
        if (rules.size() != 0) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Subscription> getUserSubscriptions(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.eq(USER_ID, Integer.valueOf(userID))).list();
        session.getTransaction().commit();

        return subscriptions;
    }

    @SuppressWarnings("unchecked")
    public static List<Subscription> getSubscriptionsFromRuleID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.eq(RULE_ID, ruleID)).list();
        session.getTransaction().commit();

        return subscriptions;
    }

    @SuppressWarnings("unchecked")
    public static BasicRule getBasicRuleByID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(ID, ruleID)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            BasicRule rule = rules.get(0);
            return rule;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getBasicRulesByID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(ID, ruleID)).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static ComplexRule getComplexRuleByID(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(ID, ruleID)).list();
        session.getTransaction().commit();
        if (rules.size() == 1) {
            ComplexRule rule = rules.get(0);
            return rule;
        }
        return null;
    }

    public static void addComplexRule(ComplexRule complexRule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(complexRule);
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    public static boolean existsTimeseriesFeed(String timeseriesId) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class)
                            .add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
        List<TimeseriesFeed> timeseriesFeeds = crit.list();
        session.getTransaction().commit();
        return timeseriesFeeds.size() != 0;
    }

    @SuppressWarnings("unchecked")
    public static List<Subscription> getSubscriptionfromUserID(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.add(Restrictions.eq(USER_ID, userID)).list();
        session.getTransaction().commit();
        return subscriptions;
    }

    @SuppressWarnings("unchecked")
    public static boolean otherAdminsExist(int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.and(Restrictions.not(Restrictions.eq(ID, userID)),
                                                     Restrictions.eq(ROLE, ADMIN))).list();

        if (users.size() >= 1) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<User> deleteUnregisteredUser() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(User.class);
        List<User> users = crit.add(Restrictions.eq(ROLE, NOT_REGISTERED_USER)).list();

        return users;
    }

    @SuppressWarnings("unchecked")
    public static boolean ruleIsSubscribed(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add(Restrictions.eq(RULE_ID, ruleID)).list();

        if (subscription.size() >= 1) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<Subscription> getAllSubscriptions() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscriptions = crit.list();
        session.getTransaction().commit();
        return subscriptions;
    }

    @SuppressWarnings("unchecked")
    public static boolean existsSubscription(int ruleID, String medium, String format, int userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add(Restrictions.and(Restrictions.and(Restrictions.eq(RULE_ID, ruleID),
                                                                                     Restrictions.eq(MEDIUM, medium)),
                                                                    Restrictions.and(Restrictions.eq(FORMAT, format),
                                                                                     Restrictions.eq(USER_ID, userID)))).list();

        if (subscription.size() >= 1) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean existsOtherSubscriptions(int ruleID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(Subscription.class);
        List<Subscription> subscription = crit.add( (Restrictions.eq(RULE_ID, ruleID))).list();

        if (subscription.size() >= 1) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> searchBasic(String row, String text) {
        text = "%" + text + "%";
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.and(Restrictions.eq(PUBLISHED, true),
                                                          Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> searchOwnBasic(String userID, String row, String text) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.and(Restrictions.eq(OWNER_ID, Integer.valueOf(userID)),
                                                          Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> searchComplex(String row, String text) {
        text = "%" + text + "%";
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.and(Restrictions.eq(PUBLISHED, true),
                                                            Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<ComplexRule> searchOwnComplex(String userID, String row, String text) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.and(Restrictions.eq(OWNER_ID, Integer.valueOf(userID)),
                                                            Restrictions.ilike(row, text))).list();
        session.getTransaction().commit();
        return rules;
    }

    public static TimeseriesMetadata getTimeseriesMetadata(String timeseriesId) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        TimeseriesMetadata metadata = (TimeseriesMetadata) session
                                            .createCriteria(TimeseriesMetadata.class)
                                            .add(Restrictions.eq(TIMESERIES_ID, timeseriesId))
                                            .uniqueResult();
        session.getTransaction().commit();
        return metadata;
        
    }
}