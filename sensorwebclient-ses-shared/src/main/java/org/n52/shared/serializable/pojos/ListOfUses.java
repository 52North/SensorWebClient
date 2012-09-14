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

public class ListOfUses implements Serializable {

    private static final long serialVersionUID = -8097986034252402451L;

    /** The id. */
    private int id;

    /** The basis rule. */
    private int basisRule;

    /** The complex rule. */
    private int complexRule;

    /** The user id. */
    private int userId;

    /**
     * Instantiates a new list of uses.
     * 
     * @param basisRule
     *            ID of a basisRule
     * @param complexRule
     *            ID of a complexRule
     * @param userId
     *            ID of a user who subscribe one of the rules
     */
    public ListOfUses(int basisRule, int complexRule, int userId) {
        this.basisRule = basisRule;
        this.complexRule = complexRule;
        this.userId = userId;
    }

    /**
     * Instantiates a new list of uses.
     */
    public ListOfUses() {
        // basic constructor is needed
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the basis rule.
     * 
     * @return the basis rule
     */
    public int getBasisRule() {
        return this.basisRule;
    }

    /**
     * Sets the basis rule.
     * 
     * @param basisRule
     *            the new basis rule
     */
    public void setBasisRule(int basisRule) {
        this.basisRule = basisRule;
    }

    /**
     * Gets the complex rule.
     * 
     * @return the complex rule
     */
    public int getComplexRule() {
        return this.complexRule;
    }

    /**
     * Sets the complex rule.
     * 
     * @param complexRule
     *            the new complex rule
     */
    public void setComplexRule(int complexRule) {
        this.complexRule = complexRule;
    }

    /**
     * Gets the user id.
     * 
     * @return the user id
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Sets the user id.
     * 
     * @param userId
     *            the new user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
