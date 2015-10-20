/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
