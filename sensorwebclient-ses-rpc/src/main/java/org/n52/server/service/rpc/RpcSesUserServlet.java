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
package org.n52.server.service.rpc;

import static org.n52.server.ses.util.WnsUtil.sendToWNSMail;
import static org.n52.shared.responses.SesClientResponseType.LAST_ADMIN;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
import org.n52.shared.session.LoginSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Delegates SES User requests to an {@link SesUserService} implementation.
 */
public class RpcSesUserServlet extends RemoteServiceServlet implements RpcSesUserService {

    private static final long serialVersionUID = -682767276044973231L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcSesUserServlet.class);
    
    private SesUserService service;
    
    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SES Client");
        service = new SesUserServiceImpl();
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
                LOGGER.error("Activation of user '{}' failed!", user.getId());
                writeHtmlResponse("User activation failed!", response);
            }
        }
    }

    protected boolean activateUser(User user) {
        // check user role to avoid changing admin role to user
        if (user != null) {
            try {
                if (user.getRole() != UserRole.ADMIN) {
                	// set user role to USER
                    user.setRole(UserRole.USER);
                    user.setActivated(true);
                } else {
                	// admin account is activated
                    user.setActivated(true);
                }
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else {
            return false;
        }
        return false;
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
        sb.append("<html><body>");
        sb.append("<h1>An exception occured</h1>");
        sb.append("<div>").append(message).append("</div>");
        if (e != null) {
            sb.append("<h2>Details</h2>");
            sb.append("<div>").append(e.getMessage()).append("</div>");
            sb.append("<ul style=\"list-style-type:none;\">");
            for (StackTraceElement trace : e.getStackTrace()) {
                sb.append("<br />");
                sb.append("<li>").append(trace.toString()).append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append("</body></html>");
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
    	return service.registerUser(userDTO);
    }

    @Override
    public SesClientResponse login(String userName, String password) throws Exception {
        return service.login(userName, password);
    }

    @Override
    public SesClientResponse resetPassword(String userName, String email) throws Exception {
        return service.resetPassword(userName, email);
    }

    @Override
    public void logout(LoginSession loginSession) throws Exception {
        service.logout(loginSession);
    }

    @Override
    public UserDTO getUser(LoginSession loginSession) throws Exception {
        return service.getUser(loginSession);
    }

    @Override
    public SesClientResponse deleteUser(LoginSession loginSession, String id) throws Exception {
        return service.deleteUser(loginSession, id);
    }

    
    public SesClientResponse performUserDelete(String userId) throws Exception {
        return ((SesUserServiceImpl) service).performUserDelete(userId);
    }

    @Override
    public SesClientResponse updateUser(LoginSession loginSession, UserDTO newUser) throws Exception {
        return service.updateUser(loginSession, newUser);
    }

    @Override
    public List<UserDTO> getAllUsers(LoginSession loginSession) throws Exception {
       return service.getAllUsers(loginSession);
    }

    @Override
    public SesClientResponse requestToDeleteProfile(LoginSession loginSession) throws Exception {
        return service.requestToDeleteProfile(loginSession);
    }

    @Override
    public SesClientResponse getTermsOfUse(String language) throws Exception {
        return service.getTermsOfUse(language);
    }

    // initial data from property file
    // this method is called on first startup
    public SesClientResponse getData() throws Exception {
        return service.getData();
    }

}