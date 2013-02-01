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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.client.service.SesUserService;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.service.SesUserServiceImpl;
import org.n52.server.ses.util.WnsUtil;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.service.rpc.RpcSesUserService;
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
    public synchronized void doGet(HttpServletRequest request, HttpServletResponse response) {
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
                deleteUser(String.valueOf(userID));
                return;
            } catch (Exception e) {
                LOGGER.error("Error deleting user", e);
            }
            // validation of new email address
        } else if (operator.equals("valida")) {
        	// user ID
            String id = registerID.substring(9);
            // user
            user = HibernateUtil.getUserBy(id);
            // set the flag "emailVerified" to true
            user.setEmailVerified(true);
            HibernateUtil.updateUser(user);
            
            try {
            	// user information
                PrintWriter writer = response.getWriter();
                writer.println("Your e-mail validation was successful!");
                writer.flush();
            } catch (IOException e) {
                LOGGER.error("Could not read response", e);
            }
            return;
        } else {

            // register new account
            LOGGER.info("RECEIVE activation link from new user");
            boolean successfull = true;

            // user data from DB
            String id = registerID.substring(5);
            user = HibernateUtil.getUserBy(id);

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
                    successfull = false;
                }
            } else {
                // no user found for the registerID
                successfull = false;
            }
            try {
                PrintWriter writer = response.getWriter();
                if (successfull) {
                    LOGGER.debug("register user: " + user.getName() + " to WNS");
                    try {
                        user.setWnsEmailId(WnsUtil.sendToWNSMail(user.getName(), user.geteMail()));

                        // add WNS IDs to user data
                        HibernateUtil.updateUser(user);
                    } catch (Exception e) {
                        writer.println("Registration to WNS failed!");
                        LOGGER.error("Registration to WNS failed!", e);
                    }
                    writer.println("Your registration was successful!");
                } else {
                    writer.println("Registration failed! Because the registration ID is unknown.");
                }
                writer.flush();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

    public synchronized SesClientResponse registerUser(UserDTO userDTO) throws Exception {
    	return service.registerUser(userDTO);
    }

    public synchronized SesClientResponse login(String userName, String password, boolean isAdminLogin) throws Exception {
        return service.login(userName, password, isAdminLogin);
    }

    public synchronized SesClientResponse newPassword(String userName, String email) throws Exception {
        return service.newPassword(userName, email);
    }

    public synchronized void logout() throws Exception {
        service.logout();
    }

    public synchronized UserDTO getUser(String id) throws Exception {
        return service.getUser(id);
    }

    public synchronized SesClientResponse deleteUser(String id) throws Exception {
        return service.deleteUser(id);
    }

    public synchronized SesClientResponse updateUser(UserDTO newUser, String userID) throws Exception {
        return service.updateUser(newUser, userID);
    }

    public synchronized List<UserDTO> getAllUsers() throws Exception {
       return service.getAllUsers();
    }

    public SesClientResponse deleteProfile(String id) throws Exception {
        return service.deleteProfile(id);
    }

    public SesClientResponse getTermsOfUse(String language) throws Exception {
        return service.getTermsOfUse(language);
    }

    // initial data from property file
    // this method is called on first startup
    public SesClientResponse getData() throws Exception {
        return service.getData();
    }
}