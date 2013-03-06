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

package org.n52.server.ses.service;

import static java.lang.Integer.parseInt;
import static org.n52.server.ses.hibernate.HibernateUtil.getSubscriptionfromUserID;
import static org.n52.shared.responses.SesClientResponseType.LAST_ADMIN;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_ACTIVATED;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_ADMIN;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_LOCKED;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_NAME;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_OK;
import static org.n52.shared.responses.SesClientResponseType.LOGIN_PASSWORD;
import static org.n52.shared.responses.SesClientResponseType.LOGOUT;
import static org.n52.shared.responses.SesClientResponseType.NEW_PASSWORD_ERROR;
import static org.n52.shared.responses.SesClientResponseType.NEW_PASSWORD_OK;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_NAME;
import static org.n52.shared.responses.SesClientResponseType.REGISTER_OK;
import static org.n52.shared.responses.SesClientResponseType.REGSITER_EMAIL;
import static org.n52.shared.responses.SesClientResponseType.REQUIRES_LOGIN;
import static org.n52.shared.responses.SesClientResponseType.USER_INFO;
import static org.n52.shared.serializable.pojos.UserRole.ADMIN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.n52.client.service.SesUserService;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.mail.MailSender;
import org.n52.server.ses.util.SesServerUtil;
import org.n52.server.ses.util.WnsUtil;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.responses.SesClientResponseType;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SesUserServiceImpl implements SesUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SesUserServiceImpl.class);

    // list with all deleted user accounts since last login of the admin.
    // This list is shown to the admin after the login
    private static ArrayList<String> deletedUser = new ArrayList<String>();

    private ServerSessionStore sessionStore; // injected
    
    public static UserDTO createUserDTO(User user) {

        // XXX refactor

        if (user == null)
            return null;
        // Basic Rule
        Set<BasicRule> basicRules = user.getBasicRules();
        Set<BasicRuleDTO> basicRuleDTOs = new HashSet<BasicRuleDTO>(basicRules != null ? basicRules.size() : 0);
        if (basicRules != null) {
            for (BasicRule basicRule : basicRules) {
                basicRuleDTOs.add(createBasicRuleDTO(basicRule));
            }
        }
        // Complex Rule
        Set<ComplexRule> complexRules = user.getComplexRules();
        Set<ComplexRuleDTO> complexRuleDTOs = new HashSet<ComplexRuleDTO>(complexRules != null ? complexRules.size()
                                                                                              : 0);
        if (complexRules != null) {
            for (ComplexRule complexRule : complexRules) {
                complexRuleDTOs.add(createComplexRuleDTO(complexRule));
            }
        }
        return new UserDTO(user.getId(),
                           user.getUserName(),
                           user.getName(),
                           user.getPassword(),
                           user.geteMail(),
                           user.getRegisterID(),
                           user.getRole(),
                           user.getActivated(),
                           user.getWnsSmsId(),
                           user.getWnsEmailId(),
                           basicRuleDTOs,
                           complexRuleDTOs,
                           user.getDate());
    }

    public static ComplexRuleDTO createComplexRuleDTO(ComplexRule complexRule) {
        // XXX refactor
        return new ComplexRuleDTO(complexRule.getId(),
                                  complexRule.getName(),
                                  complexRule.getRuleType(),
                                  complexRule.getDescription(),
                                  complexRule.isPublished(),
                                  complexRule.getOwnerID(),
                                  complexRule.getEml(),
                                  complexRule.isSubscribed(),
                                  complexRule.getMedium(),
                                  complexRule.getFormat(),
                                  complexRule.getTree(),
                                  complexRule.getSensor(),
                                  complexRule.getPhenomenon());
    }

    public static BasicRuleDTO createBasicRuleDTO(BasicRule basicRule) {
        // XXX refactor
        return new BasicRuleDTO(basicRule.getId(),
                                basicRule.getName(),
                                basicRule.getRuleType(),
                                basicRule.getType(),
                                basicRule.getDescription(),
                                basicRule.isPublished(),
                                basicRule.getOwnerID(),
                                basicRule.getEml(),
                                basicRule.isSubscribed(),
                                basicRule.getMedium(),
                                basicRule.getFormat(),
                                basicRule.getUuid(),
                                basicRule.getTimeseriesMetadata());
    }

    /**
     * delete all users who do not confirm the registration within a time span (e.g 24h)
     */
    public static void deleteUnregisteredUser() {
        // XXX refactor
        LOGGER.debug("delete all unregistered user");
        LOGGER.debug("Timeinterval in milliseconds: " + SesConfig.deleteUserInterval);
        List<User> users = HibernateUtil.deleteUnregisteredUser();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            Date date = new Date(user.getDate().toGMTString());
            Date currentDate = new Date(new Date().toGMTString());
            long difference = currentDate.getTime() - date.getTime();
            if (difference >= SesConfig.deleteUserInterval) {
                LOGGER.debug("user " + user.getName() + " has not verrified his registration since " + difference
                        / (1000 * 60 * 60) + " hours!");
                // delete user
                HibernateUtil.deleteUserBy(user.getId());

                // add deleted user to list
                deletedUser.add(user.getUserName());
            }
            LOGGER.debug("Difference = " + difference / (1000 * 60 * 60) + " hours");
            LOGGER.debug("Difference = " + difference / (1000 * 60) + " minutes");
        }
    }

    @Override
    public SesClientResponse registerUser(UserDTO userDTO) throws Exception {
        try {
            LOGGER.debug("registerUser: ", userDTO);
            // check whether the user name or email address or (if available) the mobile nr
            // to avoid multiple accounts with the same data
            User user = new User(userDTO);
            if (HibernateUtil.existsUserName(user.getUserName())) {
                return new SesClientResponse(REGISTER_NAME);
            }
            if (HibernateUtil.existsEMail(user.geteMail())) {
                return new SesClientResponse(REGSITER_EMAIL);
            }

            // generate new userID
            String userRegisterID = UUID.randomUUID().toString();
            user.setRegisterID(userRegisterID);
            user.setActive(true);

            // add user to DB
            HibernateUtil.save(user);
            UserDTO resultUser = createUserDTO(user);

            // send registration mail
            MailSender.sendRegisterMail(resultUser.geteMail(), resultUser.getRegisterID(), resultUser.getUserName());
            return new SesClientResponse(REGISTER_OK, resultUser);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse login(String userName, String password, SessionInfo unboundSession) throws Exception {
        try {
            LOGGER.debug("login '{}' (session {}).", userName, unboundSession);
            if ( !HibernateUtil.existsUserName(userName)) {
                LOGGER.info("User unknown.");
                return new SesClientResponse(LOGIN_NAME);
            }
            User user = HibernateUtil.findUserBy(userName);
            UserDTO userTransferObject = createUserDTO(user);
            // check entered password
            if (userTransferObject.getPassword() != null) {
                // wrong password
                if ( !userTransferObject.getPassword().equals(password)) {
                    LOGGER.debug("wrong password");
                    int count = user.getFalseLoginCount();
                    if (count < 3) {
                        LOGGER.debug("increase falseLoginCount");
                        // increment the count of false logins
                        user.setFalseLoginCount(count + 1);
                        HibernateUtil.updateUser(user);
                        return new SesClientResponse(LOGIN_PASSWORD);
                    }
                    LOGGER.debug("lock account");
                    // lock the account after entering the wrong password three times in sequence
                    user.setActive(false);
                    HibernateUtil.updateUser(user);
                    return new SesClientResponse(LOGIN_LOCKED);
                }
            }
            else if ( !password.equals("")) {
                return new SesClientResponse(LOGIN_PASSWORD);
            }

            // user account is not activated
            if ( !userTransferObject.getActivated()) {
                return new SesClientResponse(LOGIN_ACTIVATED);
            }

            if (user.isActive()) {
                userTransferObject.setEmailVerified(user.isEmailVerified());
                userTransferObject.setPasswordChanged(user.isPasswordChanged());
                // set
                user.setFalseLoginCount(0);
                HibernateUtil.updateUser(user);

                // clear password from user
                userTransferObject.setPassword("");
                
                SesClientResponse response = new SesClientResponse(LOGIN_OK, userTransferObject);
                response.setSessionInfo(sessionStore.createLoginSessionFor(user, unboundSession));
                return response;

            }
            return new SesClientResponse(LOGIN_LOCKED);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
    
    public SesClientResponse validateLoginSession(SessionInfo sessionInfo) throws Exception {
        try {
            if (sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                if (sessionStore.isLoggedInAdmin(sessionInfo)) {
                    UserDTO transferUser = getUserFromValidSession(sessionInfo);
                    SesClientResponse response = new SesClientResponse(LOGIN_ADMIN, transferUser);
                    response.setSessionInfo(sessionStore.reNewSession(sessionInfo));
                    return response;
                } else {
                    UserDTO transferUser = getUserFromValidSession(sessionInfo);
                    SesClientResponse response = new SesClientResponse(LOGIN_OK, transferUser);
                    response.setSessionInfo(sessionStore.reNewSession(sessionInfo));
                    return response;
                }
            } else {
                return new SesClientResponse(LOGOUT);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    private UserDTO getUserFromValidSession(SessionInfo sessionInfo) {
        String userId = sessionStore.getLoggedInUserId(sessionInfo);
        User user = HibernateUtil.getUserBy(Integer.valueOf(userId));
        return createUserDTO(user);
    }
    
    @Override
    public SessionInfo createNotLoggedInSession() throws Exception {
        try {
            return sessionStore.createNotLoggedInSession();
        } catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse resetPassword(String userName, String email) throws Exception {
        try {
            User user = HibernateUtil.findUserBy(userName);

            // no user founf or the email address is not valid
            if (user == null || !user.geteMail().equals(email)) {
                return new SesClientResponse(NEW_PASSWORD_ERROR);
            }
            // generate new password
            String newPassword = Long.toHexString(Double.doubleToLongBits(Math.random()));
            String md5Password = SesServerUtil.createMD5(newPassword);
            user.setPassword(md5Password);
            user.setPasswordChanged(true);

            // update user in DB
            HibernateUtil.updateUser(user);

            // send mail with new password
            MailSender.sendPasswordMail(email, newPassword);

            return new SesClientResponse(NEW_PASSWORD_OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public void logout(SessionInfo sessionInfo) throws Exception {
        try {
            sessionStore.invalidateLoggedInSession(sessionInfo);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getUser(SessionInfo sessionInfo) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("Get user with id '{}'", sessionStore.getLoggedInUserId(sessionInfo));
            int userID = Integer.valueOf(sessionStore.getLoggedInUserId(sessionInfo));
            UserDTO user = createUserDTO(HibernateUtil.getUserBy(userID));
            return new SesClientResponse(USER_INFO, user);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse deleteUser(SessionInfo sessionInfo, String id) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("delete user with id '{}'", id);
            if (HibernateUtil.getUserBy(parseInt(id)) != null) {
                return performUserDelete(id);
            } else {
                LOGGER.info("User could not deleted as id '{}' is not known", id);
                return new SesClientResponse();
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }
    
    /**
     * Performs a user delete. Performing the deletion assumes all pre-requirements are met so that it is ok
     * to delete the user directly from the system.
     * 
     * @param userId
     *        the id of the user to delete.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     * 
     * @see #requestToDeleteProfile(SessionInfo, String)
     */
    public SesClientResponse performUserDelete(String userId) throws Exception {
        int userID = Integer.valueOf(userId);
        User user = HibernateUtil.getUserBy(userID);
        if (isLastAdministator(user)) {
            // avoid deletion of last admin
            return new SesClientResponse(LAST_ADMIN);
        }
        deleteUserSubscriptions(userID);
        deleteAllUserRules(userId);
        unsubscribeUserFromWns(user);
        deleteUserFromDatabase(userID);
        return new SesClientResponse();
    }

    private boolean isLastAdministator(User user) {
        return user.getRole().equals(UserRole.ADMIN) && !HibernateUtil.otherAdminsExist(user.getId());
    }

    private void deleteUserSubscriptions(int userID) throws Exception {
        // delete all subscriptions from user
        try {
            List<Subscription> subscriptions = getSubscriptionfromUserID(userID);

            for (int i = 0; i < subscriptions.size(); i++) {
                String subscriptionID = subscriptions.get(i).getSubscriptionID();

                // delete from DB
                HibernateUtil.deleteSubscription(subscriptionID, String.valueOf(userID));
                // delete from SES
                SesServerUtil.unSubscribe(SesConfig.serviceVersion, SesConfig.sesEndpoint, subscriptionID);
            }
        }
        catch (Exception e) {
            throw new Exception("Delete user failed: Delete users subscriptions failed!", e);
        }
    }

    private void deleteAllUserRules(String id) {
        List<BasicRule> basicList = HibernateUtil.getAllBasicRulesBy(id);
        List<ComplexRule> complexList = HibernateUtil.getAllComplexRulesBy(id);

        // delete all basic rules
        for (int i = 0; i < basicList.size(); i++) {
            BasicRule rule = basicList.get(i);
            if ( !rule.isPublished()) {
                HibernateUtil.deleteRule(rule.getName());
            }
        }

        // dele all complex rules
        for (int i = 0; i < complexList.size(); i++) {
            ComplexRule rule = complexList.get(i);
            if ( !rule.isPublished()) {
                HibernateUtil.deleteRule(rule.getName());
            }
        }
    }
    
    private  void unsubscribeUserFromWns(User user) throws Exception {
        try {
            if ( !LOGGER.isDebugEnabled()) {
                WnsUtil.sendToWNSUnregister(user.getWnsEmailId());
                if (user.getWnsSmsId() != null && !user.getWnsSmsId().equals("")) {
                    WnsUtil.sendToWNSUnregister(user.getWnsSmsId());
                }
            }
        }
        catch (Exception e) {
            throw new Exception("Delete user failed: Unsubscribe user from WNS failed!", e);
        }
    }

    private void deleteUserFromDatabase(int userID) throws Exception {
        if ( !HibernateUtil.deleteUserBy(userID)) {
            LOGGER.error("Delete user failed: Unsubscribe user from data base failed!");
            throw new Exception("Delete user failed: Unsubscribe user from data base failed!");
        }
    }


    @Override
    public SesClientResponse updateUser(SessionInfo sessionInfo, UserDTO newUser) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            LOGGER.debug("update user with id '{}'", newUser.getId());
            
            boolean mailChanged = false;
            boolean passwordChanged = false;

            User oldUser = HibernateUtil.getUserBy(newUser.getId());
            newUser.setWnsEmailId(oldUser.getWnsEmailId());
            newUser.setWnsSmsId(oldUser.getWnsSmsId());
            newUser.setRegisterID(oldUser.getRegisterID());
            newUser.setEmailVerified(oldUser.isEmailVerified());

            if (newUser.getPassword() != null) {
                // check password
                if ( !oldUser.getPassword().equals(newUser.getPassword())) {
                    return new SesClientResponse(SesClientResponseType.ERROR);
                }
                if (newUser.getNewPassword() != null) {
                    newUser.setPassword(newUser.getNewPassword());
                }
            }
            else {
                // password has no changes
                newUser.setPassword(oldUser.getPassword());
            }

            // check Mail
            if ( !oldUser.geteMail().equals(newUser.geteMail())) {
                // mail address exsists
                if (HibernateUtil.existsEMail(newUser.geteMail())) {
                    return new SesClientResponse(SesClientResponseType.REGSITER_EMAIL);
                }
                // new address --> send validation mail
                MailSender.sendEmailValidationMail(newUser.geteMail(), oldUser.getRegisterID());
                newUser.setEmailVerified(false);

                mailChanged = true;

                // update email address in WNS
                if ( !LOGGER.isDebugEnabled()) {
                    WnsUtil.updateToWNSMail(oldUser.getWnsEmailId(), newUser.geteMail(), oldUser.geteMail());
                    LOGGER.info("Update eMail of user " + oldUser.getName() + " in WNS");
                }
            }
            // check user name
            if ( !oldUser.getUserName().equals(newUser.getUserName())) {
                // user name exists
                if (HibernateUtil.existsUserName(newUser.getUserName())) {
                    return new SesClientResponse(SesClientResponseType.REGISTER_NAME);
                }
            }

            // check role
            // check if other admins exist to avoid deleting the last admin
            if (oldUser.getRole().equals(UserRole.ADMIN)) {
                if (newUser.getRole().equals(UserRole.USER)) {
                    if ( !HibernateUtil.otherAdminsExist(newUser.getId())) {
                        LOGGER.warn("Deleting user with admin role aborted: At least one admin has to exist!");
                        return new SesClientResponse(LAST_ADMIN);
                    }
                    else if (oldUser.getId() == Integer.valueOf(sessionStore.getLoggedInUserId(sessionInfo))) {
                        LOGGER.debug("set admin to user and update user data in database");
                        User u = new User(newUser);
                        u.setEmailVerified(newUser.isEmailVerified());
                        u.setActive(oldUser.isActive());

                        HibernateUtil.updateUser(u);
                        return new SesClientResponse(LOGOUT);
                    }
                }
            }

            // set data
            LOGGER.debug("update user data in database");
            User u = new User(newUser);
            u.setEmailVerified(newUser.isEmailVerified());
            u.setActive(oldUser.isActive());
            u.setPasswordChanged(passwordChanged);

            // update user data in DB
            HibernateUtil.updateUser(u);

            if (mailChanged) {
                return new SesClientResponse(SesClientResponseType.MAIL);
            }
            return new SesClientResponse(SesClientResponseType.OK);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public List<UserDTO> getAllUsers(SessionInfo sessionInfo) throws Exception {
        try {
            LOGGER.debug("getAllUsers");
            sessionStore.validateSessionInfo(sessionInfo);
            List<UserDTO> finalList = new ArrayList<UserDTO>();

            List<User> list = HibernateUtil.getAllUsers();
            for (int i = 0; i < list.size(); i++) {
                finalList.add(createUserDTO(list.get(i)));
            }
            return finalList;
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse requestToDeleteProfile(SessionInfo sessionInfo) throws Exception {
        try {
            if ( !sessionStore.isKnownActiveSessionInfo(sessionInfo)) {
                return new SesClientResponse(REQUIRES_LOGIN);
            }
            sessionStore.validateSessionInfo(sessionInfo);
            LOGGER.debug("prepare user delete with id '{}'", sessionStore.getLoggedInUserId(sessionInfo));
            String id = sessionStore.getLoggedInUserId(sessionInfo);
            User user = HibernateUtil.getUserBy(Integer.valueOf(id));
            
            LOGGER.debug("prevent user from further logins");
            HibernateUtil.updateUserStatus(Integer.valueOf(id), false);

            LOGGER.debug("send confirmation mail to {}", user.geteMail());
            MailSender.sendDeleteProfileMail(user.geteMail(), user.getRegisterID());
            return new SesClientResponse();
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getTermsOfUse(String language) throws Exception {
        try {
            LOGGER.debug("get terms of use");
            // terms of use from file
            String termsOfUsePath = "";

            if (language.equals("en")) {
                termsOfUsePath = SesConfig.path + "/properties/termsOfUse_en.txt";
            }
            else if (language.equals("de")) {
                termsOfUsePath = SesConfig.path + "/properties/termsOfUse_de.txt";
            }

            File file = new File(termsOfUsePath);
            StringBuffer contents = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String text = null;
            while ( (text = reader.readLine()) != null) {
                contents.append(text);
            }
            reader.close();

            return new SesClientResponse(SesClientResponseType.TERMS_OF_USE, contents.toString());
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Deprecated
    @Override
    public SesClientResponse getData() throws Exception {
        try {
            // build path of the webapp root
            URL url = new URL(SesConfig.URL);
            String path = url.getPath();

            String webAppName = path.substring(1, path.length());
            webAppName = webAppName.substring(0, webAppName.indexOf("/"));

            // http://host:port/webAppName
            String finalPath = url.toString().replace(url.getPath(), "/" + webAppName);

            // fill arrayList with data
            ArrayList<Object> dataList = new ArrayList<Object>();
            dataList.add(finalPath);
            dataList.add(SesConfig.warnUserLongNotification);
            dataList.add(SesConfig.minimumPasswordLength);
            dataList.add(SesConfig.availableWNSmedia);
            dataList.add(SesConfig.defaultMedium);
            dataList.add(SesConfig.availableFormats);
            dataList.add(SesConfig.defaultFormat);

            return new SesClientResponse(SesClientResponseType.DATA, dataList);
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
