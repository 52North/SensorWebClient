package org.n52.client.sos.event.data.handler;

import org.n52.client.sos.event.data.UpdateSOSMetadataEvent;

import com.google.gwt.event.shared.EventHandler;


public interface UpdateSOSMetadataEventHandler extends EventHandler {
	
	void onUpdate(UpdateSOSMetadataEvent evt);

}
