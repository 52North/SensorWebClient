
package org.n52.server.ses.service;

import static java.util.UUID.randomUUID;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.n52.shared.serializable.pojos.User;
import org.n52.shared.session.LoginSession;
import org.n52.shared.session.LoginSessionBuilder;
import org.n52.shared.session.LoginSessionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginSessionStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginSessionStore.class);

    private Map<String, LoginSession> sessionToLoggedInUserName = new HashMap<String, LoginSession>();

    public LoginSession createNewLoginSessionFor(User user) {
        final LoginSession loginSession = LoginSessionBuilder
                .aLoginSession().forUser(user.getUserName())
                .withUserId(String.valueOf(user.getId()))
                .withRole(user.getRole().name())
                .withSessionId(randomUUID().toString())
                .build();
        sessionToLoggedInUserName.put(loginSession.getSession(), loginSession);
        scheduleLoginSessionExpiration(loginSession);
        return loginSession;
    }

    public void scheduleLoginSessionExpiration(final LoginSession loginSession) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sessionToLoggedInUserName.remove(loginSession.getSession());
            }
        }, loginSession.getExpiringDate());
    }

    public void removeSession(LoginSession loginSession) {
        sessionToLoggedInUserName.remove(loginSession.getSession());
    }

    /**
     * @param loginSession
     *        the login session to validate.
     * @throws LoginSessionException
     *         if login session is not valid.
     */
    public void validateIncomingLoginSession(LoginSession loginSession) throws LoginSessionException {
        if (hasNoValidLoginSession(loginSession)) {
            LOGGER.info("Invalid login session: {}", loginSession);
            throw new LoginSessionException("Login session is/has become invalid.");
        }
    }

    public boolean hasNoValidLoginSession(LoginSession loginSession) {
        String sessionId = loginSession.getSession();
        return !sessionToLoggedInUserName.containsKey(sessionId);
    }

    /**
     * @param loginSession
     *        the login session.
     * @return the user's id, or an empty string if no session found or has been expired.
     */
    public String getLoggedInUserId(LoginSession loginSession) {
        LoginSession session = sessionToLoggedInUserName.get(loginSession.getSession());
        return session != null ? session.getUserId() : "";
    }

}
