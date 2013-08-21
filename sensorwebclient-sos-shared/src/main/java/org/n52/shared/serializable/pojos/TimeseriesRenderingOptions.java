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

package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.Random;

/**
 * Planned successor to replace {@link TimeseriesProperties} in the future. Currently used to define client
 * side rendering options.
 */
public class TimeseriesRenderingOptions implements Serializable {

    private static final long serialVersionUID = -8863370584243957802L;

    /**
     * Color as 6-digit hex value.
     */
    private String color = getRandomHexColor();

    private int lineWidth = 2; // default

    /**
     * @return a default instance of rendering options and random hex color.
     */
    public static TimeseriesRenderingOptions createDefaultRenderingOptions() {
        return new TimeseriesRenderingOptions();
    }
    
    private static String getRandomHexColor() {
        String redHex = getNextFormattedRandomNumber();
        String yellowHex = getNextFormattedRandomNumber();
        String blueHex = getNextFormattedRandomNumber();
        return "#" + redHex + yellowHex + blueHex;
    }

    private static String getNextFormattedRandomNumber() {
        String randomHex = Integer.toHexString(new Random().nextInt(256));
        if (randomHex.length() == 1) {
            // ensure two digits
            randomHex = "0" + randomHex;
        }
        return randomHex;
    }

    /**
     * @return color as 6-digit hex value.
     */
    public String getColor() {
        return color;
    }

    /**
     * @param hexColor
     *        color as 6-digit hex value.
     */
    public void setColor(String hexColor) {
        this.color = hexColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @return one-line formatted JSON represenation of the set values.
     */
    public String asJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append(withQuotes("lineWidth"));
        sb.append(":").append(lineWidth);
        if (color != null) {
            sb.append(",");
            sb.append(withQuotes("color"));
            sb.append(":").append(withQuotes(color));
        }
        return sb.append("}").toString();
    }
    
    private String withQuotes(String toQuote) {
        return "\"".concat(toQuote).concat("\"");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeseriesRenderingOptions [ ");
        sb.append("hexColor: ").append(color);
        sb.append(", lineWidth: ").append(lineWidth);
        return sb.append(" ]").toString();
    }

}
