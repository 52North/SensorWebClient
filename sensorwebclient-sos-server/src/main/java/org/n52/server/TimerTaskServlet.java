/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.n52.server.oxf.util.timerTasks.GetStationLocationTimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerTaskServlet extends HttpServlet {

	private static final long serialVersionUID = 4666371243844151848L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimerTaskServlet.class);

    private static long STARTUP_DELAY = 30000; // 30 sec.

    private Timer delayedFirstRequest = new Timer();

    private Timer periodicRequestTask = new Timer();

    @Override
    public void init() throws ServletException {
        LOGGER.debug("Initialize " + getClass().getName() +" Servlet for SOS Client");
        scheduleFirstGetStationPositionRequest();
        schedulePeriodicGetStationLocationRequest();
    }

	private void scheduleFirstGetStationPositionRequest() {
		try {
            Integer parameter = new Integer(getServletContext().getInitParameter("STARTUP_DELAY"));
			STARTUP_DELAY = parameter != null ? parameter : STARTUP_DELAY;
            delayedFirstRequest.schedule(new GetStationLocationTimerTask(), STARTUP_DELAY);
        } catch (Exception e) {
            LOGGER.trace("Could not read init parameter", e);
        }
	}
	
	private void schedulePeriodicGetStationLocationRequest() {
		Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        Date nextMorning = calendar.getTime();
        long everyDay = 1000 * 60 * 60 * 24;
		GetStationLocationTimerTask task = new GetStationLocationTimerTask();
		periodicRequestTask.scheduleAtFixedRate(task, nextMorning, everyDay);
	}
	
    @Override
    public void destroy() {
        delayedFirstRequest.cancel();
        periodicRequestTask.cancel();
    }
}
