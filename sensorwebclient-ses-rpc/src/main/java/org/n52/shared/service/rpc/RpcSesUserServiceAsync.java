/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
/**********************************************************************************
 Copyright (C) 2010
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk 
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under the
 terms of the GNU General Public License version 2 as published by the Free
 Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with this 
 program (see gnu-gplv2.txt). If not, write to the Free Software Foundation, Inc., 
 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or visit the Free Software
 Foundation web page, http://www.fsf.org.

 Created on: 27.07.2010
 *********************************************************************************/

package org.n52.shared.service.rpc;

import org.n52.client.service.SesUserService;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.session.SessionInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * TODO extract admin operations
 */
public interface RpcSesUserServiceAsync {

    /**
     * See {@link SesUserService#registerUser(UserDTO)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#registerUser(UserDTO)
     */
    void registerUser(UserDTO userDTO, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#login(String, String, SessionInfo)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#login(String, String)
     */
    void login(String userName, String password, SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#validateLoginSession(SessionInfo)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#validateLoginSession(SessionInfo)
     */
    void validateLoginSession(SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#createNotLoggedInSession()} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     */
    void createNotLoggedInSession(AsyncCallback<SessionInfo> callback);

    /**
     * See {@link SesUserService#resetPassword(String, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#resetPassword(String, String)
     */
    void resetPassword(String username, String email, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#logout(SessionInfo)} for documentation.
     * 
     * @param callback
     *        a callback for post processing after server finishes.
     * @see SesUserService#logout(SessionInfo)
     */
    void logout(SessionInfo loginSession, AsyncCallback<Void> callback);

    /**
     * See {@link SesUserService#getUser(SessionInfo)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#getUser(SessionInfo)
     */
    void getUser(SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#deleteUser(SessionInfo, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#deleteUser(SessionInfo, String)
     */
    void deleteUser(SessionInfo sessionInfo, String userId, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#deleteUser(SessionInfo, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#deleteUser(SessionInfo, String)
     */
    void updateUser(SessionInfo sessionInfo, UserDTO userDataToUpdate, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#getAllUsers(SessionInfo)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#getAllUsers(SessionInfo)
     */
    void getAllUsers(SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#requestToDeleteProfile(SessionInfo)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#requestToDeleteProfile(SessionInfo)
     */
    void requestToDeleteProfile(SessionInfo sessionInfo, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#getTermsOfUse(String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#getTermsOfUse(String)
     */
    void getTermsOfUse(String language, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#getData()} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#getData()
     */
    void getData(AsyncCallback<SesClientResponse> callback);
}