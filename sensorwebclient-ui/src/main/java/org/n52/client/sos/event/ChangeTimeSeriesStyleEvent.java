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
package org.n52.client.sos.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.handler.ChangeTimeSeriesStyleEventHandler;

/**
 * The Class ChangeTimeSeriesStyleEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class ChangeTimeSeriesStyleEvent extends
        FilteredDispatchGwtEvent<ChangeTimeSeriesStyleEventHandler> {

    /** The TYPE. */
    public static Type<ChangeTimeSeriesStyleEventHandler> TYPE =
            new Type<ChangeTimeSeriesStyleEventHandler>();

    /** The ID. */
    private String ID;

    /** The hex color. */
    private String hexColor;

    /** The opac perc. */
    private double opacPerc;

    /** The is zero scaled. */
    private boolean isZeroScaled;

    /** The style. */
    private String style;

    /** The generalize. */
    private boolean autoScale;

    /**
     * Instantiates a new change time series style event.
     * 
     * @param ID
     *            the iD
     * @param hexColor
     *            the hex color
     * @param opacPerc
     *            the opac perc
     * @param isZeroScaled
     *            the is zero scaled
     * @param style
     *            the style
     * @param autoScale
     */
    public ChangeTimeSeriesStyleEvent(String ID, String hexColor, double opacPerc,
            boolean isZeroScaled, String style, boolean autoScale) {
        this.ID = ID;
        this.hexColor = hexColor;
        this.opacPerc = opacPerc;
        this.isZeroScaled = isZeroScaled;
        this.style = style;
        this.autoScale = autoScale;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(ChangeTimeSeriesStyleEventHandler handler) {
        handler.onChange(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ChangeTimeSeriesStyleEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Gets the iD.
     * 
     * @return the iD
     */
    public String getID() {
        return this.ID;
    }

    /**
     * Gets the auto scale.
     * 
     * @return the auto scale
     */
    public boolean getAutoScale() {
        return this.autoScale;
    }

    /**
     * Gets the hex color.
     * 
     * @return the hex color
     */
    public String getHexColor() {
        return this.hexColor;
    }

    /**
     * Gets the opacity percentage.
     * 
     * @return the opacity percentage
     */
    public double getOpacityPercentage() {
        return this.opacPerc;
    }

    /**
     * Gets the style.
     * 
     * @return the style
     */
    public String getStyle() {
        return this.style;
    }

    /**
     * Checks if is zero scaled.
     * 
     * @return the isZeroScaled
     */
    public boolean isZeroScaled() {
        return this.isZeroScaled;
    }

}
