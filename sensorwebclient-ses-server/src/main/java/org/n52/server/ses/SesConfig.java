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
package org.n52.server.ses;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO config mechanism should be redesigned
public class SesConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SesConfig.class);

    public static boolean initialized = false;

    private static String propertiesLocation;

    public static String serviceVersion;

    public static String wns;

    /** The ses-wns translator endpoint -->consumerReference*/
    public static String consumerReference;

    @Deprecated
    public static boolean warnUserLongNotification;
    
    @Deprecated
    public static int minimumPasswordLength;

//    /** The delay */
//    public static long delay;
    
    public static String applicationCookieDomain;

    public static String URL;

    public static String sesEndpoint;

    public static String resLocation_1;

    public static String resLocation_2;

    public static String resLocation_3;

    public static String resLocation_4;

    public static String resLocation_5;

    public static String resLocation_meta_text;

    public static String resLocation_meta_XML;
    
    public static String resLocation_meta_EML;

    public static String resLocation_logical;

    public static String resLocation_structural;

    public static String USER_NAME;

    public static String PASSWORD;

    public static String SENDER_ADDRESS;

    public static String SMTP_HOST;
    
    public static String STARTTLS_ENABLE;
    
    public static String PORT;
    
    public static String AUTH;
    
    public static String SSL_ENABLE;

    public static String path;

    public static String termsOfUse;

    /** mailSubjectRegister */
    public static String mailSubjectRegister_en;
    public static String mailSubjectRegister_de;

    /** mailSubjectPassword */
    public static String mailSubjectPassword_en;
    public static String mailSubjectPassword_de;

    /** mailTextRegister */
    public static String mailTextRegister_en;
    public static String mailTextRegister_de;

    /** mailTextPassword */
    public static String mailTextPassword_en;
    public static String mailTextPassword_de;

    /** mailDeleteProfile */
    public static String mailDeleteProfile_en;
    public static String mailDeleteProfile_de;

    /** mailSubjectValidation */
    public static String mailSubjectValidation_en;
    public static String mailSubjectValidation_de;

    /** mailTextValidation */
    public static String mailTextValidation_en;
    public static String mailTextValidation_de;

    /** mailSubjectDeleteProfile */
    public static String mailSubjectDeleteProfile_en;
    public static String mailSubjectDeleteProfile_de;

    /** mailSubjectSensor */
    public static String mailSubjectSensor_en;
    public static String mailSubjectSensor_de;
    
    public static String adminMessage;

    /** sensorUpdateRate */
    public static long sensorUpdateRate = 3000;

    /** delteUserInterval */
    public static long deleteUserInterval = 100000;
    
    @Deprecated
    public static String availableWNSmedia;
    @Deprecated
    public static String defaultMedium;
    @Deprecated
    public static String availableFormats;
    @Deprecated
    public static String defaultFormat;
    
    public synchronized static void init(String realPath){
        LOGGER.debug("init");
        SesConfig.propertiesLocation = realPath + "properties/ses-client.properties";
        if (!initialized) {
            try {
                LOGGER.info("## Loading properties ##");

                Properties properties = new Properties();
                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertiesLocation));
                properties.load(stream);
                stream.close();

                path = realPath;
                
                applicationCookieDomain = properties.getProperty("applicationCookieDomain");

                serviceVersion = properties.getProperty("serviceVersion");
                wns = properties.getProperty("wns");
                consumerReference = properties.getProperty("consumerReference");
                warnUserLongNotification  = Boolean.valueOf(properties.getProperty("warnUserLongNotification"));
                minimumPasswordLength = Integer.valueOf(properties.getProperty("minimumPasswordLength"));
                
                URL = properties.getProperty("url");
                sesEndpoint = properties.getProperty("sesEndpoint");

                resLocation_1 = properties.getProperty("resLocation") + "BR_1.xml";
                resLocation_2 = properties.getProperty("resLocation") + "BR_2.xml";
                resLocation_3 = properties.getProperty("resLocation") + "BR_3.xml";
                resLocation_4 = properties.getProperty("resLocation") + "BR_4.xml";
                resLocation_5 = properties.getProperty("resLocation") + "BR_5.xml";
                resLocation_meta_text = properties.getProperty("resLocation") + "format_text.xml";
                resLocation_meta_XML = properties.getProperty("resLocation") + "format_xml.xml";
                resLocation_meta_EML = properties.getProperty("resLocation") + "format_xml_eml.xml";
                resLocation_logical = properties.getProperty("resLocation") + "LogicalOperator.xml";
                resLocation_structural = properties.getProperty("resLocation") + "StructuralOperator.xml";

                availableWNSmedia = properties.getProperty("availableWNSmedia");
                defaultMedium = properties.getProperty("defaultMedium");
                availableFormats = properties.getProperty("availableFormats");
                defaultFormat = properties.getProperty("defaultFormat");

                // E-Mail texts
                mailSubjectRegister_en = properties.getProperty("mailSubjectRegister_en");
                mailSubjectRegister_de = properties.getProperty("mailSubjectRegister_de");

                mailSubjectPassword_en = properties.getProperty("mailSubjectPassword_en");
                mailSubjectPassword_de = properties.getProperty("mailSubjectPassword_de");

                mailTextRegister_en = properties.getProperty("mailTextRegister_en");
                mailTextRegister_de = properties.getProperty("mailTextRegister_de");

                mailTextPassword_en = properties.getProperty("mailTextPassword_en");
                mailTextPassword_de = properties.getProperty("mailTextPassword_de");

                mailDeleteProfile_en = properties.getProperty("mailDeleteProfile_en");
                mailDeleteProfile_de = properties.getProperty("mailDeleteProfile_de");

                mailSubjectDeleteProfile_en = properties.getProperty("mailSubjectDeleteProfile_en");
                mailSubjectDeleteProfile_de = properties.getProperty("mailSubjectDeleteProfile_de");

                mailSubjectValidation_en = properties.getProperty("mailSubjectValidation_en");
                mailSubjectValidation_de = properties.getProperty("mailSubjectValidation_de");

                mailTextValidation_en = properties.getProperty("mailTextValidation_en");
                mailTextValidation_de = properties.getProperty("mailTextValidation_de");

                mailSubjectSensor_en = properties.getProperty("mailSubjectSensor_en");
                mailSubjectSensor_de = properties.getProperty("mailSubjectSensor_de");

                Long l = Long.valueOf(properties.getProperty("sensorUpdateRate"));
                if (l != null) {
                    sensorUpdateRate = l; 
                } else {
                    LOGGER.error("sensorUpdateRate could not be read. Set value to default = 3000");
                }
                deleteUserInterval = Long.valueOf(properties.getProperty("deleteUserInterval"));

                adminMessage = properties.getProperty("adminMessage");
                
                LOGGER.info("## Loading properties finished ##");
                initialized = true;
            } catch (IOException e) {
                LOGGER.error("Load properties failed!", e);
            }
        }
    }
}