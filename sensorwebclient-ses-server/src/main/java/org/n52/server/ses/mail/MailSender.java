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
package org.n52.server.ses.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.n52.server.ses.SesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailSender {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MailSender.class);

    public static void sendRegisterMail(String address, String id, String userName) {
        LOGGER.debug("send register mail to: " + address);

        String link = "?user=" + id;
        String tempText_de = SesConfig.mailTextRegister_de.replace("_NAME_", userName);
        String tempText_en = SesConfig.mailTextRegister_en.replace("_NAME_", userName);
        
        String text = tempText_en + "\n" + tempText_de + "\n" + SesConfig.URL + link;

        Session session = Session.getDefaultInstance(getProperties(), getMailAuthenticator());

        try {
            // create a new message
            Message msg = new MimeMessage(session);

            // set sender and receiver
            msg.setFrom(new InternetAddress(SesConfig.SENDER_ADDRESS));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));

            // set subject to mail
            msg.setSubject(SesConfig.mailSubjectRegister_en + "/" + SesConfig.mailSubjectRegister_de);
            msg.setText(text);
            msg.setSentDate(new Date());

            // send message
            Transport.send(msg);
            LOGGER.debug("mail send succesfully done");
        } catch (Exception e) {
            LOGGER.error("Error occured while sending register mail: " + e.getMessage(), e);
        }
    }

    public static void sendPasswordMail(String address, String newPassword) {
        LOGGER.debug("send new password to: " + address);

        final String text = SesConfig.mailTextPassword_en + ": " + "\n\n" +
        SesConfig.mailTextPassword_de + ": " + "\n\n" + newPassword;

        Session session = Session.getDefaultInstance(getProperties(), getMailAuthenticator());

        try {
            // create a new message
            Message msg = new MimeMessage(session);

            // set sender and receiver
            msg.setFrom(new InternetAddress(SesConfig.SENDER_ADDRESS));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));

            // set subject to mail
            msg.setSubject(SesConfig.mailSubjectPassword_en + "/" + SesConfig.mailSubjectPassword_de);
            msg.setText(text);
            msg.setSentDate(new Date());

            // send mail
            Transport.send(msg);
            LOGGER.debug("mail send succesfully done");
        } catch (Exception e) {
            LOGGER.error("Error occured while sending password mail: " + e.getMessage(), e);
        }
    }

    public static boolean sendDeleteProfileMail(String address, String userID) {
        LOGGER.debug("send delete profile mail to: " + address);

        String link = "?delete=" + userID;
        String text = SesConfig.mailDeleteProfile_en + ": " + "\n\n" +
        SesConfig.mailDeleteProfile_de + ": " + "\n\n" + SesConfig.URL + link;

        Session session = Session.getDefaultInstance(getProperties(), getMailAuthenticator());

        try {
            // send a new message
            Message msg = new MimeMessage(session);

            // set sender and receiver
            msg.setFrom(new InternetAddress(SesConfig.SENDER_ADDRESS));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));

            // set subject
            msg.setSubject(SesConfig.mailSubjectDeleteProfile_en + "/" + SesConfig.mailSubjectDeleteProfile_de);
            msg.setText(text);
            msg.setSentDate(new Date());

            // send mail
            Transport.send(msg);
            LOGGER.debug("mail send succesfully done");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error occured while sending delete profile mail: " + e.getMessage(), e);
        }
        return false;
    }

    public static boolean sendEmailValidationMail(String address, String userID) {
        LOGGER.debug("send email validation mail to: " + address);

        String link = "?validate=" + userID;
        String text = SesConfig.mailTextValidation_en + ": " + "\n" +
        SesConfig.mailTextValidation_de + ": " + "\n" + SesConfig.URL + link;

        Session session = Session.getDefaultInstance(getProperties(), getMailAuthenticator());

        try {
            // create a new message
            Message msg = new MimeMessage(session);

            // set sender and receiver
            msg.setFrom(new InternetAddress(SesConfig.SENDER_ADDRESS));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));

            // set subject
            msg.setSubject(SesConfig.mailSubjectValidation_en + "/" + SesConfig.mailSubjectValidation_de);
            msg.setText(text);
            msg.setSentDate(new Date());

            // send mail
            Transport.send(msg);
            LOGGER.debug("mail send succesfully done");
            return true;
        } catch (Exception e) {
            LOGGER.error("Error occured while sending email validation mail: " + e.getMessage(), e);
        }
        return false;
    }

    public static boolean sendSensorDeactivatedMail(String address, String sensorID) {
        LOGGER.debug("send email validation mail to: " + address);

        String text = SesConfig.mailSubjectSensor_en + ": " + "\n" +
        SesConfig.mailSubjectSensor_de + ": " + "\n\n" +
        sensorID;

        Session session = Session.getDefaultInstance(getProperties(), getMailAuthenticator());

        try {
            // create a message
            Message msg = new MimeMessage(session);

            // set sender and receiver
            msg.setFrom(new InternetAddress(SesConfig.SENDER_ADDRESS));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address, false));

            // set subject
            msg.setSubject(SesConfig.mailSubjectSensor_en + "/" + SesConfig.mailSubjectSensor_de);
            msg.setText(text);
            msg.setSentDate(new Date());

            // send mail
            Transport.send(msg);
            LOGGER.debug("mail send succesfully done");
            return true;
        } catch (MessagingException e) {
            LOGGER.error("Error occured while sending sensor deactivation mail: " + e.getMessage(), e);
        }
        return false;
    }

    private static Properties getProperties() {
        Properties properties = new Properties();

        properties.put("mail.smtp.starttls.enable", SesConfig.STARTTLS_ENABLE);
        properties.put("mail.smtp.host", SesConfig.SMTP_HOST);
        properties.put("mail.smtp.user", SesConfig.USER_NAME);
        properties.put("mail.smtp.password", SesConfig.PASSWORD);
        properties.put("mail.smtp.port", SesConfig.PORT); 
        properties.put("mail.smtp.auth", SesConfig.AUTH); 
        properties.put("mail.smtp.ssl.enable", SesConfig.SSL_ENABLE);

        return properties;
    }

    private static MailAuthenticator getMailAuthenticator() {
        // http://javamail.kenai.com/nonav/javadocs/javax/mail/Session.html#getDefaultInstance%28java.util.Properties,%20javax.mail.Authenticator%29
        if (Boolean.parseBoolean(SesConfig.AUTH)) {
            return new MailAuthenticator(SesConfig.USER_NAME, SesConfig.PASSWORD);
        }
        return null;
    }
}