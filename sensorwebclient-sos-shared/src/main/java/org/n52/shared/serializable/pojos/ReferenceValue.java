/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.Random;

import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesValue;
import org.n52.shared.IdGenerator;
import org.n52.shared.MD5HashGenerator;

public class ReferenceValue implements Serializable {

    private static final long serialVersionUID = 3448456992466823855L;
    
    private TimeseriesData values;
    
    private String label;
    
    private Double value;
    private String color;
    private boolean show = false;
    
    @SuppressWarnings("unused")
    private ReferenceValue() {
        // for serialization
        values = new TimeseriesData();
    }
    
    public void addValues(TimeseriesValue... timeseriesValues) {
        values.addValues(timeseriesValues);
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
        values = new TimeseriesData();
        addValues(new TimeseriesValue(0, value));
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
    
    public String getGeneratedGlobalId(String timeseriesId) {
        IdGenerator idGenerator = new MD5HashGenerator("ref_");
        return idGenerator.generate(new String[]{label, timeseriesId});
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
