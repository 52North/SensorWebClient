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
