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

import org.n52.shared.responses.GetFeatureResponse;
import org.n52.shared.responses.GetOfferingResponse;
import org.n52.shared.responses.GetPhenomenonResponse;
import org.n52.shared.responses.GetProcedureResponse;
import org.n52.shared.responses.GetStationResponse;

public interface ServiceMetadataService {

    public GetPhenomenonResponse getPhen4SOS(String sosURL) throws Exception;
    
    public GetProcedureResponse getProcedure(String serviceURL, String procedureID) throws Exception;

    public GetOfferingResponse getOffering(String serviceURL, String offeringID) throws Exception;
    
    public GetFeatureResponse getFeature(String serviceURL, String featureID) throws Exception;
    
    public GetStationResponse getStation(String serviceURL, String offeringID, String procedureID, String phenomenonID, String featureID) throws Exception;

}
