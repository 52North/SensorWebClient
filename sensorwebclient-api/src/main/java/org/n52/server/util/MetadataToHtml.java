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
