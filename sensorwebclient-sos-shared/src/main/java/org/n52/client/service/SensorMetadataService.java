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

package org.n52.client.service;

import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public interface SensorMetadataService {

    /**
     * Assembles metadata for given timeseries' properties object. Metadata has to be set within the passed
     * object which is set and returned via {@link SensorMetadataResponse}. This is needed to keep reference
     * infos set by the client.
     * 
     * @param properties
     *        the timeseries' properties to fill. Holds reference information important for the client later
     *        on.
     * @return the sensor metadata response containing the assembled metadata of the timeseries.
     * @throws Exception
     *         if assembling metdata fails for some reason.
     */
    public SensorMetadataResponse getSensorMetadata(final TimeseriesProperties properties) throws Exception;

    public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(final SosTimeseries timeseries) throws Exception;

    public SOSMetadataResponse getUpdatedSOSMetadata();

}
