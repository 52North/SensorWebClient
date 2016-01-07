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
package org.n52.server.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.MD5HashGenerator;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public abstract class MetadataToHtml {

    protected String createFileNameWithoutExtension(SosTimeseries timeseries) {
        String serviceUrl = timeseries.getServiceUrl();
        String procedureId = timeseries.getProcedureId();
        String phenomenonId = timeseries.getPhenomenonId();
        MD5HashGenerator generator = new MD5HashGenerator("ts-metadata_");
        return generator.generate(new String[] {phenomenonId, procedureId, serviceUrl});
    }

    /**
     * Creates a link accessible from external clients. Uses the {@link ConfigurationContext#GEN_URL}.
     * 
     * @param filename
     *        the filename.
     * @return the link.
     */
    protected String getExternalURLAsString(String filename) {
        String fileLocation = ConfigurationContext.GEN_URL + "/" + filename;
        try {
            URI filePath = new URI(null, fileLocation, null);
            return filePath.getRawPath();
        }
        catch (URISyntaxException e) {
            String msg = String.format("Could NOT encode '%s' to be used as URL.", fileLocation);
            throw new RuntimeException(msg, e);
        }
    }
}
