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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.server.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.sos.feature.SOSObservationStore;
import static org.n52.oxf.xmlbeans.tools.XmlFileLoader.loadXmlFileViaClassloader;
import org.n52.server.da.AccessException;
import org.n52.shared.serializable.pojos.DesignOptions;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class GetObservationResponseToOxfFeatureCollectionReader {

    private final OXFFeatureCollection observationCollection;

    public GetObservationResponseToOxfFeatureCollectionReader(String filename) throws IOException, OXFException, XmlException {
        XmlObject response = loadXmlFileViaClassloader(filename, getClass());
        InputStream stream = response.newInputStream();
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell("version", "2.0.0");
        OperationResult result = new OperationResult(stream, container, null);
        SOSObservationStore store = new SOSObservationStore(result);
        observationCollection = store.unmarshalFeatures();
    }

    public OXFFeatureCollection getFeatureCollection() throws AccessException {
        return observationCollection;
    }
}
