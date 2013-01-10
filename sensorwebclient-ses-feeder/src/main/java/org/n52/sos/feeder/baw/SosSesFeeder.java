/****************************************************************************
 * Copyright (C) 2010
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
 * 
 * Author: Jan Schulte
 * Created: 17.05.2010
 *****************************************************************************/
package org.n52.sos.feeder.baw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.n52.sos.feeder.baw.hibernate.InitSessionFactory;
import org.n52.sos.feeder.baw.task.DescriptionTask;
import org.n52.sos.feeder.baw.task.ObservationsTask;
import org.n52.sos.feeder.baw.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Servlet SosSesFeeder.
 *
 * @author Jan Schulte
 */
@SuppressWarnings("serial")
public class SosSesFeeder extends HttpServlet {

    private ObservationsTask obsTask;
    
    private DescriptionTask descTask;
    
    /** The message factory. */
    private MessageFactory messageFactory;

    /** The Constant CONFIG_FILE. */
    private static final String CONFIG_FILE = Strings.getString("Servlet.configFile");

    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(SosSesFeeder.class);

    /** The request handler. */
    private RequestHandler reqHandler;
    
    public static boolean active = true;
    
    /**
     * List of currently feeding sensors (used to prohibit double feeding)
     * <br>
     * We use ONLY the <b>procedureId</b> of the sensor!
     * <br> 
     * TODO make config parameter of initial capacity and capacity increment
     */
    private static Vector<String> CURRENTLY_FEEDED_SENSORS = new Vector<String>();
    
    private Timer timer = new Timer("Feeder-Timer");

    /**
     * Init the Servlet.
     *
     * @param servletConfig the servlet config
     * @throws ServletException the servlet exception
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        String error;

        // initialize Logger
        log.info("init()");

        // initialize configurations
        String configFile = this.getInitParameter(CONFIG_FILE);
        ServletContext context = this.getServletContext();
        InputStream configIS = context.getResourceAsStream(configFile);
        if (configIS == null) {
            error = "could not load config file: " + configFile;
            log.error(error);
            throw new UnavailableException(error);
        }
        try {
            Configuration.instance(configIS);
        } catch (IOException e) {
            error = "could not load config file: " + configFile;
            log.error(error);
            throw new UnavailableException(error);
        }

        // initialize messageFactory
        try {
            this.messageFactory = MessageFactory.newInstance();
        } catch (SOAPException e) {
            log.error("Error while initialize message factory: " + e.getMessage());
        }

        // initialize requestHandler
        this.reqHandler = new RequestHandler();

        // start description timer task
        this.obsTask = new ObservationsTask(CURRENTLY_FEEDED_SENSORS);
        this.descTask = new DescriptionTask();
        
        this.timer.schedule(this.descTask, 1, Configuration.getInstance().getCapTime());
        this.timer.schedule(this.obsTask, Configuration.getInstance().getObsTime(), Configuration.getInstance().getObsTime());
    }

    /* (non-Javadoc)
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        try {
        	// FIXME should be removed during code review before next release
            log.info("Hotfix 52N 2011-07-15 12:00");
            active = false;
            // stop descTask
            this.descTask.cancel();
            // stop obsTask
            this.obsTask.stopObservationFeeds();
            // wait until ObsTask is finished
            while (this.descTask.isActive() || this.obsTask.isActive()) {
                log.debug("DescriptionTask is active : " + descTask.isActive());
                log.debug("ObservationTask is active : " + obsTask.isActive());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                	log.error("Error during destroy of SosSesFeeder servlet: " + e1.getLocalizedMessage(),e1);
                    e1.printStackTrace();
                }
            } 
            // stop timer
            this.timer.cancel();
            
            InitSessionFactory.getInstance().close();

            // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    log.debug(String.format("deregistering jdbc driver: %s", driver));
                } catch (SQLException e) {
                    log.error(String.format("Error deregistering driver %s", driver), e);
                }
            }
            log.info("########## Feeder complete stopped ##########");
        }
        catch (IllegalStateException e) {
            log.debug("Connection already closed, because of shutdown process ...", e);
        }
        super.destroy();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Get all the headers from the HTTP request
            MimeHeaders headers = getHeaders(req);

            // Get the body of the HTTP request
            InputStream body = req.getInputStream();

            // Now internalize the contents of the HTTP request and create a
            // SOAPMessage
            SOAPMessage msg = this.messageFactory.createMessage(headers, body);

            SOAPMessage response = null;
            response = this.reqHandler.getResponse(msg);

            if (response != null) {
                if (response.saveRequired()) {
                    response.saveChanges();
                }

                resp.setStatus(HttpServletResponse.SC_OK);
                putHeaders(response.getMimeHeaders(), resp);

                // Write out the message on the response stream
                OutputStream os = resp.getOutputStream();
                response.writeTo(os);
                os.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (SOAPException e) {
            log.error("Error while parsing SOAPRequest: " + e.getMessage());
        }
    }

    /**
     * Put headers to the response.
     *
     * @param headers the headers
     * @param res the response
     */
    private void putHeaders(MimeHeaders headers, HttpServletResponse res) {

        Iterator<?> it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader) it.next();

            String[] values = headers.getHeader(header.getName());
            if (values.length == 1) {
                res.setHeader(header.getName(), header.getValue());
            } else {
                StringBuffer concat = new StringBuffer();
                int i = 0;
                while (i < values.length) {
                    if (i != 0) {
                        concat.append(',');
                    }
                    concat.append(values[i++]);
                }
                res.setHeader(header.getName(), concat.toString());
            }
        }
    }

    /**
     * Gets the headers.
     *
     * @param req the req
     * @return the headers
     */
    private MimeHeaders getHeaders(HttpServletRequest req) {

        Enumeration<?> headerNames = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();

        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            String headerValue = req.getHeader(headerName);

            StringTokenizer values = new StringTokenizer(headerValue, ",");
            while (values.hasMoreTokens()) {
                headers.addHeader(headerName, values.nextToken().trim());
            }
        }
        return headers;
    }
    
    

}
