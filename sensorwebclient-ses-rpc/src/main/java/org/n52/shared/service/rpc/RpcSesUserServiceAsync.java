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

import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.serializable.pojos.UserDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface RpcSesUserServiceAsync.
 */
public interface RpcSesUserServiceAsync {

    /**
     * Register user.
     * 
     * @param u
     *            the u
     * @param callback
     *            the callback
     */
    void registerUser(UserDTO u, AsyncCallback<SesClientResponse> callback);

    /**
     * Login.
     * 
     * @param name
     *            the name
     * @param password
     *            the password
     * @param callback
     *            the callback
     */
    void login(String name, String password, boolean isAdminLogin, AsyncCallback<SesClientResponse> callback);

    /**
     * New password.
     * 
     * @param name
     *            the name
     * @param email
     *            the email
     * @param callback
     *            the callback
     */
    void newPassword(String name, String email, AsyncCallback<SesClientResponse> callback);

    /**
     * Logout.
     * 
     * @param callback
     *            the callback
     */
    void logout(AsyncCallback<Void> callback);

    /**
     * Gets the user.
     * 
     * @param parameterId
     *            the parameterId
     * @param callback
     *            the callback
     */
    void getUser(String id, AsyncCallback<UserDTO> callback);

    /**
     * Delete user.
     * 
     * @param parameterId
     *            the parameterId
     * @param callback
     *            the callback
     */
    void deleteUser(String id, AsyncCallback<SesClientResponse> callback);

    /**
     * Update user.
     * 
     * @param u
     *            the u
     * @param userID 
     * @param callback
     *            the callback
     */
    void updateUser(UserDTO u, String userID, AsyncCallback<SesClientResponse> callback);

    /**
     * Get all users
     * 
     * @param callback
     */
    void getAllUsers(AsyncCallback<List<UserDTO>> callback);

    /**
     * Delete user profile
     * 
     * @param parameterId
     * @param callback
     */
    void deleteProfile(String id, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param language 
     * @param callback
     */
    void getTermsOfUse(String language, AsyncCallback<SesClientResponse> callback);

    /**
     * 
     * @param callback
     */
    void getData(AsyncCallback<SesClientResponse> callback);
}