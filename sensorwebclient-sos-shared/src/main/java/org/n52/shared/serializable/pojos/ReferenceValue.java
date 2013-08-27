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
import java.util.Random;

import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesValue;

public class ReferenceValue implements Serializable {

    private static final long serialVersionUID = 3448456992466823855L;
    
    private TimeseriesData values;
    
    private String timeseriesId;

    private String label;
    
    
    private Double value;
    private String color;
    private boolean show = false;
    
    private ReferenceValue() {
        // for serialization
    }
    
    public void addValues(TimeseriesValue... values) {
        this.values.addValues(values);
    }
    
    
    public TimeseriesValue[] getValues() {
        return values.getValues();
    }
    
    public TimeseriesValue getLastValue() {
        TimeseriesValue[] allValues = getValues();
        return allValues[allValues.length - 1];
    }
    
    /**
     * @deprecated this constructor creates a reference value which is valid forever.
     */
    @Deprecated
    public ReferenceValue(String label, Double value) {
        this.label = label;
        this.value = value;
        Random rand = new Random();
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);

        this.color = "#" + pad(Integer.toHexString(r)) 
                + pad(Integer.toHexString(g)) + pad(Integer.toHexString(b));
    }
    
    private String pad(String in) {

        if (in.length() == 0) {
            return "00"; //$NON-NLS-1$
        }
        if (in.length() == 1) {
            return "0" + in; //$NON-NLS-1$
        }
        return in;

    }

    public String getId() {
        return this.label;
    }


    public void setId(String id) {
        this.label = id;
    }

    /**
     * @deprecated use {@link #getLastValue()} of {@link #getValues()}
     */
    @Deprecated
    public Double getValue() {
        return this.value;
    }

    
    /**
     * @deprecated use {@link #addValues(TimeseriesValue...)}
     */
    @Deprecated
    public void setValue(Double value) {
        this.value = value;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean show() {
        return this.show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

}
