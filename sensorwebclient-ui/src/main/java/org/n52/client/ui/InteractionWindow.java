/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
