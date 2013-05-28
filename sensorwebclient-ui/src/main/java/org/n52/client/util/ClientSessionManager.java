
package org.n52.client.util;

import static com.google.gwt.user.client.Cookies.getCookie;
import static com.google.gwt.user.client.Cookies.removeCookie;
import static com.google.gwt.user.client.Cookies.setCookie;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.shared.serializable.pojos.UserRole.LOGOUT;
import static org.n52.shared.session.SessionInfo.COOKIE_SESSION_ID;
import static org.n52.shared.session.SessionInfoBuilder.aSessionInfo;

import java.util.Date;

import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.shared.session.SessionInfo;

public class ClientSessionManager {

    static {
        new ClientSessionManagerEventBroker();
    }
    
    private static String notLoggedInSessionId;

    private static String loggedInUser;

    private static String loggedInUserId;

    private static String loggedInUserRole;

    /**
     * @return a login session object containing the current session id.
     */
    public static SessionInfo currentSession() {
        // we do not trust any information from client, using
        // the session id only as a pointer to accurate infos
        // hold on server side.
        String cookieSessionId = getCookie(COOKIE_SESSION_ID);
        return cookieSessionId == null 
                ? aSessionInfo(notLoggedInSessionId).build() 
                : aSessionInfo(cookieSessionId).build();
    }

    /**
     * Sets session information from received session object. Within the cookie only the session id and
     * expiration date is stored or updated.<br/>
     * <br/>
     * The {@link ClientSessionManager} listens to logout events to destroy current session information.
     * 
     * @param sessionInfo
     *        the session status return from server.
     */
    public static void setSessionInfo(SessionInfo sessionInfo) {
        Date expires = sessionInfo.getExpiringDate();

        // gwt sets domain automatically
        String session = sessionInfo.getSession();
        if (sessionInfo.hasUserLoginInfo()) {
            setCookie(COOKIE_SESSION_ID, session, expires, null, "/", false);
        } else {
            notLoggedInSessionId = session;
        }
    }

    /**
     * @param sessionInfo
     *        the session info containing the user information.
     */
    public static void setUserInfo(SessionInfo sessionInfo) {
        loggedInUser = sessionInfo.getUsername();
        loggedInUserRole = sessionInfo.getRole();
        loggedInUserId = sessionInfo.getUserId();
    }

    /**
     * Removes all login relevant information including session cookie.
     */
    private static void destroyCurrentLoginSession() {
        loggedInUser = null;
        loggedInUserId = null;
        loggedInUserRole = null;
        removeCookie(COOKIE_SESSION_ID, "/");
    }

    /**
     * @return <code>true</code> if a session cookie is present, <code>false</code> otherwise.
     */
    public static boolean isPresentSessionInfo() {
        return getCookie(COOKIE_SESSION_ID) != null;
    }

    public static String getLoggedInUserId() {
        return loggedInUserId;
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static String getLoggedInUserRole() {
        return loggedInUserRole;
    }

    public static boolean isNotLoggedIn() {
        return !isLoggedIn();
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null && loggedInUserId != null && loggedInUserRole != null;
    }
    
    public static boolean isAdminLogin() {
        // TODO check if this check is a security issue
        return "ADMIN".equals(loggedInUserRole);
    }

    public static boolean isUserLogin() {
        // TODO check if this check is a security issue
        return "USER".equals(loggedInUserRole);
    }

    private static class ClientSessionManagerEventBroker implements SetRoleEventHandler {

        ClientSessionManagerEventBroker() {
            getMainEventBus().addHandler(SetRoleEvent.TYPE, this);
        }

        @Override
        public void onChangeRole(SetRoleEvent evt) {
            if (evt.getRole() == LOGOUT) {
                destroyCurrentLoginSession();
            }
        }
    }
}
