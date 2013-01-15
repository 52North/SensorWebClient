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

package org.n52.server.oxf.util.connector;

import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

public abstract class MetadataHandler {
    
    // TODO pull up general methods and technics from extending handlers.

    public abstract SOSMetadataResponse performMetadataCompletion(String sosUrl, String sosVersion) throws Exception;

    /**
     * Creates an {@link AReferencingHelper} according to metadata settings (e.g. if XY axis order shall be
     * enforced during coordinate transformation).
     * 
     * @param metadata
     *        the SOS metadata containing SOS instance configuration.
     */
    protected AReferencingHelper createReferencingHelper(SOSMetadata metadata) {
        if (metadata.isForceXYAxisOrder()) {
            return AReferencingHelper.createEpsgForcedXYAxisOrder();
        }
        else {
            return AReferencingHelper.createEpsgStrictAxisOrder();
        }
    }
    
}
