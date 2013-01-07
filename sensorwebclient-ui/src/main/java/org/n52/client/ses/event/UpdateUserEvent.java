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
import org.n52.client.ses.event.handler.UpdateUserEventHandler;
import org.n52.shared.serializable.pojos.UserDTO;

/**
 * The Class UpdateUserEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class UpdateUserEvent extends FilteredDispatchGwtEvent<UpdateUserEventHandler> {

    /** The TYPE. */
    public static Type<UpdateUserEventHandler> TYPE = new Type<UpdateUserEventHandler>();

    /** The user. */
    private UserDTO user;
    private String userID;

    /**
     * Instantiates a new update user event.
     * 
     * @param u
     *            the u
     * @param userID 
     * @param blockedHandlers
     *            the blocked handlers
     */
    public UpdateUserEvent(UserDTO u, String userID, UpdateUserEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.user = u;
        this.userID = userID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(UpdateUserEventHandler handler) {
        handler.onUpdate(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateUserEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public UserDTO getUser() {
        return this.user;
    }

    /**
     * @return userID
     */
    public String getUserID() {
        return this.userID;
    }
}