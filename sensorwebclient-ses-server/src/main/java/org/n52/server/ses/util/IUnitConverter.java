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

/**
 * 
 * @author Matthes Rieke <m.rieke@uni-muenster.de>
 *
 */
public interface IUnitConverter {

    /**
     * Converts the given value to its base units and returns the new
     * ucum-expression (result[0]) and the converted value (result[1]).
     * Should be called for each subscription.
     * 
     * @param unitString UCUM code
     * @param value numerical value of the property
     * 
     * @return the new ucum-expression (result[0]) and the converted value (result[1])
     * 
     */
    public abstract Object[] convert(String unitString, double value);

    /**
     * Used to convert a value from the given unit to the first
     * registered compatible unit, using UCUM. returns the new
     * ucum-expression (result[0]) and the converted value (result[1]).
     * Should be called for every incoming sensor data.
     * 
     * @param unitString UCUM code of the unit
     * @param phenomenon the phenomenon
     * @param value the numerical value
     * @return the new ucum-expression (result[0]) and the converted value (result[1])
     * @throws Exception 
     */
    public abstract Object[] convert(String unitString, String phenomenon, double value)
    throws Exception;

    /**
     * Used to register a new Unit with a phenomenon. The unit
     * is converted to its base units (SI).
     * Should be called for each PublisherRegistration (new sensor) request.
     * 
     * @param unitString the UCUM code
     * @param phenomenon the phenomenon
     * 
     * @return false If the phenomenon is already registered and is compatible.
     * @throws Exception 
     */
    public abstract boolean registerNewUnit(String unitString, String phenomenon)
    throws Exception;

    /**
     * Used to check if the phenomenon is compatible with a possibly registered one.
     * Should be called for each subscription.
     * @param unitString the UCUM code
     * @param phenomenon the phenomenon
     * 
     * @return true if units are compatible or phenomenon was not registered previously
     * (subscribing should continue normally). false if units are not compatible.
     */
    public abstract boolean isCompatibleWithPhenomenon(String unitString, String phenomenon);
}
