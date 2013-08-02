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

package org.n52.client.ses.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.ses.event.handler.PublishRuleEventHandler;
import org.n52.shared.session.SessionInfo;

public class PublishRuleEvent extends FilteredDispatchGwtEvent<PublishRuleEventHandler> {

    public static Type<PublishRuleEventHandler> TYPE = new Type<PublishRuleEventHandler>();

    private String ruleName;

    private boolean published;

    private String role;

    private SessionInfo sessionInfo;

    public PublishRuleEvent(final SessionInfo sessionInfo, String ruleName, boolean published, String role, PublishRuleEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.ruleName = ruleName;
        this.published = published;
        this.role = role;
        this.sessionInfo = sessionInfo;
    }

    @Override
    protected void onDispatch(PublishRuleEventHandler handler) {
        handler.onPublish(this);

    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PublishRuleEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public boolean isPublished() {
        return this.published;
    }

    public String getRole() {
        return this.role;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
    
}