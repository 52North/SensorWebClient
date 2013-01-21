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

package org.n52.client.view.gui.elements.layouts;

import org.junit.Test.None;

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