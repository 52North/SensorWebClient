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

package org.n52.client.util;

import static org.n52.client.ctrl.PropertiesManager.getPropertiesManager;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ui.Toaster;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;

public class ClientUtils {

    private ClientUtils() {
        // provide only static utility methods
    }

    public static String getRandomHexColor() {
        String redHex = getNextFormattedRandomNumber();
        String yellowHex = getNextFormattedRandomNumber();
        String blueHex = getNextFormattedRandomNumber();
        return "#" + redHex + yellowHex + blueHex;
    }

    private static String getNextFormattedRandomNumber() {
        String randomHex = Integer.toHexString(Random.nextInt(256));
        if (randomHex.length() == 1) {
            // ensure two digits
            randomHex = "0" + randomHex;
        }
        return randomHex;
    }

    /**
     * @param begin
     *        in millis
     * @param end
     *        in millis
     * @return if given time range is allowed with respect to <code>minTimeFrameZoom</code> parameter.
     */
    public static boolean isValidTimeFrameForZoomIn(long begin, long end) {
        long minTime = 1;
        String parameter = getPropertiesManager().getParameterAsString("minTimeFrameZoom");
        try {
            minTime = Long.parseLong(parameter);
        }
        catch (Exception e) {
            GWT.log("Could not read property minTimeFrameZoom: " + parameter);
        }
        if ( (end - begin) >= (minTime * 1000 * 60)) {
            return true;
        }
        Toaster.getInstance().addMessage(i18n.maxZoomInTime());
        return false;
    }
}
