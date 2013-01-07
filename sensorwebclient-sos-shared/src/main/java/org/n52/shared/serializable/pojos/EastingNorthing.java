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

public class EastingNorthing implements Serializable {

    private static final long serialVersionUID = 4080241800833286545L;
    
    String easting;
    String northing;
    
    private EastingNorthing() {
        // client requires class to be default instantiable
    }

    public EastingNorthing(double easting, double northing) {
        this.easting = Double.toString(easting);
        this.northing = Double.toString(northing);
    }

    public double getEasting() {
        return Double.parseDouble(easting);
    }

    public double getNorthing() {
        return Double.parseDouble(northing);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (easting == null) ? 0 : easting.hashCode());
        result = prime * result + ( (northing == null) ? 0 : northing.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if ( ! (obj instanceof EastingNorthing))
            return false;
        EastingNorthing other = (EastingNorthing) obj;
        if (easting == null) {
            if (other.easting != null)
                return false;
        }
        else if ( !easting.equals(other.easting))
            return false;
        if (northing == null) {
            if (other.northing != null)
                return false;
        }
        else if ( !northing.equals(other.northing))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [ ");
        sb.append("Easting: ").append(easting).append(", ");
        sb.append("Northing: ").append(northing).append(" ]");
        return sb.toString();
    }

    
}
