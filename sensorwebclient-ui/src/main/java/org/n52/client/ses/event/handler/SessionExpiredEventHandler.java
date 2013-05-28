package org.n52.client.ses.event.handler;

import org.n52.client.ses.event.SessionExpiredEvent;

import com.google.gwt.event.shared.EventHandler;


public interface SessionExpiredEventHandler extends EventHandler {

	void onSessionExpired(SessionExpiredEvent evt);
}
