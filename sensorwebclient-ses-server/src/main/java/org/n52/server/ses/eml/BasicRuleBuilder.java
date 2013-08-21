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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.opengis.eml.x001.AbstractPatternType;
import net.opengis.eml.x001.ComplexPatternDocument.ComplexPattern;
import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EventAttributeType;
import net.opengis.eml.x001.SelectFunctionType;
import net.opengis.eml.x001.SimplePatternType;
import net.opengis.eml.x001.SimplePatternType.PropertyRestrictions;

import org.apache.xmlbeans.XmlException;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.User;

public abstract class BasicRuleBuilder {

    private String templatePath;

    protected BasicRuleBuilder(String templatePath) {
        this.templatePath = templatePath;
    }

    protected EMLDocument getEmlTemplate() throws MalformedURLException, XmlException, IOException {
        URL url = new URL(templatePath);
        return EMLDocument.Factory.parse(url.openStream());
    }

    protected User getUserFrom(Rule rule) {
        return HibernateUtil.getUserBy(rule.getUserID());
    }

    protected void processPropertyRestrictions(SimplePatternType pattern, TimeseriesMetadata metadata) {
        PropertyRestrictions propertyRestrictions = pattern.getPropertyRestrictions();
        EventAttributeType observedProperty = propertyRestrictions.getPropertyRestrictionArray(0);
        XmlUtil.setTextContent(observedProperty.getValue(), metadata.getPhenomenon());
        EventAttributeType sensorId = propertyRestrictions.getPropertyRestrictionArray(1);
        XmlUtil.setTextContent(sensorId.getValue(), metadata.getGlobalSesId());
    }
    
    protected void processSimplePattern(SimplePatternType pattern, String patternName, String eventName) {
        setSelectFunctionName(pattern.getSelectFunctions().getSelectFunctionArray(0), eventName);
        setPatternId(pattern, patternName);
    }

    protected void processComplexPattern(ComplexPattern pattern, String patternName, String eventName) {
        SelectFunctionType selectFunction = getSelectFunction(pattern);
        setSelectFunctionName(selectFunction, eventName);
        setPatternId(pattern, patternName);
    }

    protected void processComplexPattern(ComplexPattern pattern, String patternName, String eventName, String output) {
        SelectFunctionType selectFunction = getSelectFunction(pattern);
        setSelectFunctionName(selectFunction, eventName);
        setPatternId(pattern, patternName);
        setOutputName(selectFunction, output);
    }

    protected void setSelectEventName(AbstractPatternType pattern, String selectEventName) {
        getSelectFunction(pattern).getSelectEvent().setEventName(selectEventName);
    }

    protected void setOutputName(AbstractPatternType pattern, String output) {
        getSelectFunction(pattern).setOutputName(output);
    }

    private SelectFunctionType getSelectFunction(AbstractPatternType pattern) {
        return pattern.getSelectFunctions().getSelectFunctionArray(0);
    }

    private void setSelectFunctionName(SelectFunctionType selectFunction, String eventName) {
        selectFunction.setNewEventName(eventName);
    }

    private void setPatternId(AbstractPatternType pattern, String patternName) {
        pattern.setPatternID(patternName);
    }
    
    private void setOutputName(SelectFunctionType selectFunction, String output) {
        selectFunction.setOutputName(output);
    }

}
