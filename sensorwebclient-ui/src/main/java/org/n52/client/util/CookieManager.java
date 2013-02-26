
package org.n52.client.util;

import static com.google.gwt.user.client.Cookies.getCookie;
import static com.google.gwt.user.client.Cookies.removeCookie;
import static com.google.gwt.user.client.Cookies.setCookie;
import static org.n52.shared.session.LoginSession.COOKIE_SESSION_ID;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ID;
import static org.n52.shared.session.LoginSession.COOKIE_USER_NAME;
import static org.n52.shared.session.LoginSession.COOKIE_USER_ROLE;

import java.util.Date;

import org.n52.shared.session.LoginSession;
import org.n52.shared.session.LoginSessionBuilder;

public class CookieManager {

    /**
     * @return a login session object containing the active session id (if present).
     */
    public static LoginSession getCurrentLoginSession() {
        return LoginSessionBuilder.aLoginSession()
                .forUser(getCookie(COOKIE_USER_NAME))
                .withUserId(COOKIE_USER_ID)
                .withRole(getCookie(COOKIE_USER_ROLE))
                .withSessionId(getCookie(COOKIE_SESSION_ID))
                .build();
    }

    /**
     * Creates cookies from current session status.
     * 
     * @param loginSession
     *        the session status return from server.
     */
    public static void setCookiesFrom(LoginSession loginSession) {
        Date expires = loginSession.getExpiringDate();
        String session = loginSession.getSession();
        String name = loginSession.getUsername();
        String role = loginSession.getRole();
        String userId = loginSession.getUserId();

        setCookie(COOKIE_USER_ID, userId, expires, null, "/", false);
        setCookie(COOKIE_USER_ROLE, role, expires, null, "/", false);
        setCookie(COOKIE_USER_NAME, name, expires, null, "/", false);
        setCookie(COOKIE_SESSION_ID, session, expires, null, "/", false);
    }

    /**
     * Removes all login relevant cookies.
     */
    public static void destroyCurrentLoginSession() {
        removeCookie(COOKIE_USER_ID, "/");
        removeCookie(COOKIE_USER_ROLE, "/");
        removeCookie(COOKIE_USER_NAME, "/");
        removeCookie(COOKIE_SESSION_ID, "/");
    }
    
    public static boolean hasActiveLoginSession() {
        return getCookie(COOKIE_SESSION_ID) != null;
    }
    
    public static String getLoggedInUser()  {
        return getCookie(COOKIE_USER_NAME);
    }

    public static String getLoggedInUserRole() {
        return getCookie(COOKIE_USER_ROLE);   
    }

    public static String getLoggedInUserId() {
        return getCookie(COOKIE_USER_ID);   
    }
}
