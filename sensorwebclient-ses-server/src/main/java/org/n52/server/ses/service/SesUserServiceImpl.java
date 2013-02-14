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
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SesUserServiceImpl implements SesUserService {

    private static final Logger LOG = LoggerFactory.getLogger(SesUserServiceImpl.class);

    // list with all deleted user accounts since last login of the admin.
    // This list is shown to the admin after the login
    private static ArrayList<String> deletedUser = new ArrayList<String>();

    public synchronized static UserDTO createUserDTO(User user) {

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
                           user.getHandyNr(),
                           user.getRegisterID(),
                           user.getRole(),
                           user.getActivated(),
                           user.getWnsSmsId(),
                           user.getWnsEmailId(),
                           basicRuleDTOs,
                           complexRuleDTOs,
                           user.getDate());
    }

    /**
     * Creates the complex rule dto.
     * 
     * @param complexRule
     * @return {@link ComplexRuleDTO}
     */
    public synchronized static ComplexRuleDTO createComplexRuleDTO(ComplexRule complexRule) {
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

    /**
     * Creates the basic rule dto.
     * 
     * @param basicRule
     *        the basic rule
     * @return the basic rule dto
     */
    public synchronized static BasicRuleDTO createBasicRuleDTO(BasicRule basicRule) {
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
    public synchronized static void deleteUnregisteredUser() {
        // XXX refactor
        LOG.debug("delete all unregistered user");
        LOG.debug("Timeinterval in milliseconds: " + SesConfig.deleteUserInterval);
        List<User> users = HibernateUtil.deleteUnregisteredUser();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);

            Date date = new Date(user.getDate().toGMTString());
            Date currentDate = new Date(new Date().toGMTString());
            long difference = currentDate.getTime() - date.getTime();
            if (difference >= SesConfig.deleteUserInterval) {
                LOG.debug("user " + user.getName() + " has not verrified his registration since " + difference
                        / (1000 * 60 * 60) + " hours!");
                // delete user
                HibernateUtil.deleteUserBy(user.getId());

                // add deleted user to list
                deletedUser.add(user.getUserName());
            }
            LOG.debug("Difference = " + difference / (1000 * 60 * 60) + " hours");
            LOG.debug("Difference = " + difference / (1000 * 60) + " minutes");
        }
    }

    @Override
    public SesClientResponse registerUser(UserDTO userDTO) throws Exception {
        try {
            // check whether the user name or email address or (if available) the mobile nr
            // to avoid multiple accounts with the same data
            User user = new User(userDTO);
            if (HibernateUtil.existsUserName(user.getUserName())) {
                return new SesClientResponse(SesClientResponse.types.REGISTER_NAME);
            }
            if (HibernateUtil.existsEMail(user.geteMail())) {
                return new SesClientResponse(SesClientResponse.types.REGSITER_EMAIL);
            }

            // generate new userID
            String userRegisterID = UUID.randomUUID().toString();
            user.setRegisterID(userRegisterID);
            user.setActive(true);

            // add user to DB
            HibernateUtil.saveUser(user);
            UserDTO resultUser = createUserDTO(user);

            // send registration mail
            MailSender.sendRegisterMail(resultUser.geteMail(), resultUser.getRegisterID(), resultUser.getUserName());
            return new SesClientResponse(SesClientResponse.types.REGISTER_OK, resultUser);
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse login(String userName, String password, boolean isAdminLogin) throws Exception {
        try {
            LOG.debug("login user '{}'.", userName);
            // get user from DB
            User u = HibernateUtil.findUserBy(userName);
            UserDTO user = createUserDTO(u);
            if (user == null) {
                return new SesClientResponse(SesClientResponse.types.LOGIN_NAME);
            }
            // check entered password
            if (user.getPassword() != null) {
                // wrong password
                if ( !user.getPassword().equals(password)) {
                    LOG.debug("wrong password");
                    int count = u.getFalseLoginCount();
                    if (count < 3) {
                        LOG.debug("increase falseLoginCount");
                        // increment the count of false logins
                        u.setFalseLoginCount(count + 1);
                        HibernateUtil.updateUser(u);
                        return new SesClientResponse(SesClientResponse.types.LOGIN_PASSWORD);
                    }
                    LOG.debug("lock account");
                    // lock the account after entering the wrong password three times in sequence
                    u.setActive(false);
                    HibernateUtil.updateUser(u);
                    return new SesClientResponse(SesClientResponse.types.LOGIN_LOCKED);
                }
            }
            else if ( !password.equals("")) {
                return new SesClientResponse(SesClientResponse.types.LOGIN_PASSWORD);
            }

            // user account is not activated
            if ( !user.getActivated()) {
                return new SesClientResponse(SesClientResponse.types.LOGIN_ACTIVATED);
            }

            // clear password from user
            user.setPassword("");

            if (u.isActive()) {
                user.setEmailVerified(u.isEmailVerified());
                user.setPasswordChanged(u.isPasswordChanged());
                // set
                u.setFalseLoginCount(0);
                HibernateUtil.updateUser(u);

                // admin login
                if (isAdminLogin && !u.getRole().equals(UserRole.ADMIN)) {
					return new SesClientResponse(SesClientResponse.types.LOGIN_AS_USER_IN_ADMIN_INTERFACE);
				} else if (u.getRole().equals(UserRole.ADMIN)) {
                    // show the admin all deleted user since last login
                    ArrayList<String> temp = deletedUser;
                    deletedUser.clear();
                    return new SesClientResponse(SesClientResponse.types.LOGIN_OK, user, temp);
                }
                // user login OK
                return new SesClientResponse(SesClientResponse.types.LOGIN_OK, user);

            }
            return new SesClientResponse(SesClientResponse.types.LOGIN_LOCKED);
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse newPassword(String userName, String email) throws Exception {
        try {
            User user = HibernateUtil.findUserBy(userName);

            // no user founf or the email address is not valid
            if (user == null || !user.geteMail().equals(email)) {
                return new SesClientResponse(SesClientResponse.types.NEW_PASSWORD_ERROR);
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

            return new SesClientResponse(SesClientResponse.types.NEW_PASSWORD_OK);
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public void logout() throws Exception {
        try {
            // nothing to do for now
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public UserDTO getUser(String id) throws Exception {
        try {
            LOG.debug("get user with parameterId=" + id);
            int userID = Integer.valueOf(id);
            return createUserDTO(HibernateUtil.getUserBy(userID));
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse deleteUser(String id) throws Exception {
        try {
            LOG.debug("delete user");
            int userID = Integer.valueOf(id);
            User user = HibernateUtil.getUserBy(userID);

            // avoid deletion of the last admin
            if (user.getRole().equals(UserRole.ADMIN) && !HibernateUtil.otherAdminsExist(userID)) {
                return new SesClientResponse(SesClientResponse.types.LAST_ADMIN);
            }

            // delete all subscriptions from user
            try {
                List<Subscription> subscriptions = HibernateUtil.getSubscriptionfromUserID(userID);

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

            // delete all not published rules
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

            // unsubscribe in WNS
            try {
                if (!LOG.isDebugEnabled()) {
                    WnsUtil.sendToWNSUnregister(user.getWnsEmailId());

                    if (user.getWnsSmsId() != null && !user.getWnsSmsId().equals("")) {
                        WnsUtil.sendToWNSUnregister(user.getWnsSmsId());
                    }
                }

            }
            catch (Exception e) {
                throw new Exception("Delete user failed: Unsubscribe user from WNS failed!", e);
            }

            // delete user from DB
            if ( !HibernateUtil.deleteUserBy(userID)) {
                LOG.error("Delete user failed: Unsubscribe user from data base failed!");
                throw new Exception("Delete user failed: Unsubscribe user from data base failed!");
            }

            return new SesClientResponse();
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse updateUser(UserDTO newUser, String userID) throws Exception {
        try {
            boolean mailChanged = false;
            boolean passwordChanged = false;
            String newHandy = null;

            User oldUser = HibernateUtil.getUserBy(newUser.getId());
            newUser.setWnsEmailId(oldUser.getWnsEmailId());
            newUser.setWnsSmsId(oldUser.getWnsSmsId());
            newUser.setRegisterID(oldUser.getRegisterID());
            newUser.setEmailVerified(oldUser.isEmailVerified());

            if (newUser.getPassword() != null) {
                // check password
                if ( !oldUser.getPassword().equals(newUser.getPassword())) {
                    return new SesClientResponse(SesClientResponse.types.ERROR);
                }
                if (newUser.getNewPassword() != null) {
                    newUser.setPassword(newUser.getNewPassword());
                }
            }
            else {
                // password has no changes
                newUser.setPassword(oldUser.getPassword());
            }

            // String oldPassword = oldUser.getPassword();
            // if (oldPassword != null) {
            // if (!oldUser.getPassword().equals(newUser.getPassword())) {
            // passwordChanged = true;
            // if (oldUser.isPasswordChanged()) {
            // passwordChanged = false;
            // }
            // }
            // } else {
            // passwordChanged = true;
            // }

            // check Mail
            if ( !oldUser.geteMail().equals(newUser.geteMail())) {
                // mail address exsists
                if (HibernateUtil.existsEMail(newUser.geteMail())) {
                    return new SesClientResponse(SesClientResponse.types.REGSITER_EMAIL);
                }
                // new address --> send validation mail
                MailSender.sendEmailValidationMail(newUser.geteMail(), oldUser.getRegisterID());
                newUser.setEmailVerified(false);

                mailChanged = true;

                // update email address in WNS
                if (!LOG.isDebugEnabled()) {
                    WnsUtil.updateToWNSMail(oldUser.getWnsEmailId(), newUser.geteMail(), oldUser.geteMail());
                    LOG.info("Update eMail of user " + oldUser.getName() + " in WNS");
                }
            }
            // check user name
            if ( !oldUser.getUserName().equals(newUser.getUserName())) {
                // user name exists
                if (HibernateUtil.existsUserName(newUser.getUserName())) {
                    return new SesClientResponse(SesClientResponse.types.REGISTER_NAME);
                }
            }

            // check role
            // check if other admins exist to avoid deleting the last admin
            if (oldUser.getRole().equals(UserRole.ADMIN)) {
                if (newUser.getRole().equals(UserRole.USER)) {
                    if ( !HibernateUtil.otherAdminsExist(newUser.getId())) {
                        LOG.debug("Last admin, admin is not allowed to change his role from admin to user");
                        return new SesClientResponse(SesClientResponse.types.LAST_ADMIN);
                    }
                    else if (oldUser.getId() == Integer.valueOf(userID)) {
                        LOG.debug("set admin to user and update user data in database");
                        User u = new User(newUser);
                        u.setEmailVerified(newUser.isEmailVerified());
                        u.setActive(oldUser.isActive());

                        HibernateUtil.updateUser(u);
                        return new SesClientResponse(SesClientResponse.types.LOGOUT);
                    }
                }
            }

            // set data
            LOG.debug("update user data in database");
            User u = new User(newUser);
            u.setEmailVerified(newUser.isEmailVerified());
            u.setActive(oldUser.isActive());
            u.setPasswordChanged(passwordChanged);

            if (newHandy != null) {
                u.setHandyNr(newHandy);
            }

            // update user data in DB
            HibernateUtil.updateUser(u);

            if (mailChanged) {
                return new SesClientResponse(SesClientResponse.types.MAIL);
            }
            return new SesClientResponse(SesClientResponse.types.OK);
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public List<UserDTO> getAllUsers() throws Exception {
        try {
            LOG.debug("getAllUsers");
            List<UserDTO> finalList = new ArrayList<UserDTO>();

            List<User> list = HibernateUtil.getAllUsers();
            for (int i = 0; i < list.size(); i++) {
                finalList.add(createUserDTO(list.get(i)));
            }
            return finalList;
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse deleteProfile(String id) throws Exception {
        try {
            LOG.debug("delete profile");
            LOG.debug("set user status to false to avoid logins");
            // get user from DB
            User user = HibernateUtil.getUserBy(Integer.valueOf(id));
            HibernateUtil.updateUserStatus(Integer.valueOf(id), false);

            // send mail to user
            MailSender.sendDeleteProfileMail(user.geteMail(), user.getRegisterID());

            return new SesClientResponse();
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getTermsOfUse(String language) throws Exception {
        try {
            LOG.debug("get terms of use");
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

            return new SesClientResponse(SesClientResponse.types.TERMS_OF_USE, contents.toString());
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

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

            return new SesClientResponse(SesClientResponse.types.DATA, dataList);
        }
        catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
