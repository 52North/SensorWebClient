package org.n52.client.ses.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.ses.event.handler.SessionExpiredEventHandler;

public class SessionExpiredEvent extends FilteredDispatchGwtEvent<SessionExpiredEventHandler> {

	
	public static Type<SessionExpiredEventHandler> TYPE = new Type<SessionExpiredEventHandler>();
	
	public SessionExpiredEvent(SessionExpiredEventHandler... blockedHandlers) {
		super(blockedHandlers);
	}

	@Override
	public Type<SessionExpiredEventHandler> getAssociatedType() {
		return TYPE;
	}


	@Override
	protected void onDispatch(SessionExpiredEventHandler handler) {
		handler.onSessionExpired(this);
	}

}
