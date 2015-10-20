/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.shared.session;

import static java.lang.System.currentTimeMillis;

import java.io.Serializable;
import java.util.Date;

/**
 * Contains information about a client session. A client has to ask ther server to create a session which is
 * needed to communicate with the server. Session information other than the session id won't be considered by
 * the server. The session id acts as a pointer to the accurate session information hold on the server.
 */
public class SessionInfo implements Serializable {

    private static final long serialVersionUID = 5076704245184520622L;

    public static final String COOKIE_SESSION_ID = "n52_sensorweb_client_sessionId";

    private Date expiringDate;

    private String username;

    private String userId;

    private String role;

    private String session;

    SessionInfo() {
        // for serialization only
    }

    public SessionInfo(String sessionId) {
        this.expiringDate = createExpiringDate();
        this.session = sessionId;
    }

    private Date createExpiringDate() {
        long threeHours = 1000 * 60 * 60 * 3;
        return new Date(currentTimeMillis() + threeHours);
    }

    public boolean isExpired() {
        long delta = expiringDate.getTime() - currentTimeMillis();
        return delta <= 0;
    }

    /**
     * <b>Do not trust items on server side which were sent by the client. Use server's session storage to
     * retrieve accurate infos by checking and using session id.</b>
     * 
     * @return the user's role
     */
    public Date getExpiringDate() {
        return expiringDate;
    }

    /**
     * @param expiresAt
     *        when the session info shall become invalid.
     */
    public void setExpiringDate(Date expiresAt) {
        expiringDate = expiresAt;
    }

    /**
     * <b>Do not trust items on server side which were sent by the client. Use server's session storage to
     * retrieve accurate infos by checking and using session id.</b>
     * 
     * @return the user name
     */
    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    /**
     * <b>Do not trust items on server side which were sent by the client. Use server's session storage to
     * retrieve accurate infos by checking and using session id.</b>
     * 
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }
    
    public boolean hasUserLoginInfo() {
        return userId != null && username != null && role != null;
    }

    /**
     * <b>Do not trust items on server side which were sent by the client. Use server's session storage to
     * retrieve accurate infos by checking and using session id.</b>
     * 
     * @return the user's role
     */
    public String getRole() {
        return role;
    }

    void setRole(String role) {
        this.role = role;
    }

    /**
     * <b>Do not trust items on server side which were sent by the client. Use server's session storage to
     * retrieve accurate infos by checking and using session id.</b>
     * 
     * @return the user's role
     */
    public String getSession() {
        return session;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (expiringDate == null) ? 0 : expiringDate.hashCode());
        result = prime * result + ( (role == null) ? 0 : role.hashCode());
        result = prime * result + ( (session == null) ? 0 : session.hashCode());
        result = prime * result + ( (userId == null) ? 0 : userId.hashCode());
        result = prime * result + ( (username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if ( ! (obj instanceof SessionInfo))
            return false;
        SessionInfo other = (SessionInfo) obj;
        if (expiringDate == null) {
            if (other.expiringDate != null)
                return false;
        }
        else if ( !expiringDate.equals(other.expiringDate))
            return false;
        if (role == null) {
            if (other.role != null)
                return false;
        }
        else if ( !role.equals(other.role))
            return false;
        if (session == null) {
            if (other.session != null)
                return false;
        }
        if (userId == null) {
            if (other.userId != null)
                return false;
        }
        else if ( !userId.equals(other.userId))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        }
        else if ( !username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SessionInfo [ ");
        sb.append("session: ").append(session).append(", ");
        sb.append("expires: ").append(expiringDate).append(", ");
        sb.append("user: ").append(username).append(", ");
        sb.append("role: ").append(role).append(", ");
        sb.append("userId: ").append(userId);
        return sb.append(" ]").toString();
    }

}
