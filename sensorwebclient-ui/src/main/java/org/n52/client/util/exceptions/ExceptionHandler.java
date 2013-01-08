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
package org.n52.client.util.exceptions;

import org.n52.client.view.gui.widgets.Toaster;
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
        Toaster toaster = Toaster.getInstance();
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
        Toaster t = Toaster.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected Exception occured. Msg: \"");
        sb.append(e.getMessage()).append("\"");
        sb.append(", CausedBy: ");
        sb.append(e.getCause());
        t.addErrorMessage(sb.toString());
        if (!GWT.isProdMode()) GWT.log(sb.toString(), e);
    }

}
