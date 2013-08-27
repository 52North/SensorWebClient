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
package org.n52.client.sos.event;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.handler.ChangeTimeSeriesStyleEventHandler;
import org.n52.shared.serializable.pojos.Scale;

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

    private Scale scale;
    
    /** The style. */
    private String style;

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
     * @deprecated
     */
    public ChangeTimeSeriesStyleEvent(String ID, String hexColor, double opacPerc,
            boolean isZeroScaled, String style, boolean autoScale) {
        this.ID = ID;
        this.hexColor = hexColor;
        this.opacPerc = opacPerc;
        this.scale.setType( autoScale
        	? Scale.Type.AUTO
        	: isZeroScaled
        		? Scale.Type.ZERO
        		: Scale.Type.MANUAL);
        this.style = style;

    }

    /**
     * Instantiates a new change time series style event.
     * 
     * @param ID
     *            the iD
     * @param hexColor
     *            the hex color
     * @param opacPerc
     *            the opac perc
     * @param scaleType
     * @param style
     *            the style
     */
    public ChangeTimeSeriesStyleEvent(String ID, String hexColor, double opacPerc, Scale scale, String style) {
        this.ID = ID;
        this.hexColor = hexColor;
        this.opacPerc = opacPerc;
        this.scale= scale;
        this.style = style;

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
     * @deprecated
     */
    public boolean getAutoScale() {
        return this.scale.isAuto();
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
     * @deprecated
     */
    public boolean isZeroScaled() {
        return this.scale.isZero();
    }

    public Scale getScale() {
        return this.scale;
    }


}
