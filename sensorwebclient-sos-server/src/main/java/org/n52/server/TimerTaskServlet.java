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
