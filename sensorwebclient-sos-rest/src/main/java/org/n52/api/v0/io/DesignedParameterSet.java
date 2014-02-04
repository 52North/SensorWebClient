/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.api.v0.io;

import java.util.HashMap;
import java.util.Map;

import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;

/**
 * Represents a parameter object to request data from multiple timeseries.
 */
public class DesignedParameterSet extends ParameterSet {

    private int width = -1;
    
    private int height = -1;

    /**
     * The timeseriesIds of interest.
     */
    private Map<String, TimeseriesRenderingOptions> renderingOptions;

    /**
     * Creates an instance with non-null default values.
     */
    public DesignedParameterSet() {
        renderingOptions = new HashMap<String, TimeseriesRenderingOptions>();
    }

    /**
     * @return the requested width or negative number if no size was set.
     */
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the requested height or negative number if no size was set.
     */
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String[] getTimeseries() {
        return renderingOptions.keySet().toArray(new String[0]);
    }

    public void setRenderingOptions(Map<String, TimeseriesRenderingOptions> renderingOptions) {
        this.renderingOptions = renderingOptions;
    }
    
    public TimeseriesRenderingOptions getTimeseriesRenderingOptions(String timeseriesId) {
        return renderingOptions.get(timeseriesId);
    }
    
    public void addTimeseriesWithRenderingOptions(String timeseriesId, TimeseriesRenderingOptions renderingOptions) {
        this.renderingOptions.put(timeseriesId, renderingOptions);
    }
    
}
