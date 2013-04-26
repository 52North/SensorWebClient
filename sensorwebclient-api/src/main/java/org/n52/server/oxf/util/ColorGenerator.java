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
package org.n52.server.oxf.util;

public class ColorGenerator {
	
	public static String getComplementaryColor(String rgbColor) {
        String r = rgbColor.substring(0, 2);
        String g = rgbColor.substring(2, 4);
        String b = rgbColor.substring(4, 6);
        int red = 255 - Integer.parseInt(r, 16);
        int green = 255 - Integer.parseInt(g, 16);
        int blue = 255 - Integer.parseInt(b, 16);
        String rinv = Integer.toHexString(red);
        String ginv = Integer.toHexString(green);
        String binv = Integer.toHexString(blue);
        if (rinv.length() == 1)
            rinv = "0" + rinv;
        if (ginv.length() == 1)
            ginv = "0" + ginv;
        if (binv.length() == 1)
            binv = "0" + binv;
        return rinv+ginv+binv; 
	}
}
