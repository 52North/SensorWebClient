/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.server.mgmt.schedule;

import java.util.Date;
import java.util.Set;
import org.joda.time.DateTime;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.mgmt.SosMetadataUpdate;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Henning Bredel <h.bredel@52north.org>
 */
public class CacheUpdateJob extends ScheduledJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheUpdateJob.class);

//    private static final String REWRITE_AT_STARTUP = "rewriteAtStartup";

    private boolean rewriteAtStartup = false;

    @Override
    public JobDetail createJobDetails() {
        return JobBuilder.newJob(CacheUpdateJob.class)
                .withIdentity(getJobName())
                .withDescription(getJobDescription())
//                .usingJobData(REWRITE_AT_STARTUP, rewriteAtStartup)
                .build();
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        TriggerKey triggerKey = context.getTrigger().getKey();
        LOGGER.debug("'{}' (triggered by '{}') executing at {}", jobKey, triggerKey, new DateTime());

        ConfigurationContext.UPDATE_TASK_RUNNING = true;
        try {
            Set<String> sosUrls = ConfigurationContext.getServiceMetadatas().keySet();
            LOGGER.info("Do metadata update for #{} data sources: [{}]", sosUrls.size(), sosUrls);

            if (shallRewriteAtStartup(context)) {
                LOGGER.info("Rewriting cache at startup.");
                SosMetadataUpdate.invalidateCache();
            }
            if (context.getPreviousFireTime() != null) {
                LOGGER.info("Rewriting cache at startup.");
                SosMetadataUpdate.invalidateCache();
            }
            SosMetadataUpdate.updateSosServices(sosUrls);
            if (context.getNextFireTime() != null) {
                LOGGER.info("Next update will be run at {}.", context.getNextFireTime());
            }
        } catch (Exception e) {
            LOGGER.error("An error occured during cache update.", e);
        } finally {
        	LOGGER.info("Update process complete.");
            ConfigurationContext.UPDATE_TASK_RUNNING = false;
        }
    }

    private boolean shallRewriteAtStartup(JobExecutionContext context) {
//        JobDataMap properties = context.getJobDetail().getJobDataMap();
        return context.getPreviousFireTime() == null
//                && properties.containsKey(REWRITE_AT_STARTUP)
//                && properties.getBoolean(REWRITE_AT_STARTUP);
                && rewriteAtStartup;
    }

    public boolean isRewriteAtStartup() {
        return rewriteAtStartup;
    }

    public void setRewriteAtStartup(boolean rewriteAtStartup) {
        this.rewriteAtStartup = rewriteAtStartup;
    }

}
