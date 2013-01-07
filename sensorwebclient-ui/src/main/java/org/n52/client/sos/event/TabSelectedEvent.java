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
package org.n52.client.sos.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.handler.TabSelectedEventHandler;
import org.n52.client.view.gui.elements.interfaces.DataPanelTab;

/**
 * The Class TabSelectedEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class TabSelectedEvent extends FilteredDispatchGwtEvent<TabSelectedEventHandler> {

    /** The TYPE. */
    public static Type<TabSelectedEventHandler> TYPE = new Type<TabSelectedEventHandler>();

    /** The tab. */
    private DataPanelTab tab;

    /**
     * Instantiates a new tab selected event.
     * 
     * @param tab
     *            the tab
     * @param blockedHandlers
     *            the blocked handlers
     */
    public TabSelectedEvent(DataPanelTab tab, TabSelectedEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.tab = tab;
    }

    /**
     * Gets the tab.
     * 
     * @return the tab
     */
    public DataPanelTab getTab() {
        return this.tab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(TabSelectedEventHandler handler) {
        handler.onSelected(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<TabSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

}
