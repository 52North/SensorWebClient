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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.shared.serializable.pojos.ReferenceValue;

public class Procedure extends TimeseriesParameter implements Serializable {

    private static final long serialVersionUID = 6941911617808330972L;

    private HashMap<String, ReferenceValue> refvalues = new HashMap<String, ReferenceValue>();
    
    Procedure() {
        // for serialization
    }
    
    public Procedure(String parameterId) {
        super(parameterId);
    }
    
    public String getProcedureId() {
        return getId();
    }

    public Map<String, ReferenceValue> getReferenceValues() {
        return refvalues;
    }
    
    public void addRefValue(ReferenceValue v) {
        this.refvalues.put(v.getID(), v);
    }
    
    public ReferenceValue getRefValue(String s) {
        return this.refvalues.get(s);
    }
    
    public Set<String> getRefValues() {
        return this.refvalues.keySet();
    }

    public void addAllRefValues(HashMap<String, ReferenceValue> refvalues2) {
        this.refvalues.putAll(refvalues2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append(" [").append("procedureId: '").append(getProcedureId());
        sb.append("', ").append("label: '").append(getLabel());
        return sb.append("']").toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getLabel() == null) ? 0 : getLabel().hashCode());
        result = prime * result + ( (getProcedureId() == null) ? 0 : getProcedureId().hashCode());
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
        Procedure other = (Procedure) obj;
        if (getProcedureId() == null) {
            if (other.getId() != null)
                return false;
        }
        else if ( !getProcedureId().equals(other.getProcedureId()))
            return false;
        return true;
    }

}
