
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
        return aSessionInfo(getCookie(COOKIE_SESSION_ID)).build();
    }

    /**
     * Sets session information from received session object. The session id is hold as cookie only.<br/>
     * <br/>
     * The {@link ClientSessionManager} listens to logout events to destroy current session information.
     * 
     * @param sessionInfo
     *        the session status return from server.
     */
    public static void setSessionInfo(SessionInfo sessionInfo) {
        Date expires = sessionInfo.getExpiringDate();
        loggedInUser = sessionInfo.getUsername();
        loggedInUserRole = sessionInfo.getRole();
        loggedInUserId = sessionInfo.getUserId();

        // gwt sets domain automatically
        String session = sessionInfo.getSession();
        setCookie(COOKIE_SESSION_ID, session, expires, null, "/", false);
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
