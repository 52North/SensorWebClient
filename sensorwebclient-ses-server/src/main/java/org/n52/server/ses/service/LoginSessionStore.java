
package org.n52.server.ses.service;

import static java.util.UUID.randomUUID;
import static org.n52.shared.serializable.pojos.UserRole.isAdmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.session.LoginSession;
import org.n52.shared.session.LoginSessionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginSessionStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSessionStore.class);

    private Map<String, LoginSession> sessionToLoggedInUserName = new HashMap<String, LoginSession>();

    public LoginSession createNewLoginSessionFor(User user) {
        final LoginSession loginSession = LoginSessionBuilder.aLoginSession().forUser(user.getUserName()).withUserId(String.valueOf(user.getId())).withRole(user.getRole().name()).withSessionId(randomUUID().toString()).build();
        sessionToLoggedInUserName.put(loginSession.getSession(), loginSession);
        scheduleLoginSessionExpiration(loginSession);
        return loginSession;
    }

    public void scheduleLoginSessionExpiration(final LoginSession loginSession) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO update expiring date? => cookie has also to be reset on client side
                LoginSessionStore.this.removeSession(loginSession);
            }
        }, loginSession.getExpiringDate());
    }

    public void removeSession(LoginSession loginSession) {
        sessionToLoggedInUserName.remove(loginSession.getSession());
    }

    /**
     * @param loginSession
     *        the login session to validate.
     * @throws Exception
     *         if login session is not valid.
     */
    public void validateIncomingLoginSession(LoginSession loginSession) throws Exception {
        if ( !hasValidLoginSession(loginSession)) {
            LOGGER.info("Invalid login session: {}", loginSession);
            throw new Exception("Login session is/has become invalid: " + loginSession);
        }
    }

    /**
     * @param loginSession
     *        the login session to check for validity.
     * @return <code>true</code> when loginSession is known and valid, <code>false</code> otherwise.
     */
    public boolean hasValidLoginSession(LoginSession loginSession) {
        String sessionId = loginSession.getSession();
        return sessionToLoggedInUserName.containsKey(sessionId);
    }

    /**
     * Gets the user id of the given session if known by this instance. If session is not known an
     * {@link IllegalArgumentException} is thrown.
     * 
     * @param loginSession
     *        the login session.
     * @return the id of the logged in user.
     * @see LoginSessionStore#validateIncomingLoginSession(LoginSession)
     */
    public String getLoggedInUserId(LoginSession loginSession) {
        return getKnownSession(loginSession).getUserId();
    }

    /**
     * Checks if the user of the given login session is of role {@link UserRole#ADMIN}. If session is not
     * known an {@link IllegalArgumentException} is thrown.
     * 
     * @param loginSession
     *        the login session to check.
     * @return <code>true</code> if login session is known and user has admin role, <code>false</code> if
     *         session is known but user has not an admin role.
     * @see LoginSessionStore#validateIncomingLoginSession(LoginSession)
     */
    public boolean isLoggedInAdmin(LoginSession loginSession) {
        return isAdmin(getKnownSession(loginSession).getRole());
    }

    /**
     * @param loginSession
     *        the session which should be known.
     * @return the known session.
     * @throws IllegalArgumentException
     *         if the session is not known or has become invalid.
     */
    private LoginSession getKnownSession(LoginSession loginSession) {
        if ( !sessionToLoggedInUserName.containsKey(loginSession.getSession())) {
            throw new IllegalArgumentException("Unknown or invalid session: " + loginSession);
        }
        return sessionToLoggedInUserName.get(loginSession.getSession());
    }

}
