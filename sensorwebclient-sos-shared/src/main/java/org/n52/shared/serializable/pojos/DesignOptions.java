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

    private String language;

    private String timeParam;

    private boolean grid;

    @SuppressWarnings("unused")
	private DesignOptions() {
        // do nothin
    }

    public DesignOptions(ArrayList<TimeseriesProperties> props, long begin, long end, String timeParam, boolean grid) {
        this(props, begin, end, grid);
        this.timeParam = timeParam;
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
            String phenomenon = p.getPhenomenon().getId();
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
        return this.timeParam;
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
