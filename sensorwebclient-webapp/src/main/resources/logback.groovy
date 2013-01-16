//
// Built on Tue Jan 15 12:28:28 CET 2013 by logback-translator
// For more information on configuration files in Groovy
// please see http://logback.qos.ch/manual/groovy.html

// For assistance related to this tool or configuration files
// in general, please contact the logback user mailing list at
//    http://qos.ch/mailman/listinfo/logback-user

// For professional support please see
//   http://www.qos.ch/shop/products/professionalSupport

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.*

scan()
def logFile = "${config.logger.fileappender.filepath}/${pom.artifactId}-${project.version}"
appender("FILE", RollingFileAppender) {
  file = "${logFile}.log"
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "${logFile}/%d{yyyy-MM-dd}.log"
    maxHistory = 30
  }
  encoder(PatternLayoutEncoder) {
    pattern = "%date %level [%thread] [%file:%line] %msg%n"
  }
}
appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{0} - %msg%n"
  }
}
logger("org.n52", ${config.logger.level})
logger("org.n52.oxf", ${config.logger.level.oxf})
logger("org.n52.client", ${config.logger.level.client})
logger("org.n52.server", ${config.logger.level.server})
logger("org.geotools", WARN)
logger("org.apache.commons.httpclient", WARN)
logger("com.sun.xml", ERROR)
logger("org.geotools", WARN)
root(INFO, ["FILE", "STDOUT"])