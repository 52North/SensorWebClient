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
package org.n52.shared.responses;

import java.util.ArrayList;
import java.util.HashMap;

import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;
import org.n52.shared.serializable.pojos.Axis;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;

public class EESDataResponse extends RepresentationResponse {

    private static final long serialVersionUID = -1036816874508351530L;

    private String imageUrl;

    private Bounds plotArea;

    private ImageEntity[] imageEntities;

    private HashMap<String, Axis> tsAxis;

    private String overviewurl;

    private int width;

    private int height;

    private long begin;

    private long end;

    private ArrayList<TimeseriesProperties> propertiesList;

    private EESDataResponse() {
        // for serialization
    }

    public EESDataResponse(String imageUrl, DesignOptions options, Bounds plotArea, ImageEntity[] imageEntities, HashMap<String, Axis> tsAxis) {
        this.imageUrl = imageUrl;
        this.begin = options.getBegin();
        this.end = options.getEnd();
        this.width = options.getWidth();
        this.height = options.getHeight();
        this.propertiesList = options.getProperties();
        this.imageEntities = imageEntities;
        this.plotArea = plotArea;
        this.tsAxis = tsAxis;
    }
    
    public void destroy() {
        this.imageEntities = null;
        this.propertiesList.clear();
        this.propertiesList = null;
        this.tsAxis.clear();
        this.tsAxis = null;
    }

    public ArrayList<TimeseriesProperties> getPropertiesList() {
        return this.propertiesList;
    }
    
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getOverviewUrl() {
        return this.overviewurl;
    }

    public HashMap<String, Axis> getAxis() {
        return this.tsAxis;
    }

    public Bounds getPlotArea() {
        return this.plotArea;
    }

    public ImageEntity[] getImageEntities() {
        return this.imageEntities;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }
    
    public long getBegin() {
        return this.begin;
    }
    
    public long getEnd() {
        return this.end;
    }

}
