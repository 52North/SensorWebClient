# ARCHIVED

This project is no longer maintained and will not receive any further updates. If you plan to continue using it, please be aware that future security issues will not be addressed.

# General Infos

project = 52North Sensor Web Client
version = ${project.version}
builddate = ${timestamp}


## About

The SensorWebClient provides easy access to timeseries data stored within 
Sensor Observation Services (SOS). A station measuring the phenomenon of 
interest can easily be picked from a map, discovered by station's metadata. 
After a station has been chosen, the data can be loaded and displayed in 
the client as diagram for further investigation.

There is a Wiki page describing the Sensor Web Client in general and give
access points to more resources of interest:

  https://wiki.52north.org/bin/view/SensorWeb/SensorWebClient
  
Two deployment scenarios are possible.
 
 * Simple: only for timeseries visualization
 * Complex: Like simple scenario, plus event subscription
 
For the complex scenario be aware, that further web services are needed in 
the setup (see below).




## Requirements
Software needed to be installed beforehand

 * Oracle Java JDK v1.6
 * Servlet Container (e.g. Apache Tomcat)

To setup an event subscription architecture further components are needed 
to be installed:

 * PostgreSQL 8.4 (or higher) 

Further web applications involved:
 * 52n-ses-1.1.0.war
 * 52n-wns-2.1.1.war
 * 52n-notification-translator-1.1.1.war




## Installation

Install all required software and prepare a database if you want to setup
an event notification architecture. Deploy all web applications needed into 
your the servlet container. 

The Sensor Web Client `.war` file can be found under `/bin/sensorwebclient-webapp/`.
  
Deploy it to your servlet container (copy into webapps folder or upload it
via the container's manager tool). Wait until the Sensor Web Client grabs
all information from configured SOS instances (takes some time). After that
the client is available at

  http://localhost:8080/sensorwebclient-webapp-${project.version}




## Plugins/Ext API

By default the Sensor Web Client includes only the default SOS connector. The 
release provides further connectors: 

 * sensorwebclient-eea 
 * sensorwebclient-hydro
 
These are shipped in their own directories and can be placed into `WEB-INF/lib`
folder so that those SOS dialects/profiles are also supported. Have a look at
the `ds/sos-instances.data.xml` file for SOS instance examples.

The sensorwebclient-ext module provides an API which can be used to create
permalinks to access to the Sensor Web Client. Please note that new SOS
instances have to be known or at least supported by the client.




## Build from Source

Sources are available under `./src directory`.

Configure the sensorwebclient-build-example.properties file and build the
client from command line using Apache Maven. You can separate several 
configuration contexts by renaming (the `<new-context>` part) and saving it 
under `sensorwebclient-build-<new-context>.properties` into your 
`${user.home}`-folder. Build your context dependent Client instance by running  

```
  mvn clean install -P env-dev -Denv=<context>
```

By default the Sensor Web Client build with the default SOS connection only.
If you want to build and include another one, you have to activate the 
accoring profile, e.g. for the Hydro connector:

```
  mvn clean install -P env-dev,connector-hydro -Denv=<context>
```

For detailed installation and configuration explanation, please consult 
the Wiki page

  https://wiki.52north.org/bin/view/SensorWeb/SensorWebClientInstallationGuide


