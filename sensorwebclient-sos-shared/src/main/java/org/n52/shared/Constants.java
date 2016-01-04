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

    public static final String DISPLAY_PROJECTION = EPSG_4326;
    
    public static final String SES_OP_SEPARATOR = "-X-";
}
