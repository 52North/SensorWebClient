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
package org.n52.client.ui.btn;

import org.n52.client.ui.View;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;


public class SmallButton extends Layout {
	
	private Canvas canvas;
	
	private String tooltip;
	
	private int size = 16;
	
	private int margin = 4;
	
	private String extendedTooltip;
	
	public SmallButton(Canvas canvas, String tooltip, String extendedTooltip) {
		this.canvas = canvas;
		this.tooltip = tooltip;
		this.extendedTooltip = extendedTooltip;
		setStyleName("n52_sensorweb_client_smallicon");
		init();
	}
	
	private void init() {
		this.setWidth(this.size + 1);
		this.setHeight(this.size + 1);
		canvas.setWidth(this.size);
		canvas.setHeight(this.size);
		
		addMember(canvas);
		this.setMargin(this.margin);
		if (View.getView().isShowExtendedTooltip()) {
			this.setTooltip(this.extendedTooltip);
		} else {
			this.setTooltip(this.tooltip);
		}
	}
	
	public Canvas getCanvas() {
		return this.canvas;
	} 

}
