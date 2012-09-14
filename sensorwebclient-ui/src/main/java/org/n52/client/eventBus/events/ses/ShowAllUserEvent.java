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
package org.n52.client.eventBus.events.ses;

import java.util.List;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.ses.handler.ShowAllUserEventHandler;
import org.n52.shared.serializable.pojos.UserDTO;

/**
 * The Class ShowAllUserEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class ShowAllUserEvent extends FilteredDispatchGwtEvent<ShowAllUserEventHandler> {

    /** The TYPE. */
    public static Type<ShowAllUserEventHandler> TYPE = new Type<ShowAllUserEventHandler>();

    /** The layout. */
    private List<UserDTO> allUser;

    /**
     * Instantiates a new change layout event.
     * @param allUser 
     * 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public ShowAllUserEvent(List<UserDTO> allUser, ShowAllUserEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.allUser = allUser;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowAllUserEventHandler> getAssociatedType() {
        return TYPE;
    }

    /* (non-Javadoc)
     * @see org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(ShowAllUserEventHandler handler) {
        handler.onShow(this);
    }

    /**
     * @return {@link List}
     */
    public List<UserDTO> getAllUser() {
        return this.allUser;
    }
}