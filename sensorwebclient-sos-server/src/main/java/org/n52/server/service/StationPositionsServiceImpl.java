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
package org.n52.server.service;

import java.util.ArrayList;

import org.n52.client.service.StationPositionsService;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.server.updates.SosMetadataUpdate;
import org.n52.shared.exceptions.ServiceOccupiedException;
import org.n52.shared.responses.StationPositionsResponse;
import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StationPositionsServiceImpl implements StationPositionsService {
    
    private static final Logger LOG = LoggerFactory.getLogger(StationPositionsServiceImpl.class);

    @Override
    public StationPositionsResponse getStationPositions(String sosURL, int start, int interval, BoundingBox boundingBox) throws Exception {
        try {
            LOG.debug("Request -> GetStationPositions for " + sosURL + " in Bbox " + boundingBox.toString() );
            if (ConfigurationContext.UPDATE_TASK_RUNNING) {
                LOG.info("Update running, no service available currently.");
                String reason = "Update running, currently no service available, please try again later";
                throw new ServiceOccupiedException(reason);
            }
        
            SOSMetadata metadata = getCachedMetadata(sosURL);
            boolean shallForceXYAxisOrder = metadata.isForceXYAxisOrder();
            AReferencingHelper referencing = createReferenceHelper(shallForceXYAxisOrder);
            
            int endTmp = 0;
            ArrayList<Station> finalStations = new ArrayList<Station>();
            ArrayList<Station> stations = new ArrayList<Station>();
            stations.addAll(metadata.getStations());
            for (int i = start; i < stations.size() && finalStations.size() < interval; i++) {
                Station station = stations.get(i);
                if (referencing.isStationContainedByBBox(boundingBox, station)) {
                    finalStations.add(station);
                }
                endTmp = i + 1;
            }
            String sosUrl = metadata.getId();
            LOG.debug("Extracted " + finalStations.size() + " (" + start + "-" + endTmp + ") stations from " + stations.size());
            boolean finished = isFinished(endTmp, stations);
            return new StationPositionsResponse(sosUrl, finalStations, metadata.getSrs(), finished, start, endTmp);
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }

    }

    private SOSMetadata getCachedMetadata(String url) throws Exception {
        SOSMetadata metadata = ConfigurationContext.getServiceMetadata(url);
        if (!metadata.hasDonePositionRequest()) {
            SosMetadataUpdate.updateService(url);
        }
        return metadata;
    }

    private AReferencingHelper createReferenceHelper(boolean forceXYAxisOrder) {
        if (forceXYAxisOrder) {
            return AReferencingHelper.createEpsgForcedXYAxisOrder();
        } else {
            return AReferencingHelper.createEpsgStrictAxisOrder();
        }
    }

    boolean isFinished(int endTmp, ArrayList<Station> stations) {
        boolean devMode = ConfigurationContext.IS_DEV_MODE;
        return devMode || endTmp >= stations.size();
    }

}
