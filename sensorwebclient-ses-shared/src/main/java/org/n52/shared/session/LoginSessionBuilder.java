
package org.n52.shared.session;

/**
 * Builder to create a login session for a user and a given session id.<br>
 * <br>
 * It it recommended to create a random session id which can not be guessed easily, e.g. via
 * 
 * <pre>
 * {@code 
 * LoginSession loginSession = LoginSessionBuilder.aLoginSession()
 *                                  .forUser(username)
 *                                  .withUserId(userId)
 *                                  .withRole(role)
 *                                  .withSession(UUID.randomUUID().toString())
 *                                  .build();
 * </pre>
 * 
 * @param user
 *        the user's username to create login cookie for.
 * @param session
 *        a random session id.
 */
public class LoginSessionBuilder {
    private LoginSession loginSession = new LoginSession();

    public static LoginSessionBuilder aLoginSession() {
        return new LoginSessionBuilder();
    }

    public LoginSessionBuilder forUser(String username) {
        loginSession.setUsername(username);
        return this;
    }

    public LoginSessionBuilder withRole(String role) {
        loginSession.setRole(role);
        return this;
    }

    public LoginSessionBuilder withUserId(String userId) {
        loginSession.setUserId(userId);
        return this;
    }

    public LoginSessionBuilder withSessionId(String sessionId) {
        loginSession.setSession(sessionId);
        return this;
    }

    public LoginSession build() {
        return loginSession;
    }
}