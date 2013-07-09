package org.n52.client.sos.event.data;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.UpdateSOSMetadataEventHandler;

public class UpdateSOSMetadataEvent extends FilteredDispatchGwtEvent<UpdateSOSMetadataEventHandler> {

	public static Type<UpdateSOSMetadataEventHandler> TYPE = new Type<UpdateSOSMetadataEventHandler>();

	public UpdateSOSMetadataEvent (UpdateSOSMetadataEventHandler... blockedHandlers) {
		super(blockedHandlers);
	}
	
	@Override
	protected void onDispatch(UpdateSOSMetadataEventHandler handler) {
		handler.onUpdate(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<UpdateSOSMetadataEventHandler> getAssociatedType() {
		return TYPE;
	}
}
