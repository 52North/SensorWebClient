/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.client.service;

import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.session.SessionInfo;

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
     * Logs in a user with given username and password hash. To prevent logins from external sources an
     * not-logged-in session has to be provided which can be retrieved by calling
     * {@link #getNewLoginSessionid()} beforehand.
     * 
     * @param userName
     *        the user's username.
     * @param password
     *        the hashed password.
     * @param notLoggedInSession
     *        a not-logged-in session.
     * @return a response object containing login status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse login(String userName, String password, SessionInfo notLoggedInSession) throws Exception;

    /**
     * @param sessionInfo
     *        the active session info.
     * @return the user information as data transfer object.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse validateLoginSession(SessionInfo sessionInfo) throws Exception;

    /**
     * Lets the server create a session which is considered to be a not-logged-in session. 
     * 
     * @return an inactive session info.
     * @throws Exception
     *         if processing request fails.
     */
    public SessionInfo createNotLoggedInSession() throws Exception;

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
     * @param sessionInfo
     *        the user's session info to destroy.
     * @throws Exception
     *         if processing request fails.
     */
    public void logout(SessionInfo sessionInfo) throws Exception;

    /**
     * Gets user information from given session info object.
     * 
     * @param sessionInfo
     *        the active session info object.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse getUser(SessionInfo sessionInfo) throws Exception;

    /**
     * Deletes a user with the given user id. only user's with {@link UserRole#ADMIN} are allowed to delete a
     * user directly. Deletion of normal users are handled via
     * {@link #requestToDeleteProfile(SessionInfo, String)} as it starts a confirmation process beforehand.<br>
     * <br>
     * Note that at least one user with {@link UserRole#ADMIN} has to exist.
     * 
     * @param sessionInfo
     *        the user's active session info object.
     * @param userId
     *        the id of the user to delete.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     * 
     * @see #requestToDeleteProfile(SessionInfo, String)
     * @see #performUserDelete(String)
     */
    public SesClientResponse deleteUser(SessionInfo sessionInfo, String userId) throws Exception;

    /**
     * Updates user information of a registered user.
     * 
     * @param sessionInfo
     *        the user's active session info object.
     * @param userDataToUpdate
     *        the data to update the registered user with.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse updateUser(SessionInfo sessionInfo, UserDTO userDataToUpdate) throws Exception;

    /**
     * Gets all users registered at the system. Only user's with {@link UserRole#ADMIN} are allowed to a list
     * of all users.
     * 
     * @param sessionInfo
     *        the user's active session info object.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse getAllUsers(SessionInfo sessionInfo) throws Exception;

    /**
     * Starts deletion process of a user. The deletion has to be confirmed by email before deletion takes
     * place.<br>
     * <br>
     * Note that at least one user with {@link UserRole#ADMIN} has to exist.
     * 
     * @param sessionInfo
     *        the user's session info object.
     * @return a response object containing process status.
     * @throws Exception
     *         if processing request fails.
     */
    public SesClientResponse requestToDeleteProfile(SessionInfo sessionInfo) throws Exception;

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