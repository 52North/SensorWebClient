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
package org.n52.server.oxf.util.timerTasks;

import java.util.HashSet;
import java.util.TimerTask;

import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.updates.SosMetadataUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetStationLocationTimerTask extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(GetStationLocationTimerTask.class);

    private HashSet<String> sosUrls = new HashSet<String>();

    @Override
    public void run() {
        ConfigurationContext.UPDATE_TASK_RUNNING = true;
        try {
            sosUrls.addAll(ConfigurationContext.getServiceMetadatas().keySet());
            log.info("Get Stations from {} data sources: [{}]", sosUrls.size(), sosUrls);
            SosMetadataUpdate.updateSosServices(sosUrls);
        } catch (Exception e) {
            log.error("An error occured during station loading.", e);
        } finally {
        	log.info("Update process complete.");
            ConfigurationContext.UPDATE_TASK_RUNNING = false;
        }
    }

}
