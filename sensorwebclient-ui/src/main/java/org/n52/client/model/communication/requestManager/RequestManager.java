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
package org.n52.client.model.communication.requestManager;

import org.n52.client.model.communication.LoaderManager;

import com.google.gwt.core.client.GWT;

public abstract class RequestManager {

    private static int requestCount = 0;

    public void removeRequest(long duration) {
        if (!GWT.isProdMode()) GWT.log("Request took " + duration + "ms");
        if (requestCount > 0) requestCount--;
        LoaderManager.getInstance().removeActiveRequest();
    }

    public void addRequest() {
        requestCount++;
        LoaderManager.getInstance().addActiveRequest();
    }

    public void removeRequest() {
        if (requestCount > 0) requestCount--;
        LoaderManager.getInstance().removeActiveRequest();
    }

    public static boolean hasUnfinishedRequests() {
        return requestCount > 0;
    }

}