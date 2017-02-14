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
