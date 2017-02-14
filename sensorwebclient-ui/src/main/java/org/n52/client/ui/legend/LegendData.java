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
package org.n52.client.ui.legend;


/**
 * The Interface DataWrapper.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public interface LegendData {

    /**
     * Gets the iD.
     * 
     * @return the iD
     */
    public String getId();

    /**
     * Gets the ordering.
     * 
     * @return the ordering
     */
    public int getOrdering();

    /**
     * Sets the ordering.
     * 
     * @param ordering
     *            the new ordering
     */
    public void setOrdering(int ordering);

    /**
     * Gets the legend element.
     * 
     * @return the legend element
     */
    public LegendElement getLegendElement();

    /**
     * Sets the legend element.
     * 
     * @param elem
     *            the new legend element
     */
    public void setLegendElement(LegendElement elem);

    /**
     * Checks for data.
     * 
     * @return true, if successful
     */
    public boolean hasData();

}
