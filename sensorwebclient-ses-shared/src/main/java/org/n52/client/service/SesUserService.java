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

package org.n52.client.service;

import java.util.List;

import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.session.LoginSession;

/**
 * TODO extract admin operations
 */
public interface SesUserService {

    /**
     * Starts registration process to register a new user. A new user has to verify registration via email,
     * before login is complete.
     * 
     * @param userDTO
     *        the user data to use for registration.
     * @return a response object containing information about the new use.
     * @throws Exception
     *         if registration process fails.
     */
    public SesClientResponse registerUser(UserDTO userDTO) throws Exception;

    /**
     * Logs in a user with given username and password hash.
     * 
     * @param userName
     *        the user's username.
     * @param password
     *        the hashed password.
     * @return a response object containing login status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse login(String userName, String password) throws Exception;

    /**
     * Starts process of resetting a user's password. The given email address has to match with that address
     * belonging to the stored user.
     * 
     * @param username
     *        the user's username
     * @param email
     *        the user's email.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse resetPassword(String username, String email) throws Exception;

    /**
     * Logs out a user and destroys the user's session hold on server side.
     * 
     * @param loginSession
     *        the user's login session to destroy.
     * @throws Exception
     *         if processing request fails.
     */
    public void logout(LoginSession loginSession) throws Exception;

    /**
     * Gets user information from given login session object.
     * 
     * @param loginSession
     *        the active login session object.
     * @return the user information as data transfer object.
     * @throws Exception
     *         if processing request fails.
     */
    public UserDTO getUser(LoginSession loginSession) throws Exception;

    /**
     * Deletes a user with the given user id. only user's with {@link UserRole#ADMIN} are allowed to delete a
     * user directly. Deletion of normal users are handled via
     * {@link #requestToDeleteProfile(LoginSession, String)} as it starts a confirmation process beforehand.<br>
     * <br>
     * Note that at least one user with {@link UserRole#ADMIN} has to exist.
     * 
     * @param loginSession
     *        the user's active login session object.
     * @param userId
     *        the id of the user to delete.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     * 
     * @see #requestToDeleteProfile(LoginSession, String)
     * @see #performUserDelete(String)
     */
    public SesClientResponse deleteUser(LoginSession loginSession, String userId) throws Exception;

    /**
     * Updates user information of a registered user.
     * 
     * @param loginSession
     *        the user's active login session object.
     * @param userDataToUpdate
     *        the data to update the registered user with.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse updateUser(LoginSession loginSession, UserDTO userDataToUpdate) throws Exception;

    /**
     * Gets all users registered at the system. Only user's with {@link UserRole#ADMIN} are allowed to a list
     * of all users.
     * 
     * @param loginSession
     *        the user's active login session object.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public List<UserDTO> getAllUsers(LoginSession loginSession) throws Exception;

    /**
     * Starts deletion process of a user. The deletion has to be confirmed by email before deletion takes
     * place.<br>
     * <br>
     * Note that at least one user with {@link UserRole#ADMIN} has to exist.
     * 
     * @param loginSession
     *        the user's active login session object.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse requestToDeleteProfile(LoginSession loginSession) throws Exception;

    /**
     * XXX remove this method and integrate terms of use on client side only
     */
    public SesClientResponse getTermsOfUse(String language) throws Exception;

    /**
     * XXX let UI read config directly from URL (as SOS client does)
     * 
     * @return the SES configuration
     */
    public SesClientResponse getData() throws Exception;

}