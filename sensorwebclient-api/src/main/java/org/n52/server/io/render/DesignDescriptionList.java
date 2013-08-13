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