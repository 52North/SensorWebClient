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

import static java.lang.Integer.valueOf;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;
import static org.n52.shared.serializable.pojos.UserRole.NOT_REGISTERED_USER;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.n52.server.ses.db.CriteriaExecution;
import org.n52.server.ses.db.HibernateDaoUtil;
import org.n52.server.ses.feeder.SosSesFeeder;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserRole;

public class HibernateUtil extends HibernateDaoUtil {

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
    
    private static final String RULE_UUID = "uuid";

    public static void save(User user) {
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

    public static User getUserBy(final String registerID) {
        return execute(new CriteriaExecution<User>() {
            @Override
            public User execute(Session session) {Criteria crit = session.createCriteria(User.class);
                Criteria criteria = crit.add(Restrictions.eq(REGISTER_ID, registerID));
                return (User) criteria.uniqueResult();
            }
        });
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

    public static void saveBasicRule(final BasicRule rule) {
    	TimeseriesMetadata transientMetadata = rule.getTimeseriesMetadata();
		String timeseriesId = transientMetadata.getTimeseriesId();
		TimeseriesMetadata persistedMetadata = getTimeseriesMetadata(timeseriesId);
		if (persistedMetadata == null) {
			saveTimeseriesMetadata(transientMetadata);
			rule.setTimeseriesMetadata(getTimeseriesMetadata(timeseriesId));
		} else {
			rule.setTimeseriesMetadata(persistedMetadata);
		}
    	execute(new CriteriaExecution<Void>() {
			@Override
			public Void execute(final Session session) {
				session.saveOrUpdate(rule);
				return null;
			}
		});
    }

    /**
     * @deprecated no sharing anymore
     */
    @Deprecated
    public static void saveCopiedBasicRule(BasicRule rule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.save(rule);
        session.getTransaction().commit();
    }

    /**
     * @deprecated no sharing anymore
     */
    @Deprecated
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

    @Deprecated
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllComplexRulesBy(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(OWNER_ID, Integer.valueOf(userID))).list();
        session.getTransaction().commit();
        return rules;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public static ComplexRule getComplexRuleByName(String rulename) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(RULE_NAME, rulename)).list();
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
        Criterion restriction = Restrictions.not(Restrictions.eq(OWNER_ID, Integer.valueOf(userID)));
        List<BasicRule> rules = crit.add(restriction).list();
        session.getTransaction().commit();
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllOtherPublishedBasicRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        Criterion isOwner = Restrictions.not(Restrictions.eq(OWNER_ID, valueOf(userID)));
        SimpleExpression isPublished = Restrictions.eq(PUBLISHED, true);
        List<BasicRule> rules = crit.add(Restrictions.and(isOwner, isPublished)).list();
        session.getTransaction().commit();
        return rules;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllOtherPublishedComplexRules(String userID) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        Criterion isOwner = Restrictions.not(Restrictions.eq(OWNER_ID, valueOf(userID)));
        SimpleExpression isPublished = Restrictions.eq(PUBLISHED, true);
        List<ComplexRule> rules = crit.add(Restrictions.and(isOwner, isPublished)).list();
        session.getTransaction().commit();
        return rules;
    }

