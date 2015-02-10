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
