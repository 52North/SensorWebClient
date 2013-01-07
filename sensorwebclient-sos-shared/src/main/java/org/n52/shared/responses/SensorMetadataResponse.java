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

import org.n52.shared.serializable.pojos.TimeSeriesProperties;

public class SensorMetadataResponse implements Serializable {

    private static final long serialVersionUID = -572577336556117796L;
    
    private TimeSeriesProperties props;

    private SensorMetadataResponse() {
        // serializable for GWT needs empty default constructor
    }

    public SensorMetadataResponse(TimeSeriesProperties props) {
        this.props = props;
    }

    public TimeSeriesProperties getProps() {
        return this.props;
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder("Sensormetadata: \n");
        sb.append("\tOffering: ").append(props.getOffering().getId()).append("\n");
        sb.append("\tProcedure: ").append(props.getProcedure().getId()).append("\n");
        sb.append("\tFeatureOfInterest: ").append(props.getFoi().getId()).append("\n");
        sb.append("\tPhenomenon: ").append(props.getPhenomenon().getId()).append("\n");
        sb.append("\tlat: ").append(props.getLat()).append("  lng: ").append(props.getLon());
        return sb.toString();
    }

}
