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
import org.n52.client.ses.event.handler.InformUserEventHandler;
import org.n52.shared.responses.SesClientResponse;

public class InformUserEvent extends FilteredDispatchGwtEvent<InformUserEventHandler> {

    public static Type<InformUserEventHandler> TYPE = new Type<InformUserEventHandler>();

    private SesClientResponse response;

    public InformUserEvent(SesClientResponse response, InformUserEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.response = response;
    }

    @Override
    protected void onDispatch(InformUserEventHandler handler) {
        handler.onInform(this);

    }

    @Override
    public Type<InformUserEventHandler> getAssociatedType() {
        return TYPE;
    }

    public SesClientResponse getResponse() {
        return this.response;
    }
}