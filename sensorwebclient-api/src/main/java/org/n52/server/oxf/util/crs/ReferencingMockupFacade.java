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
package org.n52.server.oxf.util.crs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.shared.serializable.pojos.BoundingBox;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReferencingMockupFacade extends AReferencingFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencingMockupFacade.class);
    
    protected ReferencingMockupFacade() {
        // use static AReferencingFacade.createAReferencingFacade factory method
    }

    public List<Station> getContainingStations(BoundingBox bbox, Collection<Station> stations) {
        LOGGER.warn("Reference mockup assistance active! All stations are added ...");
        return new ArrayList<Station>(stations);
    }

    @Override
    public boolean isStationContainedByBBox(BoundingBox bbox, Station station) {
        LOGGER.warn("Reference mockup assistance active! No spatial filter is aplied.");
        return true;
    }
}
