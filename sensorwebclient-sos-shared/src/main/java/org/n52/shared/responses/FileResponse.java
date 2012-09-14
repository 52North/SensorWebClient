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
package org.n52.shared.responses;

/**
 * The Class FileResponse.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class FileResponse extends RepresentationResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4647012518254029594L;

    /** The url. */
    private String url;

    /**
     * Instantiates a new file response.
     */
    @SuppressWarnings("unused")
    private FileResponse() {
        // do nothin
    }

    /**
     * Instantiates a new file response.
     * 
     * @param url
     *            the url
     */
    public FileResponse(String url) {
        this.url = url;
    }

    /**
     * Gets the uRL.
     * 
     * @return the uRL
     */
    public String getURL() {
        return this.url;
    }

}
