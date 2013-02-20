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

import java.util.List;

import org.n52.client.service.SesUserService;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.UserDTO;
import org.n52.shared.session.LoginSession;

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
     * See {@link SesUserService#login(String, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#login(String, String)
     */
    void login(String userName, String password, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#resetPassword(String, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#resetPassword(String, String)
     */
    void resetPassword(String username, String email, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#logout(LoginSession)} for documentation.
     * 
     * @param callback
     *        a callback for post processing after server finishes.
     * @see SesUserService#logout(LoginSession)
     */
    void logout(LoginSession loginSession, AsyncCallback<Void> callback);

    /**
     * See {@link SesUserService#getUser(LoginSession)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#getUser(LoginSession)
     */
    void getUser(LoginSession loginSession, AsyncCallback<UserDTO> callback);

    /**
     * See {@link SesUserService#deleteUser(LoginSession, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#deleteUser(LoginSession, String)
     */
    void deleteUser(LoginSession loginSession, String userId, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#deleteUser(LoginSession, String)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#deleteUser(LoginSession, String)
     */
    void updateUser(LoginSession loginSession, UserDTO userDataToUpdate, AsyncCallback<SesClientResponse> callback);

    /**
     * See {@link SesUserService#getAllUsers(LoginSession)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#getAllUsers(LoginSession)
     */
    void getAllUsers(LoginSession loginSession, AsyncCallback<List<UserDTO>> callback);

    /**
     * See {@link SesUserService#requestToDeleteProfile(LoginSession)} for documentation.
     * 
     * @param callback
     *        a callback handling the server response.
     * @see SesUserService#requestToDeleteProfile(LoginSession)
     */
    void requestToDeleteProfile(LoginSession loginSession, AsyncCallback<SesClientResponse> callback);

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