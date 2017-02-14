/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.service;

import static java.lang.String.valueOf;
import static java.util.UUID.randomUUID;
import static org.n52.shared.serializable.pojos.UserRole.isAdmin;
import static org.n52.shared.session.SessionInfoBuilder.aSessionInfo;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.n52.shared.serializable.pojos.User;
import org.n52.shared.serializable.pojos.UserRole;
import org.n52.shared.session.SessionInfo;
import org.n52.shared.session.SessionInfoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSessionStore {

    static long CLEANUP_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 60 * 6; // 6h

    // TODO extent session store to touch active sessions (expand expiration)

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSessionStore.class);

    private Map<String, SessionInfo> notLoggedInSessions = new ConcurrentHashMap<String, SessionInfo>();

    private Map<String, SessionInfo> loggedInSessions = new ConcurrentHashMap<String, SessionInfo>();

    public ServerSessionStore() {
        scheduleSessionCleanup();
    }

    public void scheduleSessionCleanup() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cleanupExpiredSessions();
                cleanupNotLoggedInSessions();
            }
        }, 1000, CLEANUP_INTERVAL_IN_MILLISECONDS);
    }

    private void cleanupExpiredSessions() {
        for (SessionInfo sessionInfo : loggedInSessions.values()) {
            if (sessionInfo.isExpired()) {
                // TODO implement session touching => extent session
                invalidateLoggedInSession(sessionInfo);
            }
        }
    }

    private void cleanupNotLoggedInSessions() {
        for (SessionInfo sessionInfo : notLoggedInSessions.values()) {
            if (sessionInfo.isExpired()) {
                // sessions will be re-recreated
                removeNotLoggedInSession(sessionInfo);
            }
        }
    }

    /**
     * Creates an active session for the given user. If the given (inactive) session object was not generated
     * by this server instance an exception will be thrown.<br>
     * <br/>
     * Once a new session is created and bound to the user the inactive session becomes obsolete and unknown
     * to the server.
     * 
     * @param user
     *        the user to bind a session with.
     * @param inactiveSession
     *        the inactive (not bound to a user) session.
     * @return an active session object for the given user.
     * @throws Exception
     *         if given session was not generated by this server instance.
     */
    public SessionInfo createLoginSessionFor(User user, SessionInfo inactiveSession) throws Exception {
        return createSessionInfoFor(user, inactiveSession);
    }

    private SessionInfo createSessionInfoFor(User user, SessionInfo inactiveSession) {
        if ( !isKnownInactiveSessionInfo(inactiveSession)) {
            // session is not known at all, so create a new one
            return createNotLoggedInSession();
        }
        notLoggedInSessions.remove(inactiveSession);
        SessionInfo sessionInfo = buildSessionInfo(user);
        LOGGER.debug("create logged-in session: {}", sessionInfo);
        saveLoggedInSession(sessionInfo);
        return sessionInfo;
    }

    public SessionInfo reNewSession(SessionInfo sessionInfo) {
        if ( !isKnownActiveSessionInfo(sessionInfo)) {
            // LOGGER.warn("Session is not known to this server: {}", sessionInfo);
            // throw new Exception("Invalid session. Not active or become invalid.");
            return createNotLoggedInSession();
        }
        SessionInfo becameInvalid = invalidateLoggedInSession(sessionInfo);
        SessionInfo newSessionInfo = buildSessionInfo(becameInvalid);
        LOGGER.debug("re-new logged-in session: {}", newSessionInfo);
        saveLoggedInSession(newSessionInfo);
        return newSessionInfo;
    }

    /**
     * Creates and remembers a session object. Once created the server knows the session and considers it to
     * be a not-logged-in session. A user can be bound to a session object via
     * {@link #createLoginSessionFor(User, SessionInfo)}.
     * 
     * @return a session object which is valid to the given domain.
     */
    public SessionInfo createNotLoggedInSession() {
        SessionInfo notLoggedInSession = aSessionInfo(randomUUID().toString()).build();
        notLoggedInSessions.put(notLoggedInSession.getSession(), notLoggedInSession);
        LOGGER.debug("created new not-logged-in session: {}", notLoggedInSession);
        return notLoggedInSession;
    }

    private SessionInfo buildSessionInfo(User user) {
        return createSessionInfo(user.getUserName(), valueOf(user.getId()), user.getRole().name());
    }

    private SessionInfo buildSessionInfo(SessionInfo sessionInfo) {
        return createSessionInfo(sessionInfo.getUsername(), sessionInfo.getUserId(), sessionInfo.getRole());
    }

    private SessionInfo createSessionInfo(String username, String userId, String role) {
        SessionInfoBuilder builder = aSessionInfo(randomUUID().toString());
        builder.forUser(username);
        builder.withUserId(userId);
        builder.withRole(role);
        return builder.build();
    }

    public boolean isKnownInactiveSessionInfo(SessionInfo sessionInfo) {
        String sessionId = sessionInfo.getSession();
        return sessionId != null && notLoggedInSessions.containsKey(sessionId);
    }

    public SessionInfo invalidateLoggedInSession(SessionInfo sessionInfo) {
        if ( !isKnownActiveSessionInfo(sessionInfo)) {
            return createNotLoggedInSession();
        }
        SessionInfo invalidSessionInfo = getKnownSession(sessionInfo);
        loggedInSessions.remove(sessionInfo.getSession());
        return invalidSessionInfo;
    }

    private void saveLoggedInSession(SessionInfo sessionInfo) {
        loggedInSessions.put(sessionInfo.getSession(), sessionInfo);
    }

    public void removeNotLoggedInSession(SessionInfo sessionInfo) {
        notLoggedInSessions.remove(sessionInfo.getSession());
    }

    /**
     * @param sessionInfo
     *        the session info to validate.
     * @throws Exception
     *         if session info is not valid.
     * @deprecated use {@link #isKnownActiveSessionInfo(SessionInfo)} and react with appropriate response
     */
    @Deprecated
    public void validateSessionInfo(SessionInfo sessionInfo) throws Exception {
        if ( !isKnownActiveSessionInfo(sessionInfo)) {
            LOGGER.info("Invalid login session: {}", sessionInfo);
            throw new Exception("Login session is or has become invalid!");
        }
    }

    /**
     * @param sessionInfo
     *        the session info to check for validity.
     * @return <code>true</code> when session info is known, valid and has not been expired yet,
     *         <code>false</code> otherwise.
     */
    public boolean isKnownActiveSessionInfo(SessionInfo sessionInfo) {
        String sessionid = sessionInfo.getSession();
        return sessionid != null 
                && loggedInSessions.containsKey(sessionid)
                && !loggedInSessions.get(sessionid).isExpired();
    }

    /**
     * Gets the user id of the given session if known by this instance. If session is not known an
     * {@link IllegalArgumentException} is thrown.
     * 
     * @param sessionInfo
     *        the session info.
     * @return the id of the logged in user.
     * @see ServerSessionStore#validateSessionInfo(SessionInfo)
     */
    public String getLoggedInUserId(SessionInfo sessionInfo) {
        return getKnownSession(sessionInfo).getUserId();
    }

    
    public String getLoggedInUserRole(SessionInfo sessionInfo) {
        return getKnownSession(sessionInfo).getRole();
    }
    
    /**
     * @param sessionInfo
     *        the session which should be known.
     * @return the known session.
     * @throws IllegalArgumentException
     *         if the session is not known or has become invalid.
     */
    private SessionInfo getKnownSession(SessionInfo sessionInfo) {
        if ( !loggedInSessions.containsKey(sessionInfo.getSession())) {
            throw new IllegalArgumentException("Unknown or invalid session: " + sessionInfo);
        }
        return loggedInSessions.get(sessionInfo.getSession());
    }

    protected Map<String, SessionInfo> getLoggedInSessions() {
        return loggedInSessions;
    }

    protected Map<String, SessionInfo> getNotLoggedInSessions() {
        return notLoggedInSessions;
    }
}
