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