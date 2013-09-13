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

import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashIdGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class TimeseriesParameter implements Serializable {

    private static final long serialVersionUID = -6244226109934637660L;
    
    private String parameterId;
    
    private String globalId;
    
    private String label;
    
    TimeseriesParameter() {
        // for serialization
    }
    
    TimeseriesParameter(String parameterId, String[] parametersToGenerateId) {
        if (parameterId == null || parameterId.isEmpty()) {
            throw new IllegalArgumentException("parameterId must not be null.");
        }
        this.label = parseLabel(parameterId);
        this.parameterId = parameterId;
        this.globalId = generateGlobalId(parameterId, parametersToGenerateId); 
    }
    
    protected abstract String getGlobalIdPrefix();
    
    private String generateGlobalId(String id, String[] parametersToGenerateId) {
        IdGenerator idGenerator = new MD5HashIdGenerator(getGlobalIdPrefix());
        return idGenerator.generate(parametersToGenerateId);
    }

    @JsonIgnore
    public String getGlobalId() {
		return globalId;
	}

    protected String getParameterId() {
        return parameterId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
    protected String parseLabel(String parameterId) {
        if (parameterId.startsWith("urn")) {
            return parameterId.substring(parameterId.lastIndexOf(":") + 1);
        } else if (parameterId.startsWith("http")) {
            return parameterId.substring(parameterId.lastIndexOf("/") + 1);
        } else {
            return parameterId;
        }
    }
}
