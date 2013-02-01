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

public interface SesUserService {

    public abstract SesClientResponse registerUser(UserDTO userDTO) throws Exception;

    public abstract SesClientResponse login(String userName, String password, boolean isAdminLogin) throws Exception;

    public abstract SesClientResponse newPassword(String userName, String email) throws Exception;

    public abstract void logout() throws Exception;

    public abstract UserDTO getUser(String id) throws Exception;

    public abstract SesClientResponse deleteUser(String id) throws Exception;

    public abstract SesClientResponse updateUser(UserDTO newUser, String userID) throws Exception;

    public abstract List<UserDTO> getAllUsers() throws Exception;

    public abstract SesClientResponse deleteProfile(String id) throws Exception;

    public abstract SesClientResponse getTermsOfUse(String language) throws Exception;

    public abstract SesClientResponse getData() throws Exception;

}