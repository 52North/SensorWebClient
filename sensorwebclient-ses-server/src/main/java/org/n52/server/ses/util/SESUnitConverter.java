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


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vast.unit.Unit;
import org.vast.unit.UnitConversion;
import org.vast.unit.UnitConverter;
import org.vast.unit.UnitParser;
import org.vast.unit.UnitParserUCUM;

public class SESUnitConverter implements IUnitConverter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SESUnitConverter.class);


    private UnitParser parser;
    private Map<String, Unit> unitPhenomenonMap;

    /**
     * Instance for converting units using UCUM codes.
     * @param log the global logger.
     */
    public SESUnitConverter() {
        this.parser = new UnitParserUCUM();
        this.unitPhenomenonMap = new HashMap<String, Unit>();
    }

    /* (non-Javadoc)
     * @see org.n52.swe.ses.unitconversion.IUnitConverter#convert(java.lang.String, double)
     */
    public Object[] convert(String unitString, double value) {
        Object[] result = new Object[2];
        result[0] = this.parser.getUnit(unitString).getCompatibleSIUnit();
        result[1] = UnitConversion.getConverter(this.parser.getUnit(unitString),
                (Unit) result[0]).convert(value);
        result[0] = ((Unit) result[0]).getUCUMExpression();
        return result;
    }

    /* (non-Javadoc)
     * @see org.n52.swe.ses.unitconversion.IUnitConverter#convert(java.lang.String, java.lang.String, double)
     */
    public Object[] convert(String unitString, String phenomenon, double value)
    throws Exception {

        Unit unit = this.parser.getUnit(unitString);

        Unit unit2 = this.unitPhenomenonMap.get(phenomenon);
        if (unit2 == null) {
            LOGGER.warn("Phenomenon not registered. Should not happen, sensor should have registered phenomenon. Using base units for conversion.");
            unit2 = unit.getCompatibleSIUnit();
        }

        if (!unit.isCompatible(unit2)) {
            throw new Exception("The registered SI unit for this phenomenon is not" +
            "compatible with the given unit.");
        }
        Object[] result = new Object[2];
        UnitConverter converter = UnitConversion.getConverter(unit, unit2);

        if (converter == null) {
            throw new Exception("A problem occured converting values.");
        }
        result[0] = unit;
        result[1] = converter.convert(value);
        converter = null;

        return result; 
    }

    /* (non-Javadoc)
     * @see org.n52.swe.ses.unitconversion.IUnitConverter#registerNewUnit(java.lang.String, java.lang.String)
     */
    public boolean registerNewUnit(String unitString, String phenomenon)
    throws Exception {

        Unit unit = this.parser.getUnit(unitString).getCompatibleSIUnit();
        if (this.unitPhenomenonMap.containsKey(phenomenon)) {
            if (!this.unitPhenomenonMap.get(phenomenon).isCompatible(unit)) {
                throw new Exception("Phenomenon already registered, but registered" +
                " units are not compatible!");
            }
            //return false: phenomnen already registered with comparable unit
            return false;
        }
        this.unitPhenomenonMap.put(phenomenon, unit);
        //return true: new phenomenon registered
        return true;
    }

    /* (non-Javadoc)
     * @see org.n52.swe.ses.unitconversion.IUnitConverter#isCompatibleWithPhenomenon(java.lang.String, java.lang.String)
     */
    public boolean isCompatibleWithPhenomenon(String unitString, String phenomenon) {
        Unit unit = this.parser.getUnit(unitString).getCompatibleSIUnit();
        Unit unit2 = this.unitPhenomenonMap.get(phenomenon);
        if (unit2 != null) {
            return unit2.isCompatible(unit);
        }
        //return true: no phenomenon registered - continue
        return true;
    }



}
