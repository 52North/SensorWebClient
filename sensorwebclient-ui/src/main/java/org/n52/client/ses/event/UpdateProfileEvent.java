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
package org.n52.client.ses.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.ses.event.handler.UpdateProfileEventHandler;
import org.n52.shared.serializable.pojos.UserDTO;

/**
 * The Class UpdateProfileEvent.
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class UpdateProfileEvent extends FilteredDispatchGwtEvent<UpdateProfileEventHandler> {

    /** The TYPE. */
    public static Type<UpdateProfileEventHandler> TYPE = new Type<UpdateProfileEventHandler>();

    /** The user. */
    private UserDTO user;

    /**
     * Instantiates a new update profile event.
     * 
     * @param u
     *            the u
     * @param blockedHandlers
     *            the blocked handlers
     */
    public UpdateProfileEvent(UserDTO u, UpdateProfileEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.user = u;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(UpdateProfileEventHandler handler) {
        handler.onUpdate(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateProfileEventHandler> getAssociatedType() {
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
}
