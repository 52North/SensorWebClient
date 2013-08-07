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

package org.n52.io.v0;

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
