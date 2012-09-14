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

import java.io.Serializable;
import java.util.HashMap;

// TODO apply builder pattern
public class GetPhenomenonResponse implements Serializable {

    private static final long serialVersionUID = 2394382186092276388L;

    private String sosURL;

    private String offID;

    private HashMap<String, String> phenomenons;

    private String procID;

    @SuppressWarnings("unused")
    private GetPhenomenonResponse() {
        // serializable for GWT needs empty default constructor
    }

    public GetPhenomenonResponse(String sosURL, String offID,
            HashMap<String, String> phenomenons, String procID) {
        this.sosURL = sosURL;
        this.offID = offID;
        this.phenomenons = phenomenons;
        this.procID = procID;
    }

    public GetPhenomenonResponse(String sosURL, HashMap<String, String> phenomenons) {
        this.sosURL = sosURL;
        this.phenomenons = phenomenons;
    }

    public String getSosURL() {
        return this.sosURL;
    }

    public String getOffID() {
        return this.offID;
    }

    public HashMap<String, String> getPhenomenons() {
        return this.phenomenons;
    }

    public String getProcID() {
        return procID;
    }

    public void setProcID(String procID) {
        this.procID = procID;
    }

}
