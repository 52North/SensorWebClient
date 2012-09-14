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
package org.n52.server.ses.eml;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 * 
 * Constants in EML
 *
 */
public class Constants {

    // LOGICAL OPERATORS
    public final static String AND = "AND";
    public final static String OR = "OR";
    public final static String AND_NOT = "AND_NOT";

    // TAG NAMES
    final static String simplePatterns = "SimplePatterns";
    public final static String simplePattern = "SimplePattern";
    final static String complexPatterns = "ComplexPatterns";
    final static String complexPattern = "ComplexPattern";
    final static String selectFunction = "SelectFunction";
    final static String patternReference = "PatternReference";
    final static String logicalOperator = "Logicaloperator";
    final static String message = "Message";
    final static String userParameterValue = "UserParameterValue";
    final static String eventCount = "EventCount";
    final static String fesFilter = "fes:Filter";
    final static String valueReference = "fes:ValueReference";
    final static String fesLiteral = "fes:Literal";
    public final static String propertyValue = "value";
    final static String selectEvent = "SelectEvent";
    final static String duration = "Duration";
    public final static String name = "name";

    // ATTRIBUTE NAMES
    final static String patternID = "patternID";
    final static  String outputName = "outputName";
    final static String newEventName = "newEventName";
    final static String eventName = "eventName";
    
    // TAG VALUES
    public final static String sensorID = "sensorID";
    
    // FORMAT TAGS
    public final static String format = "format";
}
