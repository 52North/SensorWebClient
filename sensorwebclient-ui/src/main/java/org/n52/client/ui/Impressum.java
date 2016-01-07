/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class Impressum extends Window {
	
	private static final String IMPRESSUM_TITLE = i18n.Impressum();
	public static final String CONTENT_PATH = i18n.imprintPath();

	public Impressum() {
		setTitle(IMPRESSUM_TITLE);
        setWidth(500);
        setHeight(500);
        centerInPage();
        setIsModal(true);
        Layout layout = new VLayout();
        HTMLPane htmlContentPane = new HTMLPane();
        htmlContentPane.setContentsURL(CONTENT_PATH);
        layout.addMember(htmlContentPane);
        addItem(layout);
	}
}
