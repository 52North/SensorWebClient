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
import org.n52.client.sos.event.handler.UpdateScaleEventHandler;
import org.n52.shared.serializable.pojos.TimeseriesProperties.ScaleType;

/**
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 *
 */
public class UpdateScaleEvent extends FilteredDispatchGwtEvent<UpdateScaleEventHandler> {
    
    public static Type<UpdateScaleEventHandler> TYPE = new Type<UpdateScaleEventHandler>();
    
    private String phenomenonID;

    private ScaleType scaleType;

    /**
     * Use ScaleType instead of scaleToNull and autoScale
     * @param phenomenID
     * @param scaleToNull
     * @param autoScale
     * @deprecated
     */
    public UpdateScaleEvent(String phenomenID, boolean scaleToNull, boolean autoScale) {
        this.phenomenonID = phenomenID;
        this.scaleType = autoScale 
        		? ScaleType.AUTO 
        		: scaleToNull 
        			? ScaleType.ZERO 
        			: ScaleType.MANUAL;
    }

    /**
     * @param phenomenID
     * @param scaleType
     */
    public UpdateScaleEvent(String phenomenID, ScaleType scaleType) {
        this.phenomenonID = phenomenID;
        this.scaleType = scaleType;
    }

    /* (non-Javadoc)
     * @see org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com.google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(UpdateScaleEventHandler handler) {
        handler.onUpdateScale(this);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateScaleEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the phenomenonID
     */
    public String getPhenomenonID() {
        return this.phenomenonID;
    }

    public ScaleType getScaleType(){
    	return scaleType;
    }
    
    /**
     * @return the scaleToNull
     */
    public boolean isScaleToNull() {
        return this.scaleType==ScaleType.ZERO;
    }

    /**
     * @return the autoScale
     */
    public boolean isAutoScale() {
        return this.scaleType==ScaleType.AUTO;
    }

    /**
     * @return manual scale
     */
    public boolean isManualScale(){
    	return scaleType==ScaleType.MANUAL;
    }
}
