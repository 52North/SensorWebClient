
package org.n52.server.ses.feeder;

import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_GETOBSERVATIONS_UPDATE_INTERVAL;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_LAST_CONSIDERED_TIME_INTERVAL;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_MINIMAL_UPDATE_INTERVAL_RANGE;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_NO_DATA_VALUES;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_ONLY_YOUNGEST_OBSERVATION;
import static org.n52.server.ses.feeder.FeederConfig.ConfigurationKeys.KEY_SES_DEFAULT_TOPIC;
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

    private static final String SES_DEFAULT_LIFETIME_DURATION = "2999-12-31T23:59:59+00:00";

    private static final String SES_DEFAULT_TOPIC = "ses:Measurements";

    private static FeederConfig instance;

    private Properties configuration;

    private long minimalUpdateIntervalRange;

    private List<String> noDataValues;

    private long elapseTimeOfGetObservationsUpdateInMillis;

    private boolean onlyYoungestName;

    private String sesDefaultTopic;

    private String sesLifetimeDuration;

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
        minimalUpdateIntervalRange = parseLongValue(KEY_MINIMAL_UPDATE_INTERVAL_RANGE, 120000);
        elapseTimeOfGetObservationsUpdateInMillis = parseLongValue(KEY_GETOBSERVATIONS_UPDATE_INTERVAL, 60000);

        noDataValues = parseCommaSeparatedValues(KEY_NO_DATA_VALUES);

        sesDefaultTopic = parseStringValue(KEY_SES_DEFAULT_TOPIC, SES_DEFAULT_TOPIC);
        sesLifetimeDuration = parseStringValue(KEY_SES_LIFETIME_DURATION, SES_DEFAULT_LIFETIME_DURATION);

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
     * @return update interval of performing GetObservation updates in milliseconds.
     */
    public long getElapseTimeOfGetObservationsUpdate() {
        return this.elapseTimeOfGetObservationsUpdateInMillis;
    }

    public List<String> getNoDataValues() {
        return noDataValues;
    }

    public long getMinimalUpdateIntervalRange() {
        return minimalUpdateIntervalRange;
    }

    public String getSesDefaultTopic() {
        return sesDefaultTopic;
    }

    public String getSesLifetimeDuration() {
        return sesLifetimeDuration;
    }

    public long getFirstConsideredTimeInterval() {
        return firstConsideredTimeIntervalInMillis;
    }

    public boolean isOnlyYoungestName() {
        return onlyYoungestName;
    }

    class ConfigurationKeys {

        /** Key for a list of no data values. */
        static final String KEY_NO_DATA_VALUES = "nodata_values";

        /** Key for the period to collect the new observations in milliseconds. */
        static final String KEY_GETOBSERVATIONS_UPDATE_INTERVAL = "getobservations_update_time";

        /** Key for the minimum update time of an observation in milliseconds. */
        static final String KEY_MINIMAL_UPDATE_INTERVAL_RANGE = "minimal_update_interval_range";

        /** Key for the default topic in the SES request. */
        static final String KEY_SES_DEFAULT_TOPIC = "ses_default_topic";

        /** Key for the lifetime duration in the SES. */
        static final String KEY_SES_LIFETIME_DURATION = "ses_register_publisher_lifetime";

        /** Key for the start timestamp for a feeded sensor */
        static final String KEY_LAST_CONSIDERED_TIME_INTERVAL = "latest_considered_time_interval";

        /** Key for the youngest new observation sended to the ses */
        static final String KEY_ONLY_YOUNGEST_OBSERVATION = "only_youngest_observation";

        // static final String KEY_SLEEP_TIME_OBSERVATIONS = "sleep_time_observation";
    }

}
