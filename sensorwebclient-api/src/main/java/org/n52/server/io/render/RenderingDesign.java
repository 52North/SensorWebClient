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

package org.n52.server.io.render;

import java.awt.Color;
import java.io.Serializable;

import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

/**
 * XXX suspect to be refactored (see {@link DesignOptions}, {@link TimeseriesProperties},
 * {@link TimeseriesRenderingOptions}, etc)
 */
public class RenderingDesign implements Serializable {

    private static final long serialVersionUID = 232118551592451740L;

    private TimeseriesProperties timeseriesProperties;

    private Color color;

    private String lineStyle; // chart type actually

    private int lineWidth;

    public RenderingDesign(TimeseriesProperties properties, Color color, String lineStyle, int lineWidth) {
        SosTimeseries timeseries = properties.getTimeseries();
        if (timeseries == null) {
            throw new NullPointerException("Properties do not contain a timeseries.");
        }
        this.timeseriesProperties = properties;
        this.color = color;
        this.lineStyle = lineStyle;
        this.lineWidth = lineWidth;
    }

    public Color getColor() {
        return this.color;
    }

    public String getLineStyle() {
        return this.lineStyle;
    }

    public int getLineWidth() {
        return this.lineWidth;
    }

    public String getLabel() {
        return timeseriesProperties.getLabel();
    }

    public String getUomLabel() {
        return timeseriesProperties.getUnitOfMeasure();
    }

    public Phenomenon getPhenomenon() {
        return getTimeseries().getPhenomenon();
    }

    public Procedure getProcedure() {
        return getTimeseries().getProcedure();
    }

    public Feature getFeature() {
        return getTimeseries().getFeature();
    }
    
    
    public SosTimeseries getTimeseries() {
        return timeseriesProperties.getTimeseries();
    }

    public void setTimeseriesProperties(TimeseriesProperties timeseriesProperties) {
        this.timeseriesProperties = timeseriesProperties;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (getFeature() == null) ? 0 : getFeature().hashCode());
        result = prime * result + ( (getPhenomenon() == null) ? 0 : getPhenomenon().hashCode());
        result = prime * result + ( (getProcedure() == null) ? 0 : getProcedure().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof RenderingDesign)) {
            return false;
        }
        RenderingDesign other = (RenderingDesign) obj;
        if (getFeature() == null) {
            if (other.getFeature() != null) {
                return false;
            }
        }
        else if ( !getFeature().equals(other.getFeature())) {
            return false;
        }
        if (getPhenomenon() == null) {
            if (other.getPhenomenon() != null) {
                return false;
            }
        }
        else if ( !getPhenomenon().equals(other.getPhenomenon())) {
            return false;
        }
        if (getProcedure() == null) {
            if (other.getProcedure() != null) {
                return false;
            }
        }
        else if ( !getProcedure().equals(other.getProcedure())) {
            return false;
        }
        return true;
    }

}
