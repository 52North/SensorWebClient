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

package org.n52.client.control;

import java.util.Date;

import org.n52.client.view.gui.widgets.Toaster;

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
    
    public static long parseDateStringToMillis(String date) {

        String d = date.split("T")[0];
        String t = date.split("T")[1];
        String year = d.split("-")[0];
        String month = d.split("-")[1];
        String day = d.split("-")[2];
        String hours = t.split(":")[0];
        String minutes = t.split(":")[1];
        String seconds = t.split(":")[2];
        if (seconds.contains("+")) {
            seconds = seconds.split("+")[0];
        }

        // TODO optimize, eg w/ Joda Time, but joda has no gwt port
        Date newDate = new Date();
        newDate.setDate(new Integer(day));
        newDate.setYear(new Integer(year) - 1900);
        newDate.setMonth(new Integer(month) - 1);
        newDate.setHours(new Integer(hours));
        newDate.setMinutes(new Integer(minutes));
        newDate.setSeconds(new Integer(seconds));

        return newDate.getTime();
    }

    /**
     * @param end as millis
     * @param begin as millis
     * @return
     */
    public static boolean zoomTimeFrameValid(long begin, long end) {
        long minTime = 0;
        try {
            minTime = Long.parseLong(PropertiesManager.getInstance().getParameterAsString("minTimeFrameZoom"));
        } catch (Exception e) {
            Toaster.getInstance().addMessage("Problem while reading property entry minTimeFrameZoom");
        }
        if ((end - begin) >= (minTime * 1000 * 60)) {
            return true;
        }
        Toaster.getInstance().addMessage(I18N.sosClient.maxZoomInTime());
        return false;
    }
}