    public static void subscribeBasicRule(final String uuid) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(final Session session) {
                Criteria criteria = session.createCriteria(BasicRule.class);
                criteria.add(Restrictions.eq(RULE_UUID, uuid));
                BasicRule rule = (BasicRule) criteria.uniqueResult();
                rule.setSubscribed(true);
                session.saveOrUpdate(rule);
                return null;
            }
        });
    }
    
    public static void unsubscribeBasicRule(final String uuid) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(final Session session) {
                Criteria criteria = session.createCriteria(BasicRule.class);
                criteria.add(Restrictions.eq(RULE_UUID, uuid));
                BasicRule rule = (BasicRule) criteria.uniqueResult();
                rule.setSubscribed(false);
                session.saveOrUpdate(rule);
                return null;
            }
        });
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public static void updateComplexRuleSubscribtion(String ruleName, boolean subscribed) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(ComplexRule.class);
        List<ComplexRule> rules = crit.add(Restrictions.eq(RULE_NAME, ruleName)).list();
        if (rules.size() == 1) {
            ComplexRule rule = rules.get(0);
            rule.setSubscribed(subscribed);
            session.saveOrUpdate(rule);
        }
        session.getTransaction().commit();
    }

    @Deprecated
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
        Session session = getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(TimeseriesFeed.class);
        List<TimeseriesFeed> timeseriesFeeds = crit.list();
        session.getTransaction().commit();
        return timeseriesFeeds;
    }

    public static TimeseriesFeed getTimeseriesFeedById(final String timeseriesId) {
        return execute(new CriteriaExecution<TimeseriesFeed>() {
            @Override
            public TimeseriesFeed execute(Session session) {
                Criteria criteria = session.createCriteria(TimeseriesFeed.class);
                criteria.add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
                return (TimeseriesFeed) criteria.uniqueResult();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static List<TimeseriesFeed> getActiveTimeseriesFeeds() {
        return execute(new CriteriaExecution<List<TimeseriesFeed>>() {
            @Override
            public List<TimeseriesFeed> execute(Session session) {
                Criteria criteria = session.createCriteria(TimeseriesFeed.class);
                criteria.add(Restrictions.eq(ACTIVE, true));
                return (List<TimeseriesFeed>) criteria.list();
            }
        });
    }

    /**
     * Deactivates the timeseries to be not considered for feeding by the {@link SosSesFeeder} 
     * 
     * @param timeseriesId
     *        the timeseries' id.
     */
    public static void deactivateTimeseriesFeed(final String timeseriesId) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(Session session) {
                Criteria criteria = session.createCriteria(TimeseriesFeed.class);
                criteria.add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
                TimeseriesFeed uniqueResult = (TimeseriesFeed) criteria.uniqueResult();
                uniqueResult.setActive(false);
                session.saveOrUpdate(uniqueResult);
                return null;
            }
        });
    }
    
    /**
     * Activates the timeseries to be used by the {@link SosSesFeeder}.
     * 
     * @param timeseriesId
     *        the timeseries' id.
     */
    public static void activateTimeseriesFeed(final String timeseriesId) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(Session session) {
                Criteria criteria = session.createCriteria(TimeseriesFeed.class);
                criteria.add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
                TimeseriesFeed uniqueResult = (TimeseriesFeed) criteria.uniqueResult();
                uniqueResult.setActive(true);
                session.saveOrUpdate(uniqueResult);
                return null;
            }
        });
    }

    public static void publishRule(final String ruleName, final boolean value) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(Session session) {
                BasicRule basicRule = getBasicRuleByUuid(ruleName);
                if (basicRule != null) {
                    basicRule.setPublished(value);
                    session.update(basicRule);
                    session.getTransaction().commit();
                }
                else {
                    ComplexRule complexRule = getComplexRuleByName(ruleName);
                    complexRule.setPublished(value);
                    session.update(complexRule);
                    session.getTransaction().commit();
                }
                return null;
            }
        });
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

    @Deprecated
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
    
    public static Subscription getSubscriptionIdByRuleIdAndUserId(final int ruleID, final int userID) {
    	return execute(new CriteriaExecution<Subscription>() {
			@Override
			public Subscription execute(Session session) {
				Criteria crit = session.createCriteria(Subscription.class);
				return (Subscription) crit.add(
						Restrictions.and(Restrictions.eq(RULE_ID, ruleID),
								Restrictions.eq(USER_ID, userID))).uniqueResult();
			}
		});
    }
    
    public static boolean deleteRule(String uuid) {
        BasicRule basicRule = HibernateUtil.getBasicRuleByUuid(uuid);
        ComplexRule complexRule = HibernateUtil.getComplexRuleByName(uuid);
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
        Criteria crit = session.createCriteria(TimeseriesFeed.class).add(Restrictions.eq(TIMESERIES_ID, timeseriesId));
        List<TimeseriesFeed> sensors = crit.list();

        if (sensors.size() == 1) {
            TimeseriesFeed sensor = sensors.get(0);
            if (sensor.getUsedCounter() > 0) {
                return false;
            }
            session.delete(sensor);
            session.getTransaction().commit();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static List<BasicRule> getAllPublishedBasicRules() {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        Criteria crit = session.createCriteria(BasicRule.class);
        List<BasicRule> rules = crit.add(Restrictions.eq(PUBLISHED, true)).list();
        session.getTransaction().commit();
        return rules;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public static List<ComplexRule> getAllPublishedCcomplexRules() {
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

    public static void deleteSubscription(final String subscriptionID, final String userID) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(final Session session) {
                Criteria criteria = session.createCriteria(Subscription.class);
                criteria.add(Restrictions.and(Restrictions.eq(USER_ID, Integer.valueOf(userID)),
                                              Restrictions.eq(SUBSCRIPTION_ID, subscriptionID)));
                Subscription subscription = (Subscription) criteria.uniqueResult();
                session.delete(subscription);
                return null;
            }
        });
    }
    
    public static void deactivateSubscription(final String subscriptionID, final String userID) {
        execute(new CriteriaExecution<Void>() {
            @Override
            public Void execute(final Session session) {
                Criteria criteria = session.createCriteria(Subscription.class);
                criteria.add(Restrictions.and(Restrictions.eq(USER_ID, Integer.valueOf(userID)),
                                              Restrictions.eq(SUBSCRIPTION_ID, subscriptionID)));
                Subscription subscription = (Subscription) criteria.uniqueResult();
                subscription.setActive(false);
                session.update(subscription);
                return null;
            }
        });
    }
    
    public static void activateSubscription(final int ruleID, final int userID) {
    	execute(new CriteriaExecution<Void>() {
			@Override
			public Void execute(Session session) {
				Criteria criteria = session.createCriteria(Subscription.class);
				criteria.add(Restrictions.and(Restrictions.eq(RULE_ID, ruleID),
                        Restrictions.eq(USER_ID, userID)));
				Subscription subscription = (Subscription) criteria.uniqueResult();
				subscription.setActive(true);
				session.update(subscription);
				return null;
			}
		});
    }

    public static boolean existsBasicRule(final String uuid) {
        return execute(new CriteriaExecution<Boolean>() {
            @Override
            public Boolean execute(final Session session) {
                Criteria criteria = session.createCriteria(BasicRule.class);
                criteria.add(Restrictions.eq(RULE_UUID, uuid));
                BasicRule rule = (BasicRule) criteria.uniqueResult();
                return rule != null;
            }
        }).booleanValue();
    }

    @Deprecated
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

    public static BasicRule getBasicRuleByUuid(final String uuid) {
    	return execute(new CriteriaExecution<BasicRule>() {
            @Override
            public BasicRule execute(final Session session) {
                Criteria criteria = session.createCriteria(BasicRule.class);
                criteria.add(Restrictions.eq(RULE_UUID, uuid));
                BasicRule rule = (BasicRule) criteria.uniqueResult();
                return rule;
            }
        });
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

	@Deprecated
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

	@Deprecated
    public static void addComplexRule(ComplexRule complexRule) {
        Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(complexRule);
        session.getTransaction().commit();
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

    @Deprecated
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

    @Deprecated
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

    public static TimeseriesMetadata getTimeseriesMetadata(final String timeseriesId) {
    	return execute(new CriteriaExecution<TimeseriesMetadata>() {
    		@Override
    		public TimeseriesMetadata execute(Session session) {
    			Criteria criteria = session.createCriteria(TimeseriesMetadata.class);
    			criteria.add(Restrictions.eq(TIMESERIES_ID,timeseriesId)).uniqueResult();
    			return (TimeseriesMetadata) criteria.uniqueResult();
    		}
		});
    }
    
    public static void saveTimeseriesMetadata(final TimeseriesMetadata metadata) {
    	execute(new CriteriaExecution<Void>() {
    		@Override
    		public Void execute(Session session) {
    			session.saveOrUpdate(metadata);
				return null;
    		}
		});
    }
}