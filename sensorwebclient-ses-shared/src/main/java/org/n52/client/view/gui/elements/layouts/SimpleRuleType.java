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
package org.n52.client.view.gui.elements.layouts;


/**
 * The Enum ruleTypes.
 */
public enum SimpleRuleType {

    TENDENCY_OVER_TIME, TENDENCY_OVER_COUNT, OVER_UNDERSHOOT, SUM_OVER_TIME, SENSOR_LOSS, NONE;

    /**
     * @return the rule's name as lower case.
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    /**
     * @param name
     *        the type's name (case-insensitive).
     * @return the enumeration type matching the given name, or {@link SimpleRuleType#NONE} if no type
     *         matches.
     */
    public static SimpleRuleType getTypeFor(String name) {
        for (SimpleRuleType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return NONE;
    }
}