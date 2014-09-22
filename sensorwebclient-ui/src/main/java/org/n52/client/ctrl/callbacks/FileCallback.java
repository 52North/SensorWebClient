/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
