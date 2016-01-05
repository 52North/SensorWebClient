/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.server.sos.connector.ags;

import static org.n52.oxf.xmlbeans.tools.XmlUtil.getXmlAnyNodeFrom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.opengis.sensorML.x101.ComponentDocument;
import net.opengis.sensorML.x101.ComponentType;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList;
import net.opengis.sensorML.x101.ComponentsDocument.Components.ComponentList.Component;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.SensorDescriptionType;
import net.opengis.swes.x20.SensorDescriptionType.Data;

import org.apache.xmlbeans.XmlException;
import org.n52.server.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SensorNetworkParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorNetworkParser.class);

    private static final Map<String, String> namespaceDeclarations = new HashMap<String, String>();

    static {
        namespaceDeclarations.put("sml", "http://www.opengis.net/sensorML/1.0.1");
    }

    private final XmlHelper xmlHelper = new XmlHelper(namespaceDeclarations);

    /**
     * @param stream
     *        a DescribeSensorResponse stream.
     * @return all parsed components in a map identified by their unique id.
     */
    public Map<String, ComponentType> parseSensorDescriptions(InputStream stream) {
        Map<String, ComponentType> sensorDescriptions = new HashMap<String, ComponentType>();
        ComponentDocument[] components = parseNetworkComponentsFromDescribeSensorResponse(stream);
        for (ComponentDocument componentDocument : components) {
            ComponentType networkComponent = componentDocument.getComponent();
            if (networkComponent.getIdentificationArray().length > 0) {
                Identification identification = networkComponent.getIdentificationArray(0);
                String id = xmlHelper.getUniqueId(identification.getIdentifierList());
                if (id != null) {
                    sensorDescriptions.put(id, networkComponent);
                }
            }
        }
        return sensorDescriptions;
    }

    private ComponentDocument[] parseNetworkComponentsFromDescribeSensorResponse(InputStream stream) {
        List<ComponentDocument> componentDocs = new ArrayList<ComponentDocument>();
        try {
            DescribeSensorResponseDocument responseDoc = DescribeSensorResponseDocument.Factory.parse(stream);
            DescribeSensorResponseType response = responseDoc.getDescribeSensorResponse();
            for (DescribeSensorResponseType.Description description : response.getDescriptionArray()) {
                SensorDescriptionType sensorDescription = description.getSensorDescription();
                Data descriptionContent = sensorDescription.getData();
                SensorMLDocument smlDoc = (SensorMLDocument) getXmlAnyNodeFrom(descriptionContent, "SensorML");
                SystemType system = getSystemFrom(smlDoc);
                if (system.isSetComponents()) {
                    ComponentList components = system.getComponents().getComponentList();
                    for (Component component : components.getComponentArray()) {
                        componentDocs.add(ComponentDocument.Factory.parse(component.getProcess().getDomNode()));
                    }
                }
            }
        }
        catch (XmlException e) {
            LOGGER.error("Could not parse DescribeSensorResponse for procedure.", e);
        }
        catch (IOException e) {
            LOGGER.error("Could not read DescribeSensorResponse for procedure.", e);
        }
        return componentDocs.toArray(new ComponentDocument[0]);
    }

    private SystemType getSystemFrom(SensorMLDocument smlDoc) {
        Member[] members = smlDoc.getSensorML().getMemberArray();
        return (members == null || members.length > 0)
                ? (SystemType) members[0].getProcess()
                : SystemDocument.Factory.newInstance().addNewSystem();

    }
}
