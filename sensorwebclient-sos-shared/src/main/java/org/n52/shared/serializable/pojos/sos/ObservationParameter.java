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

public abstract class ObservationParameter implements Serializable {

    private static final long serialVersionUID = -6244226109934637660L;
    
    protected String parameterId;
    
    protected String label;
    
    public enum EncodeType {
    	PLAIN, HTML//, URLENC
    }
    
    ObservationParameter() {
        // for serialization
    }
    
    ObservationParameter(String parameterId) {
        if (parameterId == null || parameterId.isEmpty()) {
            throw new IllegalArgumentException("parameterId must not be null.");
        }
        this.label = parseLabel(parameterId);
        this.parameterId = parameterId;
    }

    public void setId(String id) {
        this.parameterId = id;
    }

    public String getId() {
        return parameterId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return getLabel(EncodeType.PLAIN);
    }
    
    public String getLabel(EncodeType encodeType){
    	switch(encodeType){
		case HTML:
			return encodeAsHTML(this.label);
		case PLAIN:
		default:
			return this.label;
    	}
    }
    
    public static String encodeAsHTML( String str){
        String retStr = str;
        retStr = retStr.replaceAll("_", " ");
        retStr = retStr.replaceAll("kuerzest", "kürzest");
        retStr = retStr.replaceAll("laengst", "längst");
        retStr = retStr.replaceAll("Leitfaehigkeit", "Leitfähigkeit");
        retStr = retStr.replaceAll("Saettigung", "Sättigung");
        retStr = retStr.replaceAll("Stroemung", "Strömung");
        retStr = retStr.replaceAll("hoechst", "höchst");
        retStr = retStr.replaceAll("Trueb", "Trüb");
//        retStr = retStr.replaceAll("", "");

        return retStr;
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
