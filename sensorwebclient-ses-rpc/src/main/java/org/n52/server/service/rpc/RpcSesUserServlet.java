/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.server.service.rpc;

import static org.n52.server.ses.util.WnsUtil.sendToWNSMail;
import static org.n52.shared.responses.SesClientResponseType.LAST_ADMIN;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.client.service.SesUserService;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.service.SesUserServiceImpl;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.service.rpc.RpcSesUserService;
import org.n52.shared.session.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.n52.server.ses.util.SesServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Delegates SES User requests to an {@link SesUserService} implementation.
 */
public class RpcSesUserServlet extends RemoteServiceServlet implements RpcSesUserService {

    private static final long serialVersionUID = -682767276044973231L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesUserServlet.class);

    private final SesUserService service = SesServiceConfig.getService("sesUserService", SesUserService.class);

    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
    }

    private SesUserService getService() {
        if (service == null) {
            LOGGER.error("SesUserService not configured properly. Check 'spring-ses-config.xml'.");
            throw new NullPointerException("SES module not available.");
        }
        return service;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        // attribute string at the end of the url
    	String registerID = request.getQueryString();
        User user = null;

        // operator (user, delete, validate)
        String operator = registerID.substring(0, 6);

        // delete account
        if (operator.equals("delete")) {
            LOGGER.info("RECEIVE delete profile link");
            // user ID
            String id = registerID.substring(7);
            // user
            user = HibernateUtil.getUserBy(id);
            int userID = user.getId();
            try {
                SesClientResponse result = performUserDelete(String.valueOf(userID));
                if (result.getType() == LAST_ADMIN) {
                    writeHtmlResponse("You are the last admin! At one admin has to exist.", response);
                }
                return;
            } catch (Exception e) {
                LOGGER.error("Error deleting user", e);
                writeHtmlResponse("Could not delete user.", e, response);
            }
            // validation of new email address
        } else if (operator.equals("valida")) { // XXX check operator here
        	// user ID
            String id = registerID.substring(9);
            // user
            user = HibernateUtil.getUserBy(id);
            // set the flag "emailVerified" to true
            user.setEmailVerified(true);
            HibernateUtil.updateUser(user);
            writeHtmlResponse("Your e-mail validation was successful!", response);
            return;
        } else {
            // register new account
            LOGGER.info("RECEIVE activation link from new user");

            // user data from DB
            String id = registerID.substring(5);
            user = HibernateUtil.getUserBy(id);

            if (user != null) {
            	if (activateUser(user)) {
            		LOGGER.debug("register user: " + user.getName() + " to WNS");
                    try {
                        user.setWnsEmailId(sendToWNSMail(user.getName(), user.geteMail()));
                        HibernateUtil.updateUser(user);
                    } catch (Exception e) {
                        LOGGER.error("Registration to WNS failed!", e);
                        writeHtmlResponse("Registration to WNS failed!", e, response);
                    }
                    writeHtmlResponse("Registration successful!", response);
            	} else {
            		String userActiv = "User activation is still done!";
                	LOGGER.error(userActiv);
                	writeHtmlResponse(userActiv, response);
            	}
            } else {
            	String noUserToActivationID = "No user for this activation link!";
            	LOGGER.error(noUserToActivationID);
            	writeHtmlResponse(noUserToActivationID, response);
            }
        }
    }

    protected boolean activateUser(User user) {
        // check user role to avoid changing admin role to user
        if (!user.getActivated()) {
            try {
                if (user.getRole() != UserRole.ADMIN) {
                	// set user role to USER
                    user.setRole(UserRole.USER);
                    user.setActivated(true);
                } else {
                	// admin account is activated
                    user.setActivated(true);
                }
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else {
            return false;
        }
    }

    private void writeHtmlResponse(String message, HttpServletResponse response) {
        message = includeExceptionInfoTo(message, null);
        writeResponse(message, response);
    }

    private void writeHtmlResponse(String message, Exception e, HttpServletResponse response) {
        message = includeExceptionInfoTo(message, e);
        writeResponse(message, response);
    }

    private String includeExceptionInfoTo(String message, Exception e) {
        StringBuilder sb = new StringBuilder(message);
        if (e != null) {
        	sb.append("<html><body>");
            sb.append("<h1>An exception occured</h1>");
            sb.append("<h2>Details</h2>");
            sb.append("<div>").append(e.getMessage()).append("</div>");
            sb.append("<ul style=\"list-style-type:none;\">");
            for (StackTraceElement trace : e.getStackTrace()) {
                sb.append("<br />");
                sb.append("<li>").append(trace.toString()).append("</li>");
            }
            sb.append("</ul>");
            sb.append("</body></html>");
        }
        return sb.toString();
    }

    private void writeResponse(String message, HttpServletResponse response) {
        PrintWriter writer = null;
        try {
            response.getWriter().println(message);
        } catch (IOException e) {
            LOGGER.error("Could not write to response", e);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    @Override
    public SesClientResponse registerUser(UserDTO userDTO) throws Exception {
    	return getService().registerUser(userDTO);
    }

    @Override
    public SesClientResponse login(String userName, String password, SessionInfo sessionInfo) throws Exception {
        return getService().login(userName, password, sessionInfo);
    }

    @Override
    public SesClientResponse validateLoginSession(SessionInfo sessionInfo) throws Exception {
        return getService().validateLoginSession(sessionInfo);
    }

    @Override
    public SessionInfo createNotLoggedInSession() throws Exception {
        return getService().createNotLoggedInSession();
    }

    @Override
    public SesClientResponse resetPassword(String userName, String email) throws Exception {
        return getService().resetPassword(userName, email);
    }

    @Override
    public void logout(SessionInfo sessioninfo) throws Exception {
        getService().logout(sessioninfo);
    }

    @Override
    public SesClientResponse getUser(SessionInfo sessionInfo) throws Exception {
        return getService().getUser(sessionInfo);
    }

    @Override
    public SesClientResponse deleteUser(SessionInfo sessioninfo, String id) throws Exception {
        return getService().deleteUser(sessioninfo, id);
    }


    public SesClientResponse performUserDelete(String userId) throws Exception {
        return ((SesUserServiceImpl) getService()).performUserDelete(userId);
    }

    @Override
    public SesClientResponse updateUser(SessionInfo sessionInfo, UserDTO newUser) throws Exception {
        return getService().updateUser(sessionInfo, newUser);
    }

    @Override
    public SesClientResponse getAllUsers(SessionInfo sessionInfo) throws Exception {
       return getService().getAllUsers(sessionInfo);
    }

    @Override
    public SesClientResponse requestToDeleteProfile(SessionInfo sessionInfo) throws Exception {
        return getService().requestToDeleteProfile(sessionInfo);
    }

    @Override
    public SesClientResponse getTermsOfUse(String language) throws Exception {
        return getService().getTermsOfUse(language);
    }

    // initial data from property file
    // this method is called on first startup
    public SesClientResponse getData() throws Exception {
        return getService().getData();
    }

}