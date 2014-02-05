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
package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DesignOptions implements Serializable {
	
	public static final String SOS_PARAM_FIRST = "getFirst";

	public static final String SOS_PARAM_LAST = "latest";

    private static final long serialVersionUID = -3922742599500705640L;

    private ArrayList<TimeseriesProperties> properties; // XXX really necessary to hols these?

    private long begin;

    private long end;

    private int height;

    private int width;

    private boolean grid;

    private String language;

    private String timeExtensionTerm; // getFirst or latest
    
    @SuppressWarnings("unused")
	private DesignOptions() {
        // do nothin
    }
    
    public static DesignOptions createOptionsForGetFirstValue(TimeseriesProperties properties) {
        DesignOptions options = new DesignOptions();
        options.timeExtensionTerm = SOS_PARAM_FIRST;
        options.properties = new ArrayList<TimeseriesProperties>();
        options.properties.add(properties);
        return options;
    }
    
    public static DesignOptions createOptionsForGetLastValue(TimeseriesProperties properties) {
        DesignOptions options = new DesignOptions();
        options.timeExtensionTerm = SOS_PARAM_LAST;
        options.properties = new ArrayList<TimeseriesProperties>();
        options.properties.add(properties);
        return options;
    }

    public DesignOptions(ArrayList<TimeseriesProperties> props, long begin, long end, String timeParam, boolean grid) {
        this(props, begin, end, grid);
        this.timeExtensionTerm = timeParam;
    }

    public DesignOptions(ArrayList<TimeseriesProperties> props, long begin, long end, boolean grid) {
        if (props.isEmpty()) {
            throw new IllegalArgumentException("Cannot create DesignOptions from empty TimeSeriesProperties list.");
        }
        this.properties = props;
        this.begin = begin;
        this.end = end;
        this.grid = grid;
        this.height = props.get(0).getHeight();
        this.width = props.get(0).getWidth();
        this.language = props.get(0).getLanguage();
    }

    public boolean getGrid() {
        return this.grid;
    }

    public String[] getAllPhenomenIds() {
        HashMap<String, String> obs = new HashMap<String, String>();
        for (TimeseriesProperties p : this.properties) {
            String phenomenon = p.getPhenomenon();
            obs.put(phenomenon, phenomenon);
        }
        return obs.values().toArray(new String[obs.size()]);
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public ArrayList<TimeseriesProperties> getProperties() {
        return this.properties;
    }

    public long getBegin() {
        return this.begin;
    }

    public long getEnd() {
        return this.end;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getTimeParam() {
        return this.timeExtensionTerm;
    }

    public void setBegin(long i) {
        this.begin = this.begin - i;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DesignOptions [");
        sb.append("begin: ").append(new Date(begin)).append(", ");
        sb.append("end: ").append(new Date(end)).append(", ");
        sb.append("height: ").append(height).append(", ");
        sb.append("width: ").append(width).append(", ");
        sb.append("language: ").append(language).append("]");
        return sb.toString();
    }
}
