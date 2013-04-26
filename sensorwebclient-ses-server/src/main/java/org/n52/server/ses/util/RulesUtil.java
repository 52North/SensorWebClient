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
package org.n52.server.ses.util;

import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;

public class RulesUtil {
    
    /**
     * @param oldRule
     * @param newRule
     * @return true if only description and/or the publish status has changed.
     * 
     * If the return value is true, then it is not necessary to resubscribe the rule
     */
    public static boolean changesOnlyInDBBasic(BasicRule oldRule, BasicRule newRule){
        if (oldRule.getEml().equals(newRule.getEml())
                && (!oldRule.getDescription().equals(newRule.getDescription()) || oldRule.isPublished() != newRule.isPublished())) {
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @param oldRule
     * @param newRule
     * @return true if only description and/or the publish status has changed.
     * 
     * If the return value is true, then it is not necessary to resubscribe the rule
     */
    public static boolean changesOnlyInDBComplex(ComplexRule oldRule, ComplexRule newRule){
        if (oldRule.getEml().equals(newRule.getEml())
                && (!oldRule.getDescription().equals(newRule.getDescription()) || oldRule.isPublished() != newRule.isPublished())) {
            return true;
        }
        return false;
    }
}