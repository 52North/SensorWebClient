/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
