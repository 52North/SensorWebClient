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
/**********************************************************************************
 Copyright (C) 2011
 by 52 North Initiative for Geospatial Open Source Software GmbH

 Contact: Andreas Wytzisk 
 52 North Initiative for Geospatial Open Source Software GmbH
 Martin-Luther-King-Weg 24
 48155 Muenster, Germany
 info@52north.org

 This program is free software; you can redistribute and/or modify it under the
 terms of the GNU General Public License version 2 as published by the Free
 Software Foundation.

 This program is distributed WITHOUT ANY WARRANTY; even without the implied
 WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License along with this 
 program (see gnu-gplv2.txt). If not, write to the Free Software Foundation, Inc., 
 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or visit the Free Software
 Foundation web page, http://www.fsf.org.
 
 Created on: 27.04.2011
 *********************************************************************************/
package org.n52.shared;

import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.EastingNorthing;


public abstract class Constants {

    public static final String SOS_VERSION_200 = "2.0.0";
    
    public static final String SOS_VERSION_100 = "1.0.0";

    public static final String SOS_VERSION_000 = "0.0.0";
    
    public static final String DEFAULT_SOS_VERSION = SOS_VERSION_100;

    public static final String SOS_URLS = "sosUrls";
    
    public static final String DEFAULT_INTERVAL = "defaultInterval";
    
    public static final String DEFAULT_OVERVIEW_INTERVAL = "defaultOverviewInterval";

    public static final int Z_INDEX_ON_TOP = 1000000;
    
    public static final String EPSG_4326 = "EPSG:4326";
    
    public static final String GOOGLE_PROJECTION = "EPSG:900913";

    public static final String DISPLAY_PROJECTION = Constants.EPSG_4326;
    
    /**
     * A fall back extent if no other extent was configured (data source instance, or global).
     */
    public static final BoundingBox FALLBACK_EXTENT = new BoundingBox(new EastingNorthing(-90, -45), new EastingNorthing(90, 45), DISPLAY_PROJECTION);
    
    public static final String SES_OP_SEPARATOR = "-X-";
}
