/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
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
        return timeseriesProperties.getStation().getLabel();
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
