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
package org.n52.client.ctrl;

import org.n52.client.ui.Toaster;
import org.n52.shared.MetaException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * The Class ExceptionHandler.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class ExceptionHandler {

    public static void handleException(MetaException e) {
        logInToaster(e);
    }

    /**
     * Process exception.
     * 
     * @param e
     *            the e
     */
    private static void logInToaster(MetaException e) {
        Toaster toaster = Toaster.getToasterInstance();
        if (toaster == null) {
            Window.alert("Initialization error: no message toaster present!");
            if (!GWT.isProdMode()) {
                GWT.log("INIT ERROR: No message toaster present!");
                GWT.log(e.getMessage(), e);
            }
            return;
        }

        StringBuilder sb = new StringBuilder();
        switch (e.getWeight()) {
        case mild:
            sb.append("MILD: ");
            break;
        case severe:
            sb.append("SEVERE: ");
            break;
        }
        sb.append(e.getMessage());
        sb.append("\ncaused by: ");
        sb.append(e.getCause());
        toaster.addErrorMessage(sb.toString());
        if (!GWT.isProdMode()) GWT.log(e.getMessage(), e);
    }

    public static void handleUnexpectedException(Exception e) {
        Toaster t = Toaster.getToasterInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected Exception occured. Msg: \"");
        sb.append(e.getMessage()).append("\"");
        sb.append(", CausedBy: ");
        sb.append(e.getCause());
        t.addErrorMessage(sb.toString());
        if (!GWT.isProdMode()) GWT.log(sb.toString(), e);
    }

}
