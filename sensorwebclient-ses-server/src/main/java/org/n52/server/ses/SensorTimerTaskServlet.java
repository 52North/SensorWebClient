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
package org.n52.server.ses;

import java.util.Date;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.n52.server.ses.util.DeleteUnregisteredUserTimerTask;

/**
 * The Class SensorTimerTaskServlet. Timer t1 adds new sensors in a n interval
 * from SES to DB .The timer t2 tries to delete all registrations without confirmation.
 * 
 * FIXME refactor! Avoid using Servlet
 */
public class SensorTimerTaskServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    private static Timer t2;

    @Override
    public void init() throws ServletException {
        super.init();

            t2 = new Timer();
            t2.scheduleAtFixedRate(new DeleteUnregisteredUserTimerTask(), new Date(), SesConfig.deleteUserInterval);  
    }
    
    @Override
    public void destroy(){
        t2.cancel();
        t2 = null;
        super.destroy();
    }
}