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

import java.util.ArrayList;
import java.util.List;

import org.n52.client.ui.Toaster;
import org.n52.ext.link.sos.PermalinkParameter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;

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
        Toaster.getToasterInstance().addMessage(i18n.maxZoomInTime());
        return false;
    }
    
    public static String[] getDecodedParameters(PermalinkParameter parameter) {
        return getDecodedParameters(parameter.nameLowerCase());
    }
    
    public static String[] getDecodedParameters(String parameter) {
        String value = Window.Location.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            return new String[] {};
        }
        if (value.startsWith("{")) {
            return splitJsonObjects(value);
        }
        else {
            return value.split(",");
        }
    }

    public static String[] splitJsonObjects(String value) {
        int openCurlies = 0;
        List<String> objects = new ArrayList<String>();
        StringBuilder object = new StringBuilder();
        for (int i=0; i < value.length() ; i++) {
            char currentChar = value.charAt(i);
            object.append(currentChar);
            if (currentChar == '{') {
                openCurlies++;
            }
            if (currentChar == '}') {
                openCurlies--;
            }
            if (openCurlies == 0) {
                objects.add(object.toString());
                object = new StringBuilder();
                i++; // skip separating comma
            }
        }
        return objects.toArray(new String[0]);
    }

    
    public static boolean isSesEnabled() {
        return getPropertiesManager().getTabsFromPropertiesFile().contains("SesTab");
    }
}
