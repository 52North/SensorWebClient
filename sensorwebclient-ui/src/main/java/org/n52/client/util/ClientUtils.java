/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
