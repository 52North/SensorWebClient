/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.n52.shared.serializable.pojos.TimeseriesProperties;

public class DesignDescriptionList implements Serializable {

    private static final long serialVersionUID = -2304572574771192746L;
    
    private Map<String, RenderingDesign> designDescriptions;

    private String domainAxisLabel;

    public DesignDescriptionList(String domainAxisLabel) {
        this.designDescriptions = new HashMap<String, RenderingDesign>();
        this.domainAxisLabel = domainAxisLabel;
    }

    public String getDomainAxisLabel() {
        return this.domainAxisLabel;
    }
    
    public Collection<RenderingDesign> getAllDesigns() {
        return designDescriptions.values();
    }

    public RenderingDesign get(String timeseriesId) {
        return designDescriptions.get(timeseriesId);
    }

    public void add(TimeseriesProperties tsProperties, Color color, String lineStyle, int lineWidth, boolean grid) {
        RenderingDesign designDescription = new RenderingDesign(tsProperties, color, lineStyle, lineWidth);
        designDescriptions.put(tsProperties.getTimeseries().getTimeseriesId(), designDescription);
    }
    
    public int size() {
        return designDescriptions.size();
    }

    
}