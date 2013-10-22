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
package org.n52.client.ses.i18n;

import com.google.gwt.i18n.client.Constants;

public interface I18N extends Constants {
    
    String userName();

    String password();

    String userLogin();

    String registration();

    String name();

    String passwordAgain();

    String email();

    String emailAgain();

    String handy();

    String termsOfUse();

    String termsOfUseUrl();
    
    String acceptTermsOfUse();

    String register();

    String invalidEmail();

    String emailDoNotMatch();

    String passwordDoNotMatch();

    String forgotPassword();

    String sendEmail();

    String showUser();

    String emailAddress();

    String handyNumber();

    String role();

    String edit();

    String delete();

    String createNewUser();

    String editUserData();

    String deleteUserData();

    String reallyDeleteUser();

    String showSensors();

    String inUse();

    String save();

    String saveChanges();

    String active();

    String inactive();

    String deleteSensor();

    String reallyDeleteSensor();

    String createBasicRule();

    String title();

    String description();

    String publish();

    String enterCondition();
    
    String exitCondition();
    
    String enterExitCondition();

    String create();

    String unitsLength();

    String unitsTime();

    String possibleChars();

    String yes();

    String no();

    String chooseStation();

    String choosePhenomenon();

    String chooseRuleType();

    String indicateTimeUnit();

    String indicateValueUnit();

    String indicateConditionTimeUnit();

    String indicateConditionValueUnit();

    String indicateConditionCount();

    String indicateTime();

    String trendOverTime();

    String trendOverCount();
    
    String overshoot();

    String undershoot();
    
    String overUnderShoot();

    String sumOverCountMeasurements();

    String sensorFailure();

    String value();

    String countOfMeasurements();

    String operator();

    String timeValue();

    String editBasicRule();

    String createComplexRule();

    String addBlock();

    String addSingleRule();

    String reset();

    String allRules();

    String type();

    String editThisRule();

    String deleteThisRule();

    String subscribeRules();

    String ownRules();

    String otherRules();

    String medium();

    String subscribe();

    String unsubscribe();

    String sms();

    String subscribeThisRule();

    String unsubscribeThisRule();

    String subscriptions();

    String editProfile();

    String deleteProfile();

    String reallyDeleteProfile();

    String accountNotActivated();

    String editRules();

    String userManagement();

    String sensorManagement();

    String showAllRules();

    String logout();

    String failedLoadControls();

    String failedRegistration();

    String emailSent();

    String failedLogin();

    String failedGeneratePassword();

    String passwordSended();

    String failedLogout();

    String failedGetUser();

    String failedUpdateUser();

    String updateSuccessful();

    String failedCreateBR();
    String creationSuccessful();
    String ruleExists();
    String failedGetAllUser();
    String failedGetStations();
    String failedGetPhenomena();
    String failedGetAllRegisteredSensors();
    String failedPublishRule();
    String unsubscribeSuccessful();
    String failedUpdateSensor();
    String failedDeleteSensor();
    String failedGetAllRules();

    String failedSubscribeSES();
    String failedSES();
    String failedSensorToUsed();
    String failedAddSensorToFeeder();
    String subscribeSuccessful1();
    String subscribeSuccessful2();
    String subscriptionInfo();
    String failedUnsubscribe();
    String failedDeleteSubscription();
    String failedDeleteRule();
    String failedGetUserSubscription();
    
    String failedWNSRegistration();
    String registrationSuccessful();
    String failedRegistration2();
    String failedDeleteUserFromWNS();
    String failedDeleteUserFromDB();
    
    String mailSubjectRegister();
    String mailSubjectPassword();
    String mailTextRegister();
    String mailTextPassword();
    
    String reallyDeleteRule();
    
    String cancelPublication();
    
    String publishThisRule();
    
    String publishButton();
    
    String ruleSubscribed();
    
    String accountLocked();
    
    String registerName();
    
    String registerEMail();
    
    String registerHandy();
    
    String validateEMail();
    
    String help();
    
    String unpublishButton();
    
    String lastAdmin();
    
    String mailSent();
    
    String profileDelete();
    
    String passwordChanged();
    
    String invalidInputs();
    
    String editComplexRule();
    
    String acceptTermsOfUseInfo();
    
    String errorSubscribeSES();
    
    String errorSubscribeFeeder();
    
    String errorUnsubscribeSES();
    
    String invalidPassword();
    
    String invalidName();
    
    String deletedUser();
    
    String invalidNewPasswordInputs();
    
    String search();
    
    String searchFullText();
    
    String sensor();
    
    String phenomenon();
    
    String subscriptionExists();
    
    String changeLanguage();
    
    String basic();
    
    String complex();
    
    String owner();
    
    String copy();
    
    String copyExists();
    
    String copyExistsSubscribe();
    
    String welcomeText();
    
    String welcome();
    
    String welcomeUserRole();
    
    String loggedinAs();
    
    String selectMedium();
    
    String selectFormat();
    
    String searchWord();
    
    String searchCriterion();
    
    String rules();
    
    String filterOwn();
    
    String filterOther();
    
    String filterBoth();
    
    String count();
    
    String unit();
    
    String filterQuestion();
    
    String editOtherRule();
    
    String timeUnit();
    
    String ruleType();
    
    String longNotificationMessage();
    
    String ruleNameStartsWithDigit();
    
    String cancel();
    
    String createUserSuccessful();
    
    String indicateCount();
    
    String newPassword();
    
    String currentPassword();
    
    String ruleNotFound();
    
    String createAboWindowTitle();

    String aboName();

    String timeseriesMetadataTable();

    String station();

    String provider();

    String selectPredefinedEventForSubscription();

    String seconds();

    String minutes();

    String hours();

    String creatingRuleWasUnsuccessful();

    String adminLogin();

    String back();

    String login();

	String onlyAdminsAllowedToLogin();

	String deleteOnlyWhenUnsubbscribed();

	String deleteSubscriptionQuestion();

	String validateTextBoxes();

    String loginIsOrHasBecomeInvalid();

    String failedSessionCreation();

    String helpPath();

	String relogin();
    
}