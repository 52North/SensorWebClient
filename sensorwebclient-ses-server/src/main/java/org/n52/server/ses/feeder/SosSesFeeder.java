package org.n52.server.ses.feeder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.Vector;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.n52.server.ses.feeder.hibernate.InitSessionFactory;
import org.n52.server.ses.feeder.task.DescriptionTask;
import org.n52.server.ses.feeder.task.ObservationsTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosSesFeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosSesFeeder.class);
    
    private static final String CONFIG_FILE = "/feeder-properties.xml";
    
    private ObservationsTask obsTask;
    
    private DescriptionTask descTask;
    
    private MessageFactory messageFactory;

    private RequestHandler reqHandler;
    
    public static boolean active = true;
    
    /**
     * List of currently feeding sensors (used to prohibit double feeding)
     * <br>
     * We use ONLY the <b>procedureId</b> of the sensor!
     * <br> 
     * TODO make config parameter of initial capacity and capacity increment
     * @deprecated we want to register new timeseries to feed by interface
     */
    @Deprecated
    private static Vector<String> CURRENTLY_FEEDED_SENSORS = new Vector<String>();
    
    private Timer timer = new Timer("Feeder-Timer");

    /**
     * @throws IllegalStateException if feeder could not be initialized from config file. 
     */
    public SosSesFeeder() {
        
//        ApplicationContext context = new ClassPathXmlApplicationContext("feeder.xml");
        
        InputStream configIS = getClass().getResourceAsStream(CONFIG_FILE);
        if (configIS == null) {
            throw new IllegalStateException("Missing config file: " + CONFIG_FILE);
        }
        try {
            Configuration.instance(configIS);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config file: " + CONFIG_FILE);
        }

        try {
            this.messageFactory = MessageFactory.newInstance();
        } catch (SOAPException e) {
            LOGGER.error("Error while initialize message factory: " + e.getMessage());
        }

        // initialize requestHandler
        this.reqHandler = new RequestHandler();

        // start description timer task
        this.obsTask = new ObservationsTask(CURRENTLY_FEEDED_SENSORS);
        this.descTask = new DescriptionTask();
    }
    
    public void startFeeding() {
        LOGGER.info("Start feeding registered timeseries to SES.");
        this.timer.schedule(this.descTask, 1, Configuration.getInstance().getCapTime());
        this.timer.schedule(this.obsTask, Configuration.getInstance().getObsTime(), Configuration.getInstance().getObsTime());
    }
    
    @Override
    public void finalize() {
        try {
        	// FIXME should be removed during code review before next release
            LOGGER.info("Hotfix 52N 2011-07-15 12:00");
            active = false;
            // stop descTask
            this.descTask.cancel();
            // stop obsTask
            this.obsTask.stopObservationFeeds();
            // wait until ObsTask is finished
            while (this.descTask.isActive() || this.obsTask.isActive()) {
                LOGGER.debug("DescriptionTask is active : " + descTask.isActive());
                LOGGER.debug("ObservationTask is active : " + obsTask.isActive());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                	LOGGER.error("Error during destroy of SosSesFeeder servlet: " + e1.getLocalizedMessage(),e1);
                    e1.printStackTrace();
                }
            } 
            
            stopFeeding();
            
            InitSessionFactory.getInstance().close();

            // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.debug(String.format("deregistering jdbc driver: %s", driver));
                } catch (SQLException e) {
                    LOGGER.error(String.format("Error deregistering driver %s", driver), e);
                }
            }
            LOGGER.info("########## Feeder complete stopped ##########");
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Connection already closed, because of shutdown process ...", e);
        }
    }

    public void stopFeeding() {
        LOGGER.info("Stop feeding registered timeseries to SES.");
        this.timer.cancel();
    }
    
    // TODO create interface to register new timeseries to feed

//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        try {
//            // Get all the headers from the HTTP request
//            MimeHeaders headers = getHeaders(req);
//
//            // Get the body of the HTTP request
//            InputStream body = req.getInputStream();
//
//            // Now internalize the contents of the HTTP request and create a SOAPMessage
//            SOAPMessage msg = this.messageFactory.createMessage(headers, body);
//
//            SOAPMessage response = null;
//            response = this.reqHandler.getResponse(msg);
//
//            if (response != null) {
//                if (response.saveRequired()) {
//                    response.saveChanges();
//                }
//
//                resp.setStatus(HttpServletResponse.SC_OK);
//                putHeaders(response.getMimeHeaders(), resp);
//
//                // Write out the message on the response stream
//                OutputStream os = resp.getOutputStream();
//                response.writeTo(os);
//                os.flush();
//            } else {
//                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
//            }
//        } catch (SOAPException e) {
//            LOGGER.error("Error while parsing SOAPRequest: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Put headers to the response.
//     *
//     * @param headers the headers
//     * @param res the response
//     */
//    private void putHeaders(MimeHeaders headers, HttpServletResponse res) {
//
//        Iterator<?> it = headers.getAllHeaders();
//        while (it.hasNext()) {
//            MimeHeader header = (MimeHeader) it.next();
//
//            String[] values = headers.getHeader(header.getName());
//            if (values.length == 1) {
//                res.setHeader(header.getName(), header.getValue());
//            } else {
//                StringBuffer concat = new StringBuffer();
//                int i = 0;
//                while (i < values.length) {
//                    if (i != 0) {
//                        concat.append(',');
//                    }
//                    concat.append(values[i++]);
//                }
//                res.setHeader(header.getName(), concat.toString());
//            }
//        }
//    }
//
//    /**
//     * Gets the headers.
//     *
//     * @param req the req
//     * @return the headers
//     */
//    private MimeHeaders getHeaders(HttpServletRequest req) {
//
//        Enumeration<?> headerNames = req.getHeaderNames();
//        MimeHeaders headers = new MimeHeaders();
//
//        while (headerNames.hasMoreElements()) {
//            String headerName = (String) headerNames.nextElement();
//            String headerValue = req.getHeader(headerName);
//
//            StringTokenizer values = new StringTokenizer(headerValue, ",");
//            while (values.hasMoreTokens()) {
//                headers.addHeader(headerName, values.nextToken().trim());
//            }
//        }
//        return headers;
//    }
//    
    

}
