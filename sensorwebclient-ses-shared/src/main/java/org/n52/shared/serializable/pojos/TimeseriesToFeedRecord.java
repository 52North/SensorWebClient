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

public class TimeseriesToFeedRecord implements Serializable {

    private static final long serialVersionUID = 4658847230133552081L;
    
    private FeedingMetadata metadata;
    
    private String status;
    
    private String inUse;

    public TimeseriesToFeedRecord() {
        // for serialization
    }

    public TimeseriesToFeedRecord(FeedingMetadata metadata, String status, String inUse) {
        this.metadata = metadata;
        this.status = status;
        this.inUse = inUse;
    }
    
    public FeedingMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(FeedingMetadata metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getInUse() {
        return inUse;
    }
    
    public void setInUse(String inUse) {
        this.inUse = inUse;
    }
}