/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ses.ui.rules;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserId;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.CopyEvent;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

// XXX continue refactoring
public class OtherUserRulesListGrid extends ListGrid {
    @Override
    protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {

        if (record != null) {
            String fieldName = this.getFieldName(colNum);

           if (fieldName.equals(EditRulesLayout.EDIT_RULES_COPY)) {
                // Copy button
                IButton copyButton = new IButton(i18n.copy());
                copyButton.setShowDown(false);
                copyButton.setShowRollOver(false);
                copyButton.setHeight(17);
                copyButton.setLayoutAlign(Alignment.CENTER);
                copyButton.setAlign(Alignment.CENTER);
                copyButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        System.out.println("copy " + record.getAttribute("name"));
                        String userID = getLoggedInUserId();
                        EventBus.getMainEventBus().fireEvent(new CopyEvent(userID, record.getAttribute("name")));
                    }
                });
                return copyButton;
            }
        return null;
        }
        return null;
    }
}