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
