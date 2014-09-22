/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Stack;

public class Axis implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6782493345168190041L;

    /** The upper bound. */
    private double upperBound;

    /** The lower bound. */
    private double lowerBound;

    /** The max y. */
    private double maxY = 0d;

    /** The min y. */
    private double minY = 0d;

    /** The upper stack. */
    private Stack<Double> upperStack = new Stack<Double>();

    /** The lower stack. */
    private Stack<Double> lowerStack = new Stack<Double>();

    /** The min y stack. */
    private Stack<Double> minYStack = new Stack<Double>();

    /** The max y stack. */
    private Stack<Double> maxYStack = new Stack<Double>();

    /**
     * Instantiates a new axis.
     */
    private Axis() {
        // do nothin
    }

    /**
     * Instantiates a new axis.
     * 
     * @param upperBound
     *            the upper bound
     * @param lowerBound
     *            the lower bound
     */
    public Axis(double upperBound, double lowerBound) {
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.upperStack.add(upperBound);
        this.lowerStack.add(lowerBound);
    }

    /**
     * Gets the upper bound.
     * 
     * @return the upper bound
     */
    public double getUpperBound() {
        try {
            return this.upperStack.peek();
        } catch (Exception e) {
            return this.upperBound;
        }

    }

    /**
     * Sets the upper bound.
     * 
     * @param upperBound
     *            the new upper bound
     */
    public void setUpperBound(double upperBound) {
        this.upperStack.add(upperBound);
    }

    /**
     * Gets the lower bound.
     * 
     * @return the lower bound
     */
    public double getLowerBound() {
        try {
            return this.lowerStack.peek();
        } catch (Exception e) {
            return this.lowerBound;
        }

    }

    /**
     * Sets the lower bound.
     * 
     * @param lowerBound
     *            the new lower bound
     */
    public void setLowerBound(double lowerBound) {
        this.lowerStack.add(lowerBound);
    }

    /**
     * Gets the length.
     * 
     * @return the length
     */
    public double getLength() {
        try {
            return this.maxYStack.peek() - this.minYStack.peek();
        } catch (Exception e) {
            return this.maxY - this.minY;
        }
    }

    /**
     * Gets the max y.
     * 
     * @return the max y
     */
    public double getMaxY() {
        try {
            return this.maxYStack.peek();
        } catch (Exception e) {
            return this.maxY;
        }
    }

    /**
     * Gets the min y.
     * 
     * @return the min y
     */
    public double getMinY() {
        try {
            return this.minYStack.peek();
        } catch (Exception e) {
            return this.minY;
        }
    }

    /**
     * Sets the max y.
     * 
     * @param maxY
     *            the new max y
     */
    public void setMaxY(double maxY) {
        this.maxYStack.add(maxY);
    }

    /**
     * Sets the min y.
     * 
     * @param minY
     *            the new min y
     */
    public void setMinY(double minY) {
        this.minYStack.add(minY);
    }

    /**
     * Pop axis.
     */
    public void popAxis() {
        try {
            if (this.upperStack.size() > 1) {
                this.upperStack.pop();
            }
            if (this.lowerStack.size() > 1) {
                this.lowerStack.pop();
            }
            if (this.maxYStack.size() > 1) {
                this.maxYStack.pop();
            }
            if (this.minYStack.size() > 1) {
                this.minYStack.pop();
            }
        } catch (Exception e) {
            // if its empty do nothing
        }
    }

}
