
package org.n52.server.ses.feeder;

import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_CAPABILITIES_TASK_PERIOD;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_MAXIMUM_NUMBER_PROCEDURES;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_NODATAS;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_OBSERVATIONS_TASK_PERIOD;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_ONLY_YOUNGEST_OBSERVATION;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_PROCEDURE_NAME_CONSTRAINTS;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_PROHIBIT_PROCEDURE_NAMES;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_DEFAULT_TOPIC;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_DEFAULT_TOPIC_DIALECT;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_ENDPOINT;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_LIFETIME_DURATION;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SOS_VERSION;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_START_TIMESTAMP;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_UPDATE_OBSERVATION_PERIOD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.avalon.framework.availability.UnavailableException;
import org.apache.fop.fo.expr.PropertyException;
import org.n52.server.oxf.util.properties.GeneralizationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class handles all configurations for the feeder (Singleton).
 * 
 * @author Jan Schulte
 */
public class FeederConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeederConfig.class);
    
    private static FeederConfig instance;
    
    private static final String CONFIG_FILE = "/feeder-properties.xml";

    private Properties props;

    private int updateInterval;

    /** The procedure name constraints list. */
    private List<String> procedureNameConstraints;

    /** The prohibit procedure names list. */
    private List<String> prohibitProcedureNames;

    /** The no data value list in the SOSs. */
    private List<String> noDatas;

    /** The time in millis for a capabilities task period */
    private long capTime;

    /** The time in millis for a observation task period */
    private long obsTime;

    private boolean onlyYoungestName;

    private String sosVersion;

    private int maxNumProc;

    private String sesDefaultTopicDialect;

    private String sesDefaultTopic;

    private String sesLifetimeDuration;

    private String sesEndpoint;

    private int startTimestamp;

    /**
     * @param is
     *        InputStream for the configuration file of the servlet.
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws PropertyException 
     */
    private FeederConfig() {
    	LOGGER.debug("Initialize " + getClass().getName());
    	loadProperties();

        // Capabilities Task Period
        try {
            this.capTime = Long.parseLong(getValue(KEY_CAPABILITIES_TASK_PERIOD)) * 1000;
        }
        catch (NumberFormatException e) {
            this.capTime = 86400000;
            LOGGER.warn("Error while parsing " + KEY_CAPABILITIES_TASK_PERIOD + ". Defaultvalue is now " + this.capTime);
        }

        // Observation Task Period
        try {
            this.obsTime = Long.parseLong(getValue(KEY_OBSERVATIONS_TASK_PERIOD)) * 1000;
        }
        catch (NumberFormatException e) {
            this.obsTime = 60000;
            LOGGER.warn("Error while parsing " + KEY_OBSERVATIONS_TASK_PERIOD + ". Defaultvalue is now " + this.obsTime);
        }

        // SOS Version
        this.sosVersion = getValue(KEY_SOS_VERSION);
        if (this.sosVersion == null) {
            this.sosVersion = "1.0.0";
            LOGGER.warn("Missing Parameter: " + KEY_SOS_VERSION + ". Default value is now " + this.sosVersion);
        }

        // update Observation period
        try {
            this.updateInterval = Integer.parseInt(getValue(KEY_UPDATE_OBSERVATION_PERIOD)) * 1000;
        }
        catch (NumberFormatException e) {
            this.updateInterval = 120000;
            LOGGER.warn("Error while parsing " + KEY_UPDATE_OBSERVATION_PERIOD + ". Defaultvalue is now "
                    + this.updateInterval);
        }

        // maximum number of procedures
        try {
            this.maxNumProc = Integer.parseInt(getValue(KEY_MAXIMUM_NUMBER_PROCEDURES));
        }
        catch (NumberFormatException e) {
            this.maxNumProc = Integer.MAX_VALUE;
            LOGGER.warn("Error while parsing " + KEY_MAXIMUM_NUMBER_PROCEDURES + ". Defaultvalue is now "
                    + this.maxNumProc);
        }

        // get list of procedure name constraints
        this.procedureNameConstraints = new ArrayList<String>();
        String tmp = getValue(KEY_PROCEDURE_NAME_CONSTRAINTS);
        if (tmp == null) {
            LOGGER.warn("Error while parsing " + KEY_PROCEDURE_NAME_CONSTRAINTS + ". Defaultvalue is now an empty list");
        }
        else if (tmp.contains(",")) {
            for (String constraint : tmp.split(",")) {
                if ( !constraint.isEmpty()) {
                    this.procedureNameConstraints.add(constraint);
                }
            }
        }
        else if ( !tmp.equals("")) {
            this.procedureNameConstraints.add(tmp);
        }

        // get list of no data values
        this.noDatas = new ArrayList<String>();
        tmp = getValue(KEY_NODATAS);
        if (tmp == null) {
            LOGGER.warn("Error while parsing " + KEY_NODATAS + ". Defaultvalue is now an empty list");
        }
        else if (tmp.contains(",")) {
            for (String noData : tmp.split(",")) {
                if ( !noData.isEmpty()) {
                    this.noDatas.add(noData);
                }
            }
        }
        else if ( !tmp.equals("")) {
            this.noDatas.add(tmp);
        }

        // get list of prohibit procedure names
        this.prohibitProcedureNames = new ArrayList<String>();
        tmp = getValue(KEY_PROHIBIT_PROCEDURE_NAMES);
        if (tmp == null) {
            LOGGER.warn("Error while parsing " + KEY_PROHIBIT_PROCEDURE_NAMES + ". Defaultvalue is now an empty list");
        }
        else if (tmp.contains(",")) {
            for (String prohibitProcName : tmp.split(",")) {
                if ( !prohibitProcName.isEmpty()) {
                    this.prohibitProcedureNames.add(prohibitProcName);
                }
            }
        }
        else if ( !tmp.equals("")) {
            this.prohibitProcedureNames.add(tmp);
        }

        // SES default topic dialect
        this.sesDefaultTopicDialect = getValue(KEY_SES_DEFAULT_TOPIC_DIALECT);
        if (this.sesDefaultTopicDialect == null) {
            this.sesDefaultTopicDialect = "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple";
            LOGGER.warn("Missing Parameter: " + KEY_SES_DEFAULT_TOPIC_DIALECT + ". Default value is now "
                    + this.sesDefaultTopicDialect);
        }

        // SES default topic
        this.sesDefaultTopic = getValue(KEY_SES_DEFAULT_TOPIC);
        if (this.sesDefaultTopic == null) {
            this.sesDefaultTopic = "ses:Measurements";
            LOGGER.warn("Missing Parameter: " + KEY_SES_DEFAULT_TOPIC + ". Default value is now "
                    + this.sesDefaultTopic);
        }

        // SES lifetime duration
        this.sesLifetimeDuration = getValue(KEY_SES_LIFETIME_DURATION);
        if (this.sesLifetimeDuration == null) {
            this.sesLifetimeDuration = "2999-12-31T23:59:59+00:00";
            LOGGER.warn("Missing Parameter: " + KEY_SES_LIFETIME_DURATION + ". Default value is now "
                    + this.sesLifetimeDuration);
        }

        // SES endpoint
        this.sesEndpoint = getValue(KEY_SES_ENDPOINT);
        if (this.sesEndpoint == null) {
            this.sesEndpoint = "http://localhost:8080/";
            LOGGER.warn("Missing Parameter: " + KEY_SES_ENDPOINT + ". Default value is now " + this.sesEndpoint);
        }

        // start timestamp
        try {
            this.startTimestamp = Integer.parseInt(getValue(KEY_START_TIMESTAMP)) * 1000;
        }
        catch (NumberFormatException e) {
            this.startTimestamp = 120000;
            LOGGER.warn("Error while parsing " + KEY_UPDATE_OBSERVATION_PERIOD + ". Defaultvalue is now "
                    + this.startTimestamp);
        }

        // only youngest observation
        this.onlyYoungestName = Boolean.parseBoolean(getValue(KEY_ONLY_YOUNGEST_OBSERVATION));

        LOGGER.debug("Configuration loaded");
    }

    private void loadProperties() {
		try {
			this.props = new Properties();
			URL classFolder = GeneralizationConfiguration.class.getResource(CONFIG_FILE);
			File configFile = new File(classFolder.toURI());
			this.props.loadFromXML(new FileInputStream(configFile));
		} catch (Exception e) {
			LOGGER.error("Could not find configuration file", e);
		}
	}

	/**
     * Gets the single instance of Configuration.
     * 
     * @return The instance of the Configuration class
     * @throws UnavailableException
     */
    public static FeederConfig getInstance() throws IllegalStateException {
        if (instance == null) {
        	instance = new FeederConfig();
        }
        return instance;
    }

    /**
     * Gets the value of the given key.
     * 
     * @param key
     *        Configuration key
     * @return The value to the given configuration key
     */
    public String getValue(String key) {
        return this.props.getProperty(key);
    }

    /**
     * Gets the procedure name constraints.
     * 
     * @return the procedure name constraints.
     */
    public List<String> getProcedureNameConstraints() {
        return this.procedureNameConstraints;
    }

    /**
     * @return the prohibitProcedureNames
     */
    public List<String> getProhibitProcedureNames() {
        return this.prohibitProcedureNames;
    }

    /**
     * @return the capTime
     */
    public long getCapTime() {
        return this.capTime;
    }

    /**
     * @return the minDelay
     */
    public long getObsTime() {
        return this.obsTime;
    }

    /**
     * Gets the no datas.
     * 
     * @return the no data values
     */
    public List<String> getNoDatas() {
        return this.noDatas;
    }

    /**
     * @return the updateInterval
     */
    public long getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * @return the sosVersion
     */
    public String getSosVersion() {
        return this.sosVersion;
    }

    /**
     * @return the maxNumProc
     */
    public int getMaxNumProc() {
        return this.maxNumProc;
    }

    /**
     * @return the sesDefaultTopicDialect
     */
    public String getSesDefaultTopicDialect() {
        return this.sesDefaultTopicDialect;
    }

    /**
     * @return the sesDefaultTopic
     */
    public String getSesDefaultTopic() {
        return this.sesDefaultTopic;
    }

    /**
     * @return the sesLifetimeDuration
     */
    public String getSesLifetimeDuration() {
        return this.sesLifetimeDuration;
    }

    /**
     * @return the sesEndpoint
     */
    public String getSesEndpoint() {
        return this.sesEndpoint;
    }

    /**
     * @return the startTimestamp
     */
    public int getStartTimestamp() {
        return this.startTimestamp;
    }

    /**
     * @return the onlyYoungestName
     */
    public boolean isOnlyYoungestName() {
        return onlyYoungestName;
    }

    class ConfigurationKeys {
        /** Key for the period to start to collect the sensorML documents in milliseconds. */
        static final String KEY_CAPABILITIES_TASK_PERIOD = "capabilities_task_period";

        /** Key for the period to collect the new observations in milliseconds. */
        static final String KEY_OBSERVATIONS_TASK_PERIOD = "observations_task_period";

        /** Key for the supported SOS version. */
        static final String KEY_SOS_VERSION = "sos_version";

        /** Key for the minimum update time of an observation in milliseconds. */
        static final String KEY_UPDATE_OBSERVATION_PERIOD = "update_observation_period";

        /** Key for the maximum number of procedures. */
        static final String KEY_MAXIMUM_NUMBER_PROCEDURES = "maximum_number_procedures";

        /** Key for a list of procedure name constraints. */
        static final String KEY_PROCEDURE_NAME_CONSTRAINTS = "procedure_name_constraints";

        /** Key for a list of prohibit procedure names. */
        static final String KEY_PROHIBIT_PROCEDURE_NAMES = "prohibit_procedure_names";

        /** Key for a list of no data values. */
        static final String KEY_NODATAS = "nodatas";

        /** Key for the supported SES version. */
        static final String KEY_SES_VERSION = "ses_version";

        /** Key for the SES url. */
        static final String KEY_SES_URL = "ses_url";

        /** Key for the basic port type path of the SES. */
        static final String KEY_SES_BASIC_PORT_TYPE_PATH = "ses_basic_port_type_path";

        /** Key for the default topic dialect in the SES requests. */
        static final String KEY_SES_DEFAULT_TOPIC_DIALECT = "ses_default_topic_dialect";

        /** Key for the default topic in the SES request. */
        static final String KEY_SES_DEFAULT_TOPIC = "ses_default_topic";

        /** Key for the lifetime duration in the SES. */
        static final String KEY_SES_LIFETIME_DURATION = "ses_register_publisher_lifetime";

        /** Key for the SES endpoint. */
        static final String KEY_SES_ENDPOINT = "ses_register_publisher_endpoint";

        /** Key for the start timestamp for a feeded sensor */
        static final String KEY_START_TIMESTAMP = "start_timestamp";

        /** Key for the youngest new observation sended to the ses */
        static final String KEY_ONLY_YOUNGEST_OBSERVATION = "only_youngest_observation";

        // static final String KEY_SLEEP_TIME_OBSERVATIONS = "sleep_time_observation";
    }

}
