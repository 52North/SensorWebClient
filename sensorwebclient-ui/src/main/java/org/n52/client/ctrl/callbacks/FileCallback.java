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
package org.n52.client.ctrl.callbacks;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.ExceptionHandler;
import org.n52.client.sos.ctrl.SOSRequestManager;
import org.n52.client.sos.event.data.ExportFinishedEvent;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
public class FileCallback extends GetFileCallback {

    public FileCallback(SOSRequestManager requestMgr) {
        super(requestMgr, "File callback could not be handled.");
    }

    public void onSuccess(RepresentationResponse result) {
        removeRequest();
        try {
            FileResponse resp = (FileResponse) result;
            Window.open(GWT.getHostPageBaseURL() + resp.getURL(), "_blank", "width=500,height=500");
            EventBus.getMainEventBus().fireEvent(new ExportFinishedEvent());
        }
        catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
    }

}
