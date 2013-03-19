package org.n52.client.sos.ui;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class SOSClickedHandler implements ClickHandler {
	
	private final StationSelectorController controller;
	
	public SOSClickedHandler(StationSelectorController controller) {
		this.controller = controller;
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO clear filtering 
	}

}
