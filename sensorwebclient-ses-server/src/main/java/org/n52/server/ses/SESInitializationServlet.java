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

import static org.n52.server.ses.feeder.SosSesFeeder.createSosSesFeeder;

import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.swing.JOptionPane;

import org.jfree.util.Log;
import org.n52.server.ses.eml.Meta_Builder;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.service.SesUserServiceImpl;
import org.n52.server.ses.util.SesServerUtil;
import org.n52.server.ses.util.WnsUtil;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Startup unit of the SES-Client. Checks connections to SES and WNS.
 * 
 * FIXME Refactor! Avoid using servlet!
 */
public class SESInitializationServlet extends HttpServlet {

    private static final long serialVersionUID = -8453052195694079440L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SESInitializationServlet.class);

    public static boolean SESavailable = false;

    public static boolean WNSavailable = false;

    public static boolean initialized = false;

    @Override
    public void init() throws ServletException {
        
        try {
            LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");

            SesConfig.init(this.getServletContext().getRealPath("/"));
            SesConfig.USER_NAME = this.getServletContext().getInitParameter("MAIL_USERNAME");
            SesConfig.PASSWORD = this.getServletContext().getInitParameter("MAIL_PASSWORD");
            SesConfig.SENDER_ADDRESS = this.getServletContext().getInitParameter("MAIL_SENDER_ADDRESS");
            SesConfig.SMTP_HOST = this.getServletContext().getInitParameter("MAIL_SMTP_HOST");
            SesConfig.STARTTLS_ENABLE = this.getServletContext().getInitParameter("MAIL_STARTTLS_ENABLE");
            SesConfig.PORT = this.getServletContext().getInitParameter("MAIL_PORT");
            SesConfig.AUTH = this.getServletContext().getInitParameter("MAIL_AUTH");
            SesConfig.SSL_ENABLE = this.getServletContext().getInitParameter("MAIL_SSL_ENABLE");
            
            LOGGER.info("ckeck availability of SES and WNS");
            Thread t = new Thread(new Runnable() {
                public void run() {
                    checkAvailability();
                }
            });
            t.start();

            // init the servlet sesUserService. This servlet handle user registrations
            // and creation
            LOGGER.info("init sesUserService");
            startInitializingSesUserService();
        } catch (Exception e) {
            LOGGER.error("Could not initialize servlet appropriatly", e);
            return;
        }

        // validate templates
        LOGGER.info("Validate templates");
        Thread validationThread = new Thread(new Runnable() {
            public void run() {
                try {
                    templatesValidation();
                } catch (Exception e) {
                    LOGGER.error("Error validating template", e);
                }
            }
        });
        validationThread.start();
    }

    /**
     * This method checks all 10 seconds the availability of the SES and the WNS.
     * After both services are available, the registered sensors from SES are stored in DB
     */
    private void checkAvailability() {
        Thread checkThread = new Thread(new Runnable() {
            public void run() {
                while (!SESInitializationServlet.SESavailable || !SESInitializationServlet.WNSavailable) {
                    try {
                        // check if SES is available
                        if (!SESInitializationServlet.SESavailable) {
                            SESInitializationServlet.SESavailable = SesServerUtil.isAvailable();
                            if (!SESInitializationServlet.SESavailable) {
                                LOGGER.warn("SES (\"" + SesConfig.sesEndpoint + "\") is not (yet) available.");
                            }
                        }

                        // check if WNS is available
                        if (!SESInitializationServlet.WNSavailable) {
                            SESInitializationServlet.WNSavailable = WnsUtil.isAvailable();
                            if (!SESInitializationServlet.WNSavailable) {
                                LOGGER.warn("WNS (\"" + SesConfig.wns + "\") is not (yet) available.");
                            }
                        } 
                        
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        LOGGER.trace("Checking service was interrupted.", e);
                    }
                }
                SESInitializationServlet.initialized = true;
                boolean startAutomatically = true;
                createSosSesFeeder(startAutomatically);
            }
        });
        checkThread.start();
    }

    /**
     * This method cheks whether it is possible to build rules without exceptions.
     * If one exception is thrown --> template is not valid
     */
    private void templatesValidation() throws Exception {
    	// wait until servlet is initialized
        while (!SESInitializationServlet.initialized) {
            Thread.yield();
        }
        String ruleName = "DUMMY_RULE";
        String medium = "E-Mail";

        // create dummy user
        User dummyUser = new User();
        dummyUser.setWnsEmailId("999999");

        // create dummy basic Rule
        BasicRule dummyBasicrule = new BasicRule();
        dummyBasicrule.setName(ruleName);

        try {
            Meta_Builder.createTextMeta(dummyUser, ruleName, medium);
            Meta_Builder.createTextFailureMeta(dummyUser, dummyBasicrule, medium, "dummySensor");
            Meta_Builder.createXMLMeta(dummyUser, ruleName, medium, "XML");
            Meta_Builder.createXMLMeta(dummyUser, ruleName, medium, "EML");
        } catch (Exception e) {
            LOGGER.error("Template validation failed! Please change the templates and restart the application", e);
            SESInitializationServlet.initialized = false;
            JOptionPane.showMessageDialog(null, SesConfig.adminMessage);
        }

    }

    private void startInitializingSesUserService() {

        Thread sesUserThread = new Thread(new Runnable() {
            public void run() {
                while (!SESInitializationServlet.initialized) {
                    try {
                        // check all 20 seconds
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        LOGGER.trace("Checking service was interrupted.", e);
                    }
                }
                // run only if the init servlet is initialized
                if (SESInitializationServlet.initialized) {
                    LOGGER.info("create admin user");

                    // create default admin on start
                    UserDTO admin =
                            SesUserServiceImpl.createUserDTO(new User("admin", "Admin", SesServerUtil.createMD5("admin"),
                                    SesConfig.SENDER_ADDRESS, UserRole.ADMIN, true));
                    admin.setRegisterID(UUID.randomUUID().toString());

                    // check if default admin already exists
                    if (!HibernateUtil.existsUserName(admin.getUserName())) {
                        try {
                            LOGGER.debug("get IDs from WNS for admin");
                            admin.setWnsEmailId(WnsUtil.sendToWNSMail(admin.getName(), admin.geteMail()));
//                            admin.setWnsSmsId(WnsUtil
//                                    .sendToWNSSMS(admin.getName(), String.valueOf(admin.getHandyNr())));

                            HibernateUtil.save(new User(admin));
                        } catch (Exception e) {
                            LOGGER.debug("WNS is not available.", e);
                        }
                    } else {
                        LOGGER.debug("default admin already exists");
                    }

                    // in debug-mode. check if default user already exists
                    if (Log.isDebugEnabled()) {
                        UserDTO user =
                            SesUserServiceImpl.createUserDTO(new User("user", "User", SesServerUtil.createMD5("user"),
                                    "52n.development@googlemail.com", UserRole.USER, true));
                        if (!HibernateUtil.existsUserName(user.getUserName())) {
                            user.setRegisterID(UUID.randomUUID().toString());

                            try {
                                user.setWnsEmailId(WnsUtil.sendToWNSMail(user.getName(), user.geteMail()));
                                HibernateUtil.save(new User(user));
                            } catch (Exception e) {
                                LOGGER.debug("WNS is not available.",e);
                            }
                        }
                    }
                }
                return;
            }
        });
        sesUserThread.run();
    }

    @Override
    public void destroy() {
        try {
            HibernateUtil.closeDatabaseSessionFactory();
        } catch (Exception e) {
            LOGGER.error("Could not close database session factory appropriatly.", e);
        }

        super.destroy();
    }

}