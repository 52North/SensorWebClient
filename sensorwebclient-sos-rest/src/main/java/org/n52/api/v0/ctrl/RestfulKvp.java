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
