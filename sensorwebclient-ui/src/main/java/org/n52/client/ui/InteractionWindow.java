/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ui;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class InteractionWindow extends VLayout {
	
	private Label title;
	
	private Layout content;
	
	public InteractionWindow(Layout content) {
		this.setContent(content);
		
		title = new Label();
		title.setWidth100();
		title.setAutoHeight();
		title.setStyleName("n52_sensorweb_client_interactionmenuHeader");
		title.setWrap(false);
		title.hide();
		addMember(title);
		addMember(content);
		
		setStyleName("n52_sensorweb_client_interactionmenu");
	}

	public String getWindowTitle() {
		return title.getContents();
	}

	public void setWindowTitle(String windowTitle) {
		if (windowTitle.isEmpty() || windowTitle == null) {
			title.hide();
		} else {	
			title.setContents(windowTitle);
			title.show();
		}
	}

	public Layout getContent() {
		return content;
	}

	public void setContent(Layout content) {
		this.content = content;
	}
	
}
