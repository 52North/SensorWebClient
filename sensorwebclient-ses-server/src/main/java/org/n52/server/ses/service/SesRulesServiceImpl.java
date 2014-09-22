/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.service;

import static java.lang.String.valueOf;
import static java.util.UUID.randomUUID;
import static org.n52.server.ses.feeder.SosSesFeeder.getSosSesFeederInstance;
import static org.n52.server.ses.hibernate.HibernateUtil.activateTimeseriesFeed;
import static org.n52.server.ses.hibernate.HibernateUtil.deactivateSubscription;
import static org.n52.server.ses.hibernate.HibernateUtil.deactivateTimeseriesFeed;
import static org.n52.server.ses.hibernate.HibernateUtil.deleteSubscription;
import static org.n52.server.ses.hibernate.HibernateUtil.existsBasicRule;
import static org.n52.server.ses.hibernate.HibernateUtil.existsComplexRuleName;
import static org.n52.server.ses.hibernate.HibernateUtil.existsSubscription;
import static org.n52.server.ses.hibernate.HibernateUtil.getSubscriptionIdByRuleIdAndUserId;
import static org.n52.server.ses.hibernate.HibernateUtil.getTimeseriesFeedById;
import static org.n52.server.ses.hibernate.HibernateUtil.getTimeseriesMetadata;
import static org.n52.server.ses.hibernate.HibernateUtil.subscribeBasicRule;
import static org.n52.server.ses.hibernate.HibernateUtil.unsubscribeBasicRule;
import static org.n52.server.ses.hibernate.HibernateUtil.updateComplexRuleSubscribtion;
import static org.n52.server.ses.util.SesServerUtil.getTimeseriesIdsFromEML;
import static org.n52.server.ses.util.SesServerUtil.isLoggedInAdmin;
import static org.n52.shared.responses.SesClientResponseType.DELETE_RULE_OK;
import static org.n52.shared.responses.SesClientResponseType.ERROR_SUBSCRIBE_FEEDER;
import static org.n52.shared.responses.SesClientResponseType.ERROR_SUBSCRIBE_SES;
import static org.n52.shared.responses.SesClientResponseType.OK;
import static org.n52.shared.responses.SesClientResponseType.REQUIRES_LOGIN;
import static org.n52.shared.responses.SesClientResponseType.USER_SUBSCRIPTIONS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.n52.client.service.SesRuleService;
import org.n52.oxf.adapter.OperationResult;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.eml.BasicRule_1_Builder;
import org.n52.server.ses.eml.BasicRule_2_Builder;
import org.n52.server.ses.eml.BasicRule_3_Builder;
import org.n52.server.ses.eml.BasicRule_4_Builder;
import org.n52.server.ses.eml.BasicRule_5_Builder;
import org.n52.server.ses.eml.ComplexRule_Builder;
import org.n52.server.ses.eml.ComplexRule_BuilderV2;
import org.n52.server.ses.eml.Meta_Builder;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.util.RulesUtil;
import org.n52.server.ses.util.SearchUtil;
import org.n52.server.ses.util.SesServerUtil;
import org.n52.shared.Constants;
import org.n52.shared.LogicalOperator;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.responses.SesClientResponseType;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SesRulesServiceImpl implements SesRuleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SesRulesServiceImpl.class);
    
    private ServerSessionStore sessionStore; // injected

    @Override
    public SesClientResponse subscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("subscribe to rule with UUID: {}", uuid);
            LOGGER.debug("notification type:  {}", medium);

            String userID = sessionStore.getLoggedInUserId(sessionInfo);

            // get EML from DB
            BasicRule basicRule = HibernateUtil.getBasicRuleByUuid(uuid);
            ComplexRule complexRule = HibernateUtil.getComplexRuleByName(uuid);

            // get user from DBs
            User user = HibernateUtil.getUserBy(Integer.valueOf(userID));

            // subscribe basic rules from other user
            if (basicRule != null && basicRule.getOwnerID() != Integer.valueOf(userID)) {
                // copy rule and then continue
                SesClientResponse response = copy(userID, uuid);
                if (response.getType().equals(SesClientResponseType.RULE_NAME_EXISTS)) {
                    // rule name exists
                    return response;
                }
                // new rule name = originalRulaName_USERNAME
                String newRuleName = basicRule.getName() + "_" + user.getUserName();
                basicRule = HibernateUtil.getBasicRuleByUuid(uuid);
            }
            // subscribe complex rule from other user
            if (complexRule != null && complexRule.getOwnerID() != Integer.valueOf(userID)) {
                // copy rule and then continue
                SesClientResponse response = copy(userID, uuid);
                if (response.equals(SesClientResponseType.RULE_NAME_EXISTS)) {
                    // rule name exists
                    return response;
                }
                // new rule name = originalRulaName_USERNAME
                String newRuleName = complexRule.getName() + "_" + user.getUserName();
                complexRule = HibernateUtil.getComplexRuleByName(newRuleName);
            }

            // for all formats check whether such subscription already exists
            String content = "";
            String[] formats = eml.split("_");
            String[] media = medium.split("_");
            boolean subscriptionExists = false;
            boolean subscriptionsExists = false;

            for (int k = 0; k < media.length; k++) {
                for (int i = 0; i < formats.length; i++) {
                    subscriptionExists = false;

                    if (basicRule != null
                            && existsSubscription(basicRule.getId(), media[k], formats[i], Integer.valueOf(userID))) {
                        subscriptionExists = true;
                        subscriptionsExists = true;
                        HibernateUtil.activateSubscription(basicRule.getId(), Integer.valueOf(userID));
                        HibernateUtil.subscribeBasicRule(uuid);
                    }
                    else if (complexRule != null
                            && existsSubscription(complexRule.getId(), media[k], formats[i], Integer.valueOf(userID))) {
                        subscriptionExists = true;
                        subscriptionsExists = true;
                    }
                    // if subscription does not exists
                    if ( !subscriptionExists) {
                        // create meta pattern
                        String meta = "";
                        content = "";

                        // create meta pattern for basic rule
                        if (basicRule != null) {
                            content = basicRule.getEml();

                            if ( !basicRule.getType().equals("BR5")) {
                                if (formats[i].equals("Text")) {
                                    meta = Meta_Builder.createTextMeta(user, basicRule.getName(), media[k]);
                                }
                                else {
                                    meta = Meta_Builder.createXMLMeta(user, basicRule.getName(), media[k], formats[i]);
                                }
                                // other rule types
                            }
                            else {
                                if (formats[i].equals("Text")) {
                                    TimeseriesMetadata metadata = basicRule.getTimeseriesMetadata();
                                    meta = Meta_Builder.createTextFailureMeta(user,
                                                                              basicRule,
                                                                              media[k],
                                                                              metadata.getProcedure());
                                }
                                else {
                                    meta = Meta_Builder.createXMLMeta(user, basicRule.getName(), media[k], formats[i]);
                                }
                            }
                            // create meta pattern for complex rule
                        }
                        else if (complexRule != null) {
                            content = complexRule.getEml();
                            if (formats[i].equals("Text")) {
                                meta = Meta_Builder.createTextMeta(user, complexRule.getName(), media[k]);
                            }
                            else {
                                meta = Meta_Builder.createXMLMeta(user, complexRule.getName(), media[k], formats[i]);
                            }
                        }

                        // add meta to EML
                        StringBuffer buffer = new StringBuffer(content);
                        int m = buffer.indexOf("<SimplePatterns>") + 16;
                        content = buffer.insert(m, meta).toString();

                        String museResource;
                        try {
                            // subscribe to SES
                            OperationResult opResult = SesServerUtil.subscribe(SesConfig.serviceVersion,
                                                                               SesConfig.sesEndpoint,
                                                                               SesConfig.consumerReference,
                                                                               content);
                            museResource = SesServerUtil.getSubscriptionIDfromSES(opResult);
                            if ( (museResource == null) || (museResource.equals(""))) {
                                throw new IllegalStateException("Subscribing resource at SES failed.");
                            }
                        }
                        catch (Exception e) {
                            LOGGER.error("Error while subscribing to SES", e);
                            return new SesClientResponse(ERROR_SUBSCRIBE_SES);
                        }

                        // save subscription in DB
                        LOGGER.debug("save subscription to DB: " + museResource);
                        if (basicRule != null) {
                            HibernateUtil.saveSubscription(new Subscription(Integer.valueOf(userID),
                                                                            basicRule.getId(),
                                                                            museResource,
                                                                            media[k],
                                                                            formats[i],
                                                                            true));
                            subscribeBasicRule(uuid);
                        }
                        else if (complexRule != null) {
                            HibernateUtil.saveSubscription(new Subscription(Integer.valueOf(userID),
                                                                            complexRule.getId(),
                                                                            museResource,
                                                                            media[k],
                                                                            formats[i],
                                                                            true));
                            HibernateUtil.updateComplexRuleSubscribtion(uuid, true);
                        }
                    }
                }
            }

            if (basicRule != null) {
                // set sensor status to used
                LOGGER.debug("set sensor to used");
                ArrayList<String> timeseriesIds = SesServerUtil.getTimeseriesIdsFromEML(basicRule.getEml());
                // check if sensor is already in feeder DB. If yes --> no new request to feeder
                try {
                    for (String timeseriesId : timeseriesIds) {
                        TimeseriesFeed timeseriesFeed = getTimeseriesFeedById(timeseriesId);
                        if (timeseriesFeed == null) {
                            TimeseriesMetadata metadata = getTimeseriesMetadata(timeseriesId);
                            LOGGER.debug("Create TimeseriesFeed '{}'. ", timeseriesId);
                            TimeseriesFeed createdTimeseriesFeed = createTimeseriesFeed(metadata);
                            getSosSesFeederInstance().enableFeedingFor(createdTimeseriesFeed);
                        }
                        else if (timeseriesFeed.getUsedCounter() == 0) {
                            LOGGER.debug("Enable inactive TimeseriesFeed '{}'.", timeseriesId);
                            getSosSesFeederInstance().enableFeedingFor(timeseriesFeed);
                        }
                        else {
                            LOGGER.debug("TimeseriesFeed '{}' is already being feeded.", timeseriesId);
                        }
                        activateTimeseriesFeedWith(timeseriesId);
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Error subscribing to feeder.", e);
                    return new SesClientResponse(ERROR_SUBSCRIBE_FEEDER);
                }
            }
            return new SesClientResponse(OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private void activateTimeseriesFeedWith(String timeseriesId) throws Exception {
        try {
            activateTimeseriesFeed(timeseriesId);
        }
        catch (Exception e) {
            LOGGER.error("Could not update database", e);
            throw new Exception("Failed to activate timeseries!");
        }
    }

    private TimeseriesFeed createTimeseriesFeed(TimeseriesMetadata timeseriesMetadata) {
        TimeseriesFeed timeseriesFeed = new TimeseriesFeed(timeseriesMetadata);
        // timeseriesFeed.setLastConsideredTimeInterval(getFeederConfig().getFirstUpdateIntervalRange());
        timeseriesFeed.setLastFeeded(null);
        timeseriesFeed.setUsedCounter(0);
        timeseriesFeed.setSesId(null);
        return timeseriesFeed;
    }

    @Override
    public SesClientResponse unSubscribe(SessionInfo sessionInfo, String uuid, String medium, String eml) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("unsubscribe from rule with UUID: {}", uuid);

            String userID = sessionStore.getLoggedInUserId(sessionInfo);

            // get rule
            BasicRule basicRule = HibernateUtil.getBasicRuleByUuid(uuid);
            ComplexRule complexRule = HibernateUtil.getComplexRuleByName(uuid);

            // rule as EML
            String ruleAsEML = "";

            String museID = "";
            if (basicRule != null) {
                Subscription subscription = getSubscriptionIdByRuleIdAndUserId(basicRule.getId(), Integer.valueOf(userID));
                museID = subscription.getSubscriptionID();
                ruleAsEML = basicRule.getEml();
            }
            else if (complexRule != null) {
                Subscription subscription = getSubscriptionIdByRuleIdAndUserId(complexRule.getId(), Integer.valueOf(userID));
                museID = subscription.getSubscriptionID();
                ruleAsEML = complexRule.getEml();
            }

            try {
                // unsubscribe from SES
                LOGGER.debug("unsubscribe from SES: " + museID);
                SesServerUtil.unSubscribe(SesConfig.serviceVersion, SesConfig.sesEndpoint, museID);
            }
            catch (Exception e) {
                LOGGER.error("Failed to unsubscribe", e);
                return new SesClientResponse(SesClientResponseType.ERROR_UNSUBSCRIBE_SES);
            }

            ArrayList<String> timeseriesFeedIds = getTimeseriesIdsFromEML(ruleAsEML);
            for (String timeseriesId : timeseriesFeedIds) {
                TimeseriesFeed timseriesFeed = getTimeseriesFeedById(timeseriesId);
                getSosSesFeederInstance().decreaseSubscriptionCountFor(timseriesFeed);
                if (getTimeseriesFeedById(timeseriesId).getUsedCounter() == 0) {
                    deactivateTimeseriesFeed(timeseriesId);
                }
            }

            try {
                // TODO couple subscription and rules in DB model
                deactivateSubscription(museID, userID);
                if (basicRule != null) {
                    unsubscribeBasicRule(uuid);
                }
                else if (complexRule != null) {
                    // TODO refactor complex rules
                    updateComplexRuleSubscribtion(uuid, false);
                }
            }
            catch (Exception e) {
                throw new Exception("Failed delete subscription from DB!", e);
            }
            return new SesClientResponse(SesClientResponseType.OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse createBasicRule(SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName) throws Exception {
        try {
            LOGGER.debug("createBasicRule: {} (session {})", rule, sessionInfo);
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            
            rule.setUuid(randomUUID().toString());
            LOGGER.debug("createBasicRule with UUID {} and timeseries {}", rule.getUuid(), rule.getTimeseriesMetadata());
            if (exists(rule) && !edit) {
                LOGGER.debug("Cannot create rule: Rule '{}' already exists!", rule.getTitle());
                return new SesClientResponse();
            }

            BasicRule basicRule = null;
            switch (rule.getRuleType()) {
            case SUM_OVER_TIME:
                basicRule = BasicRule_3_Builder.create_BR_3(rule);
                break;
            case TENDENCY_OVER_COUNT:
                basicRule = BasicRule_1_Builder.create_BR_1(rule);
                break;
            case TENDENCY_OVER_TIME:
                basicRule = BasicRule_2_Builder.create_BR_2(rule);
                break;
            case OVER_UNDERSHOOT:
                BasicRule_4_Builder ruleGenerator = new BasicRule_4_Builder();
                basicRule = ruleGenerator.create(rule);
                break;
            case SENSOR_LOSS:
                basicRule = new BasicRule_5_Builder().create_BR_5(rule);
                break;
            }

            if (basicRule != null) {
                basicRule.setTimeseriesMetadata(rule.getTimeseriesMetadata());

                // user wants to edit the rule
                if (edit) {
                    // update Basic rule
                    LOGGER.debug("update basicRule in DB");
                    BasicRule oldRule = HibernateUtil.getBasicRuleByUuid(oldRuleName);

                    // check if only description and/or publish status is changed ==> no resubscriptions are
                    // needed
                    if (RulesUtil.changesOnlyInDBBasic(oldRule, basicRule)) {
                        // update in DB only
                        // delete old rule
                        HibernateUtil.deleteRule(oldRuleName);
                        // save new
                        HibernateUtil.saveBasicRule(basicRule);

                        return new SesClientResponse(SesClientResponseType.EDIT_SIMPLE_RULE);
                    }

                    // rule is subscribed
                    if (oldRule.isSubscribed()) {
                        List<Subscription> subscriptions = HibernateUtil.getSubscriptionsFromRuleID(oldRule.getId());
                        // delete old rule
                        HibernateUtil.deleteRule(oldRuleName);
                        // save new
                        HibernateUtil.saveBasicRule(basicRule);

                        // iterate over all subscriptions of this rule
                        // unsubscribe old rule and subscribe the edited rule
                        for (int i = 0; i < subscriptions.size(); i++) {
                            Subscription subscription = subscriptions.get(i);
                            try {
                                // unsubscribe from SES
                                LOGGER.debug("unsubscribe from SES: " + subscription.getSubscriptionID());
                                SesServerUtil.unSubscribe(SesConfig.serviceVersion,
                                                          SesConfig.sesEndpoint,
                                                          subscription.getSubscriptionID());
                                subscribe(sessionInfo,
                                          rule.getTitle(),
                                          subscription.getMedium(),
                                          subscription.getFormat());
                            }
                            catch (Exception e) {
                                LOGGER.error("Could not unsubscribe from SES", e);
                            }
                            HibernateUtil.deleteSubscription(subscription.getSubscriptionID(),
                                                             String.valueOf(subscription.getUserID()));
                        }
                    }
                    else {
                        // delete old rule
                        HibernateUtil.deleteRule(oldRuleName);
                        // save new
                        HibernateUtil.saveBasicRule(basicRule);
                    }

                    return new SesClientResponse(SesClientResponseType.EDIT_SIMPLE_RULE);
                }
                LOGGER.debug("save basicRule to DB");
                HibernateUtil.saveBasicRule(basicRule);
            }
            return new SesClientResponse(SesClientResponseType.RULE_NAME_NOT_EXISTS, rule);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    boolean exists(Rule rule) {
        return existsBasicRule(rule.getUuid()) || existsComplexRuleName(rule.getTitle());
    }

    @Override
    public SesClientResponse getAllOwnRules(SessionInfo sessionInfo, boolean edit) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            String id = sessionStore.getLoggedInUserId(sessionInfo);
            LOGGER.debug("getAllOwnRules of user: " + id);

            ArrayList<BasicRuleDTO> finalBasicList = new ArrayList<BasicRuleDTO>();
            ArrayList<ComplexRuleDTO> finalComplexList = new ArrayList<ComplexRuleDTO>();
            List<BasicRule> basicList;
            List<ComplexRule> complexList;

            // get rules from DB
            basicList = HibernateUtil.getAllBasicRulesBy(id);
            complexList = HibernateUtil.getAllComplexRulesBy(id);

            // basic rules
            for (int i = 0; i < basicList.size(); i++) {
                BasicRule rule = basicList.get(i);

                // check if user subscribed this rule
                if (HibernateUtil.isSubscribed(id, rule.getId())) {
                    rule.setSubscribed(true);
                }
                else {
                    rule.setSubscribed(false);
                }

                finalBasicList.add(SesUserServiceImpl.createBasicRuleDTO(rule));
            }

            // complex rules
            for (int i = 0; i < complexList.size(); i++) {
                ComplexRule rule = complexList.get(i);

                // check if user subscribed this rule
                if (HibernateUtil.isSubscribed(id, rule.getId())) {
                    rule.setSubscribed(true);
                }
                else {
                    rule.setSubscribed(false);
                }

                finalComplexList.add(SesUserServiceImpl.createComplexRuleDTO(rule));
            }

            if (edit) {
                return new SesClientResponse(SesClientResponseType.EDIT_OWN_RULES, finalBasicList, finalComplexList);
            }

            return new SesClientResponse(SesClientResponseType.OWN_RULES, finalBasicList, finalComplexList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getAllOtherRules(SessionInfo sessionInfo, boolean edit) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            String id = sessionStore.getLoggedInUserId(sessionInfo);
            LOGGER.debug("get all rules except user: " + id);
            ArrayList<BasicRuleDTO> finalBasicList = new ArrayList<BasicRuleDTO>();
            ArrayList<ComplexRuleDTO> finalComplexList = new ArrayList<ComplexRuleDTO>();
            List<BasicRule> basicList;
            List<ComplexRule> complexList;

            // get rules from DB
            basicList = HibernateUtil.getAllOtherBasicRules(id);
            complexList = HibernateUtil.getAllOtherComplexRules(id);

            // basic rules
            for (int i = 0; i < basicList.size(); i++) {
                BasicRule rule = basicList.get(i);

                // show only published rules
                if (rule.isPublished()) {

                    // check if user subscribed this rule
                    if (HibernateUtil.isSubscribed(id, rule.getId())) {
                        rule.setSubscribed(true);
                    }
                    else {
                        rule.setSubscribed(false);
                    }
                    finalBasicList.add(SesUserServiceImpl.createBasicRuleDTO(rule));
                }
            }

            // complex rules
            for (int i = 0; i < complexList.size(); i++) {
                ComplexRule rule = complexList.get(i);

                // show only published rules
                if (rule.isPublished()) {

                    // check if user subscribed this rule
                    if (HibernateUtil.isSubscribed(id, rule.getId())) {
                        rule.setSubscribed(true);
                    }
                    else {
                        rule.setSubscribed(false);
                    }
                    finalComplexList.add(SesUserServiceImpl.createComplexRuleDTO(rule));
                }
            }

            if (edit) {
                return new SesClientResponse(SesClientResponseType.EDIT_OTHER_RULES, finalBasicList, finalComplexList);
            }

            return new SesClientResponse(SesClientResponseType.OTHER_RULES, finalBasicList, finalComplexList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse publishRule(SessionInfo sessionInfo, String ruleName, boolean published) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("publish rule: " + ruleName + ": " + published);
            if (isLoggedInAdmin(sessionInfo, sessionStore)) {
                return new SesClientResponse(SesClientResponseType.PUBLISH_RULE_ADMIN);
            }
            return new SesClientResponse(SesClientResponseType.PUBLISH_RULE_USER);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
    
    @Override
    public SesClientResponse getAllRules(SessionInfo sessionInfo) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("get all rules");

            BasicRuleDTO basicDTO;
            ComplexRuleDTO complexDTO;

            ArrayList<BasicRuleDTO> finalBasicList = new ArrayList<BasicRuleDTO>();
            ArrayList<ComplexRuleDTO> finalComplexList = new ArrayList<ComplexRuleDTO>();

            List<BasicRule> basicList = HibernateUtil.getAllBasicRules();
            List<ComplexRule> complexList = HibernateUtil.getAllComplexRules();

            for (int i = 0; i < basicList.size(); i++) {
                basicDTO = SesUserServiceImpl.createBasicRuleDTO(basicList.get(i));
                basicDTO.setOwnerName(HibernateUtil.getUserBy(basicDTO.getOwnerID()).getUserName());
                finalBasicList.add(basicDTO);
            }

            for (int i = 0; i < complexList.size(); i++) {
                complexDTO = SesUserServiceImpl.createComplexRuleDTO(complexList.get(i));
                complexDTO.setOwnerName(HibernateUtil.getUserBy(complexDTO.getOwnerID()).getUserName());
                finalComplexList.add(complexDTO);
            }

            return new SesClientResponse(SesClientResponseType.All_RULES, finalBasicList, finalComplexList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse deleteRule(SessionInfo sessionInfo, String uuid) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("delete rule with uuid " + uuid);

            // get rule
            BasicRule basicRule = HibernateUtil.getBasicRuleByUuid(uuid);
            ComplexRule complexRule = HibernateUtil.getComplexRuleByName(uuid);

            if (basicRule != null) {
//                if (HibernateUtil.ruleIsSubscribed(basicRule.getId())) {
                Subscription subscription = getSubscriptionForRule(basicRule);
                if (subscription.isActive()) {
                    return new SesClientResponse(SesClientResponseType.DELETE_RULE_SUBSCRIBED);
                }
            }
            else if (complexRule != null) {
                if (HibernateUtil.ruleIsSubscribed(complexRule.getId())) {
                    return new SesClientResponse(SesClientResponseType.DELETE_RULE_SUBSCRIBED);
                }
            }

            if (HibernateUtil.deleteRule(uuid)) {
                Subscription subscription = getSubscriptionForRule(basicRule);
                deleteSubscription(subscription.getSubscriptionID(), valueOf(subscription.getUserID()));
                return new SesClientResponse(DELETE_RULE_OK);
            }
            else {
                LOGGER.error("Error occured while deleting a rule");
                throw new Exception("Delete rule failed!");
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private Subscription getSubscriptionForRule(BasicRule basicRule) {
        int ruleID = basicRule.getId();
        int userID = basicRule.getOwnerID();
        return getSubscriptionIdByRuleIdAndUserId(ruleID, userID);
    }

    @Override
    public SesClientResponse getRuleForEditing(String ruleName) throws Exception {
        try {
            BasicRule basicRule = HibernateUtil.getBasicRuleByUuid(ruleName);
            ComplexRule complexRule = HibernateUtil.getComplexRuleByName(ruleName);
            Rule rule = null;

            if (basicRule != null) {
                // check the ruletype
                if (basicRule.getType().equals("BR1")) {
                    rule = BasicRule_1_Builder.createRuleBy(basicRule);
                }
                else if (basicRule.getType().equals("BR2")) {
                    rule = BasicRule_2_Builder.getRuleByEML(basicRule);
                }
                else if (basicRule.getType().equals("BR3")) {
                    rule = BasicRule_3_Builder.getRuleByEml(basicRule);
                }
                else if (basicRule.getType().equals("BR4")) {
                    rule = new BasicRule_4_Builder().getRuleByEML(basicRule);
                }
                else if (basicRule.getType().equals("BR5")) {
                    rule = new BasicRule_5_Builder().getRuleByEML(basicRule);
                }
                rule.setTitle(basicRule.getName());
                rule.setDescription(basicRule.getDescription());
                rule.setPublish(basicRule.isPublished());
                return new SesClientResponse(SesClientResponseType.EDIT_SIMPLE_RULE, rule);
            }
            if (complexRule != null) {
                rule = new Rule();
                rule.setTitle(complexRule.getName());
                rule.setDescription(complexRule.getDescription());
                rule.setPublish(complexRule.isPublished());

                // tree representation of a complex rule
                String tree = complexRule.getTree();
                ArrayList<String> treeList = new ArrayList<String>();
                String[] elements = tree.split("_T_");
                for (int i = 0; i < elements.length; i++) {
                    String content = elements[i];

                    // check whether the rule names still exist
                    if ( ( !content.equals(LogicalOperator.AND.toString())
                            && !content.equals(LogicalOperator.OR.toString()) && !content.equals(LogicalOperator.AND_NOT.toString()))) {
                        if ( !HibernateUtil.existsBasicRule(content) && !HibernateUtil.existsComplexRuleName(content)) {
                            content = Constants.SES_OP_SEPARATOR + content;
                        }
                    }
                    treeList.add(content);
                }

                SesClientResponse response = new SesClientResponse(SesClientResponseType.EDIT_COMPLEX_RULE, treeList);
                response.setRule(rule);

                return response;
            }
            return null;
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getAllPublishedRules(SessionInfo sessionInfo, int operator) throws Exception {
        try {
            sessionStore.validateSessionInfo(sessionInfo);
            String userID = sessionStore.getLoggedInUserId(sessionInfo);
            LOGGER.debug("get all published rules");
            ArrayList<String> finalList = new ArrayList<String>();

            List<BasicRule> basicRuleList = new ArrayList<BasicRule>();
            List<ComplexRule> complexRuleList = new ArrayList<ComplexRule>();

            // 1 = own
            // 2 = other
            // 3 = both
            if (operator == 1) {
                basicRuleList.addAll(HibernateUtil.getAllBasicRulesBy(userID));
                complexRuleList.addAll(HibernateUtil.getAllComplexRulesBy(userID));
            }
            else if (operator == 2) {
                basicRuleList.addAll(HibernateUtil.getAllOtherPublishedBasicRules(userID));
                complexRuleList.addAll(HibernateUtil.getAllOtherPublishedComplexRules(userID));
            }
            else if (operator == 3) {
                basicRuleList.addAll(HibernateUtil.getAllBasicRulesBy(userID));
                complexRuleList.addAll(HibernateUtil.getAllComplexRulesBy(userID));
                basicRuleList.addAll(HibernateUtil.getAllPublishedBasicRules());
                complexRuleList.addAll(HibernateUtil.getAllPublishedCcomplexRules());
            }
            // HashSet is used to avoid duplicates
            HashSet<String> h = new HashSet<String>();

            for (int i = 0; i < basicRuleList.size(); i++) {
                h.add(basicRuleList.get(i).getName());
            }

            for (int i = 0; i < complexRuleList.size(); i++) {
                h.add(complexRuleList.get(i).getName());
            }

            finalList.addAll(h);

            return new SesClientResponse(SesClientResponseType.ALL_PUBLISHED_RULES, finalList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse ruleNameExists(String ruleName) throws Exception {
        try {
            LOGGER.debug("check whether rule name '{}' exists.", ruleName);
            if (HibernateUtil.existsBasicRule(ruleName) || HibernateUtil.existsComplexRuleName(ruleName)) {
                return new SesClientResponse(SesClientResponseType.RULE_NAME_EXISTS);
            }
            return new SesClientResponse(SesClientResponseType.RULE_NAME_NOT_EXISTS);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    @Deprecated
    public SesClientResponse createComplexRule(SessionInfo sessionInfo,
                                               ComplexRuleData rule,
                                               boolean edit,
                                               String oldRuleName) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("create complex rule: " + rule.getTitle());

            // rule name exists
            if ( (HibernateUtil.existsComplexRuleName(rule.getTitle()) && !edit)
                    || HibernateUtil.existsBasicRule(rule.getTitle())) {
                return new SesClientResponse(SesClientResponseType.RULE_NAME_EXISTS);
            }

            ArrayList<String> ruleNames = rule.getRuleNames();
            ArrayList<Object> rules = new ArrayList<Object>();

            ComplexRule finalComplexRule = null;
            BasicRule basicRule = null;
            ComplexRule complexRule = null;

            // combine only 2 rules
            if (rule.getRuleNames() != null) {
                // operator
                String operator = rule.getRuleNames().get(0);

                // get all used rules
                for (int i = 1; i < ruleNames.size(); i++) {
                    basicRule = HibernateUtil.getBasicRuleByUuid(ruleNames.get(i));
                    complexRule = HibernateUtil.getComplexRuleByName(ruleNames.get(i));

                    if (basicRule != null) {
                        rules.add(basicRule);
                        if (basicRule.getOwnerID() != rule.getUserID()) {
                            copy(String.valueOf(rule.getUserID()), basicRule.getName());
                        }

                    }
                    if (complexRule != null) {
                        rules.add(complexRule);
                        if (complexRule.getOwnerID() != rule.getUserID()) {
                            copy(String.valueOf(rule.getUserID()), complexRule.getName());
                        }
                    }
                }
                finalComplexRule = ComplexRule_Builder.combine2Rules(operator, rules, rule);
            }
            else {
                // combine 3 or more rules
                finalComplexRule = ComplexRule_BuilderV2.combineRules(rule, rule.getTreeContent());
            }

            if (finalComplexRule != null) {

                // set sensors
                String sensors = "";
                ArrayList<String> timeseriesFeedIdsList = SesServerUtil.getTimeseriesIdsFromEML(finalComplexRule.getEml());

                for (int i = 0; i < timeseriesFeedIdsList.size(); i++) {
                    sensors = sensors + timeseriesFeedIdsList.get(i);
                    sensors = sensors + "&";
                }
                finalComplexRule.setSensor(sensors);

                // set Phenomenona
                String phenomena = "";
                ArrayList<String> phenomenaList = SesServerUtil.getPhenomenaFromEML(finalComplexRule.getEml());

                for (int i = 0; i < phenomenaList.size(); i++) {
                    phenomena = phenomena + phenomenaList.get(i);
                    phenomena = phenomena + "&";
                }
                finalComplexRule.setPhenomenon(phenomena);

                if (edit) {
                    // update Complex rule
                    LOGGER.debug("update complex rule in DB");
                    ComplexRule oldRule = HibernateUtil.getComplexRuleByName(oldRuleName);

                    // check if only description and/or publish status is changed ==> no resubscriptions are
                    // needed
                    if (RulesUtil.changesOnlyInDBComplex(oldRule, finalComplexRule)) {
                        // update in DB only
                        // delete old rule
                        HibernateUtil.deleteRule(oldRuleName);
                        // save new
                        HibernateUtil.addComplexRule(finalComplexRule);

                        return new SesClientResponse(SesClientResponseType.EDIT_COMPLEX_RULE);
                    }

                    if (oldRule.isSubscribed()) {
                        List<Subscription> subscriptions = HibernateUtil.getSubscriptionsFromRuleID(oldRule.getId());
                        // delete old rule
                        HibernateUtil.deleteRule(oldRuleName);
                        // save new
                        HibernateUtil.addComplexRule(finalComplexRule);

                        // resubscribe
                        for (int i = 0; i < subscriptions.size(); i++) {
                            Subscription subscription = subscriptions.get(i);
                            try {
                                // unsubscribe from SES
                                LOGGER.debug("unsubscribe from SES: " + subscription.getSubscriptionID());
                                SesServerUtil.unSubscribe(SesConfig.serviceVersion,
                                                          SesConfig.sesEndpoint,
                                                          subscription.getSubscriptionID());
                                subscribe(sessionInfo,
                                          rule.getTitle(),
                                          subscription.getMedium(),
                                          subscription.getFormat());
                            }
                            catch (Exception e) {
                                LOGGER.error("Error occured while unsubscribing a rule from SES: " + e.getMessage(), e);
                            }
                            deleteSubscription(subscription.getSubscriptionID(),
                                                             String.valueOf(subscription.getUserID()));
                        }
                    }
                    else {
                        // delete old rule
                        HibernateUtil.deleteRule(oldRuleName);
                        // save new
                        HibernateUtil.addComplexRule(finalComplexRule);
                    }
                    return new SesClientResponse(SesClientResponseType.EDIT_COMPLEX_RULE);
                }
                HibernateUtil.addComplexRule(finalComplexRule);
            }

            return new SesClientResponse(SesClientResponseType.OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getUserSubscriptions(SessionInfo sessionInfo) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            String userID = sessionStore.getLoggedInUserId(sessionInfo);
            LOGGER.debug("get all subscriptions of user with id {}.", userID);
            List<Subscription> subscriptions = HibernateUtil.getUserSubscriptions(userID);
            ArrayList<BasicRuleDTO> basicList = new ArrayList<BasicRuleDTO>();
            ArrayList<ComplexRuleDTO> complexList = new ArrayList<ComplexRuleDTO>();

            BasicRule basicRule;
            ComplexRule complexRule;
            for (int i = 0; i < subscriptions.size(); i++) {
                Subscription subscription = subscriptions.get(i);
                basicRule = HibernateUtil.getBasicRuleByID(subscription.getRuleID());

                if (basicRule != null) {
                    basicRule.setMedium(subscription.getMedium());
                    basicRule.setFormat(subscription.getFormat());
                    basicList.add(SesUserServiceImpl.createBasicRuleDTO(basicRule));
                }
                complexRule = HibernateUtil.getComplexRuleByID(subscription.getRuleID());
                if (complexRule != null) {
                    complexRule.setMedium(subscription.getMedium());
                    complexRule.setFormat(subscription.getFormat());
                    complexList.add(SesUserServiceImpl.createComplexRuleDTO(complexRule));
                }
            }

            return new SesClientResponse(USER_SUBSCRIPTIONS, basicList, complexList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse search(String text, int criterion, String userID) throws Exception {
        try {
            LOGGER.debug("search");
            return SearchUtil.search(text, criterion, userID);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse copy(String userID, String uuid) throws Exception {
        try {
            LOGGER.debug("Copy rule to own rules: " + uuid);

            // get the selected rule
            BasicRule basicRule = HibernateUtil.getBasicRuleByUuid(uuid);
            ComplexRule complexRule = HibernateUtil.getComplexRuleByName(uuid);

            User user = HibernateUtil.getUserBy(Integer.valueOf(userID));

            String newRuleName = "";

            if (basicRule != null) {
                newRuleName = basicRule.getName() + "_" + user.getUserName();

                // check if allready exists
                if (HibernateUtil.existsBasicRule(newRuleName)) {
                    return new SesClientResponse(SesClientResponseType.RULE_NAME_EXISTS);
                }

                basicRule.setName(newRuleName);
                basicRule.setOwnerID(Integer.valueOf(userID));
                HibernateUtil.saveCopiedBasicRule(basicRule);
            }

            if (complexRule != null) {
                newRuleName = complexRule.getName() + "_" + user.getUserName();

                // check if allready exists
                if (HibernateUtil.existsComplexRuleName(newRuleName)) {
                    return new SesClientResponse(SesClientResponseType.RULE_NAME_EXISTS);
                }

                complexRule.setName(newRuleName);
                complexRule.setOwnerID(Integer.valueOf(userID));
                HibernateUtil.saveCopiedComplexRule(complexRule);
            }
            return new SesClientResponse(SesClientResponseType.OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    public ServerSessionStore getSessionStore() {
        return sessionStore;
    }

    public void setSessionStore(ServerSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }
    
}
