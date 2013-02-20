
package org.n52.shared.session;

import static java.lang.System.currentTimeMillis;

import java.io.Serializable;
import java.util.Date;

/**
 * TODO move to a common module
 */
public class LoginSession implements Serializable {

    private static final long serialVersionUID = 5076704245184520622L;

    public static final String COOKIE_USER_ID = "n52_sensorweb_client_userId";

    public static final String COOKIE_USER_ROLE = "n52_sensorweb_client_role";

    public static final String COOKIE_USER_NAME = "n52_sensorweb_client_username";

    public static final String COOKIE_SESSION_ID = "n52_sensorweb_client_sessionId";

    private Date expiringDate;

    private String username;

    private String userId;

    private String role;

    private String session;

    public LoginSession() {
        this.expiringDate = createExpiringDate();
    }

    private Date createExpiringDate() {
        long oneDay = 1000 * 60 * 60 * 24;
        return new Date(currentTimeMillis() + oneDay);
    }
    
    public Date getExpiringDate() {
        return expiringDate;
    }

    public String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    void setRole(String role) {
        this.role = role;
    }

    public String getSession() {
        return session;
    }

    void setSession(String session) {
        this.session = session;
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
        if ( ! (obj instanceof LoginSession))
            return false;
        LoginSession other = (LoginSession) obj;
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
        else if ( !session.equals(other.session))
            return false;
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
        sb.append("LoginCookie [ ");
        sb.append("user: ").append(username).append(", ");
        sb.append("userId: ").append(userId).append(", ");
        sb.append("role: ").append(role).append(", ");
        sb.append("session: ").append(session);
        return sb.append(" ]").toString();
    }

}
