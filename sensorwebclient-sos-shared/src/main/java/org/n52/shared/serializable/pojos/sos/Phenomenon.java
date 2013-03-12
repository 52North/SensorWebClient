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

public class Phenomenon extends ObservationParameter implements Serializable {

    private static final long serialVersionUID = 207874913321466876L;

    private String unitOfMeasure;

    private Phenomenon() {
        // for serialization
    }

    public Phenomenon(String phenomenonId) {
        super(phenomenonId);
    }

    public String getUnitOfMeasure() {
        return this.unitOfMeasure;
    }
    
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("phenomenonId: '").append(parameterId);
        sb.append("', ").append("label: '").append(label);
        return sb.append("']").toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (label == null) ? 0 : label.hashCode());
        result = prime * result + ( (parameterId == null) ? 0 : parameterId.hashCode());
        result = prime * result + ( (unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
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
        Phenomenon other = (Phenomenon) obj;
        if (unitOfMeasure == null) {
            if (other.unitOfMeasure != null)
                return false;
        }
        else if ( !unitOfMeasure.equals(other.unitOfMeasure))
            return false;
        return true;
    }

}
