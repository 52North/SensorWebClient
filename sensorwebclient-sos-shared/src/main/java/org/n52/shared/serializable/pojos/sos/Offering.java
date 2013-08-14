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

package org.n52.shared.serializable.pojos.sos;

import java.io.Serializable;

public class Offering extends TimeseriesParameter implements Serializable {

    private static final long serialVersionUID = -544290033391799572L;

    // TODO store responseFormat as it may vary for each offering

    Offering() {
        // for serialization
    }

    public Offering(String parameterId) {
        super(parameterId);
    }
    
    public String getOfferingId() {
        return getId();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("offeringId: '").append(getOfferingId());
        sb.append("', ").append("label: '").append(getLabel());
        return sb.append("']").toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ( (getOfferingId() == null) ? 0 : getOfferingId().hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Offering other = (Offering) obj;
        if (getOfferingId() == null) {
            if (other.getId() != null)
                return false;
        }
        else if ( !getOfferingId().equals(other.getOfferingId()))
            return false;
        return true;
    }

}
