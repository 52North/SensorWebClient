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
import org.n52.client.ses.event.handler.CreateSimpleRuleEventHandler;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.session.SessionInfo;

public class CreateSimpleRuleEvent extends FilteredDispatchGwtEvent<CreateSimpleRuleEventHandler> {

    public static Type<CreateSimpleRuleEventHandler> TYPE = new Type<CreateSimpleRuleEventHandler>();

    private Rule rule;
    
    private boolean edit;
    
    private String oldRuleName;

    private SessionInfo sessionInfo;

    public CreateSimpleRuleEvent(final SessionInfo sessionInfo, Rule rule, boolean edit, String oldRuleName, CreateSimpleRuleEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.rule = rule;
        this.edit = edit;
        this.oldRuleName = oldRuleName;
        this.sessionInfo = sessionInfo;
    }

    @Override
    protected void onDispatch(CreateSimpleRuleEventHandler handler) {
        handler.onCreate(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateSimpleRuleEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Rule getRule() {
        return this.rule;
    }

    public boolean isEdit() {
        return this.edit;
    }

    public String getOldRuleName() {
        return this.oldRuleName;
    }

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }
    
}