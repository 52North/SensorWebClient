
package org.n52.server.ses.feeder;

import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_FIRST_UPDATE_INTERVAL_RANGE;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_LAST_CONSIDERED_TIME_INTERVAL;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_NO_DATA_VALUES;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_GETOBSERVATIONS_UPDATE_INTERVAL;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_ONLY_YOUNGEST_OBSERVATION;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_PROCEDURE_NAME_CONSTRAINTS;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_PROHIBIT_PROCEDURE_NAMES;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_DEFAULT_TOPIC;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_DEFAULT_TOPIC_DIALECT;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_ENDPOINT;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_LIFETIME_DURATION;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class handles all configurations for the feeder (Singleton).
 * 
 * @author Jan Schulte
 */
public class FeederConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeederConfig.class);

    private static final String CONFIG_FILE = "/feeder-properties.xml";

    private static final String SES_DEFAULT_TOPIC_DIALECT = "http://docs.oasis-open.org/wsn/t-1/TopicExpression/Simple";

    private static final String SES_DEFAULT_LIFETIME_DURATION = "2999-12-31T23:59:59+00:00";

    private static final String SES_DEFAULT_TOPIC = "ses:Measurements";

    private static FeederConfig instance;

    private Properties configuration;

    private long firstUpdateIntervalRange;

    @Deprecated
    private List<String> procedureNameConstraints;

    private List<String> prohibitProcedureNames;

    private List<String> noDataValues;

    private long elapseTimeOfGetObservationsUpdateInMillis;

    private boolean onlyYoungestName;

    private int maximalNumberOfProcedures;

    private String sesDefaultTopicDialect;

    private String sesDefaultTopic;

    private String sesLifetimeDuration;

    private String sesEndpoint;

    private long firstConsideredTimeIntervalInMillis;

    public static FeederConfig getFeederConfig() {
        if (instance == null) {
            instance = new FeederConfig();
        }
        return instance;
    }

    private FeederConfig() {
        LOGGER.debug("Initialize " + getClass().getName());
        configuration = loadProperties();

        firstConsideredTimeIntervalInMillis = parseLongValue(KEY_LAST_CONSIDERED_TIME_INTERVAL, 120000);
        firstUpdateIntervalRange = parseLongValue(KEY_FIRST_UPDATE_INTERVAL_RANGE, 120000);
        elapseTimeOfGetObservationsUpdateInMillis = parseLongValue(KEY_GETOBSERVATIONS_UPDATE_INTERVAL, 60000);

        noDataValues = parseCommaSeparatedValues(KEY_NO_DATA_VALUES);
        prohibitProcedureNames = parseCommaSeparatedValues(KEY_PROHIBIT_PROCEDURE_NAMES);
        procedureNameConstraints = parseCommaSeparatedValues(KEY_PROCEDURE_NAME_CONSTRAINTS);

        sesEndpoint = parseStringValue(KEY_SES_ENDPOINT, "http://localhost:8080/");
        sesDefaultTopic = parseStringValue(KEY_SES_DEFAULT_TOPIC, SES_DEFAULT_TOPIC);
        sesLifetimeDuration = parseStringValue(KEY_SES_LIFETIME_DURATION, SES_DEFAULT_LIFETIME_DURATION);
        sesDefaultTopicDialect = parseStringValue(KEY_SES_DEFAULT_TOPIC_DIALECT, SES_DEFAULT_TOPIC_DIALECT);

        onlyYoungestName = Boolean.parseBoolean(getValue(KEY_ONLY_YOUNGEST_OBSERVATION));

        LOGGER.info("Feeder configuration has been loaded successfully.");
    }

    private long parseLongValue(String configKey, long defaultValue) {
        try {
            String value = getValue(configKey);
            return Long.parseLong(value.trim());
        }
        catch (NumberFormatException e) {
            LOGGER.warn("Could not parse setting for '{}'-Key. Using default value: {}.", configKey, defaultValue);
            return defaultValue;
        }
    }

    private List<String> parseCommaSeparatedValues(String configKey) {
        String configString = getValue(configKey);
        if (configString == null) {
            LOGGER.warn("Error while parsing '{}'. Using empty list as default.", configKey);
            return Collections.emptyList();
        }
        ArrayList<String> values = new ArrayList<String>();
        for (String valueString : configString.split(",")) {
            String trimmedValue = valueString.trim();
            if ( !trimmedValue.isEmpty()) {
                values.add(trimmedValue);
            }
        }
        return values;
    }

    private String parseStringValue(String configKey, String defaultValue) {
        String value = getValue(configKey);
        if (value == null) {
            value = defaultValue;
            LOGGER.warn("Missing Parameter '{}'. Default value is now '{}'", configKey, defaultValue);
        }
        return value;
    }

    private Properties loadProperties() {
        try {
            final Properties config = new Properties();
            URL configurationResource = getClass().getResource(CONFIG_FILE);
            File configFile = new File(configurationResource.toURI());
            config.loadFromXML(new FileInputStream(configFile));
            return config;
        }
        catch (Exception e) {
            LOGGER.error("Could not find feeder configuration: {}", CONFIG_FILE);
            throw new IllegalStateException("Could not find configuration file.");
        }
    }

    public String getValue(String key) {
        return configuration.getProperty(key);
    }

    /**
     * @deprecated obsolete config
     */
    @Deprecated
    public List<String> getProcedureNameConstraints() {
        return this.procedureNameConstraints;
    }

    public List<String> getProhibitProcedureNames() {
        return this.prohibitProcedureNames;
    }

    /**
     * @return update interval of performing GetObservation updates in milliseconds.
     */
    public long getElapseTimeOfGetObservationsUpdate() {
        return this.elapseTimeOfGetObservationsUpdateInMillis;
    }

    public List<String> getNoDataValues() {
        return noDataValues;
    }

    public long getInitialUpdateIntervalRange() {
        return firstUpdateIntervalRange;
    }

    public int getMaxNumProc() {
        return maximalNumberOfProcedures;
    }

    public String getSesDefaultTopicDialect() {
        return this.sesDefaultTopicDialect;
    }

    public String getSesDefaultTopic() {
        return sesDefaultTopic;
    }

    public String getSesLifetimeDuration() {
        return sesLifetimeDuration;
    }

    public String getSesEndpoint() {
        return sesEndpoint;
    }

    public long getFirstConsideredTimeInterval() {
        return firstConsideredTimeIntervalInMillis;
    }

    public boolean isOnlyYoungestName() {
        return onlyYoungestName;
    }

    class ConfigurationKeys {

        /** Key for the period to collect the new observations in milliseconds. */
        static final String KEY_GETOBSERVATIONS_UPDATE_INTERVAL = "getobservations_update_time";

        /** Key for the minimum update time of an observation in milliseconds. */
        static final String KEY_FIRST_UPDATE_INTERVAL_RANGE = "first_update_interval_range";

        /** Key for the maximum number of procedures. */
        static final String KEY_MAXIMUM_NUMBER_PROCEDURES = "maximum_number_procedures";

        /** Key for a list of procedure name constraints. */
        static final String KEY_PROCEDURE_NAME_CONSTRAINTS = "procedure_name_constraints";

        /** Key for a list of prohibit procedure names. */
        static final String KEY_PROHIBIT_PROCEDURE_NAMES = "prohibit_procedure_names";

        /** Key for a list of no data values. */
        static final String KEY_NO_DATA_VALUES = "nodata_values";

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
        static final String KEY_LAST_CONSIDERED_TIME_INTERVAL = "latest_considered_time_interval";

        /** Key for the youngest new observation sended to the ses */
        static final String KEY_ONLY_YOUNGEST_OBSERVATION = "only_youngest_observation";

        // static final String KEY_SLEEP_TIME_OBSERVATIONS = "sleep_time_observation";
    }

}
