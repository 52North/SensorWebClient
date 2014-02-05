/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.api.v0.ctrl;

/**
 * The {@link RestfulKvp} serves as markup interface, so that each controller instance uses the same KVP
 * parameter names.<br/>
 * <br/>
 * <b>Note:</b> Do not code against this type.
 */
public interface RestfulKvp {

    /**
     * How detailed the output shall be. Possible values are:
     * <ul>
     * <li><code>complete</code></li>
     * <li><code>simple</code></li>
     * </ul>
     */
    static final String KVP_SHOW = "show";

    /**
     * An <code>int</code> value which is the index of the first member of the response page (a.k.a. page
     * offset).
     */
    static final String KVP_OFFSET = "offset";

    /**
     * An <code>int</code> value determining the size of the page to be returned.
     */
    static final String KVP_SIZE = "size";

    /**
    * An <code>int</code> value (provided as {@link String}) which defines the default page offset.
    * 
    * @see #KVP_OFFSET
    */
    static final String KVP_DEFAULT_OFFSET = "-1";
    
    /**
     * An <code>int</code> value (provided as {@link String}) which defines the default page size.
     * 
     * @see #KVP_SIZE
     */
    static final String KVP_DEFAULT_SIZE = "10";
    
    /**
     * Default <code>show</code>-parameter value.
     */
    static final String KVP_DEFAULT_SHOW = "complete";
    
    /**
     * The spatial surrounding, given by a lon/lat ordered EPSG:4326 coordinate and radius.
     */
    static final String KVP_WITHIN = "within";
    
    static final String KVP_FEATURE = "feature";
    
    static final String KVP_OFFERING = "offering";
    
    static final String KVP_PROCEDURE = "procedure";
    
    static final String KVP_PHENOMENON = "phenomenon";
    
    

}
