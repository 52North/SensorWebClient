/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.server.mgmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralizationConfiguration {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralizationConfiguration.class);
    
    private static Properties properties = null;
    
    public static String getProperty(String name) throws PropertyException {
        if (properties == null) {
            if (!loadProperties()) {
                throw new PropertyException("The property resource could not be found");
            }
        }
        return properties.getProperty(name);
    }

    private static boolean loadProperties() {
        try {
            properties = new Properties();
        	URL classFolder = GeneralizationConfiguration.class.getResource("/generalizer.properties");
        	File configFile = new File(classFolder.toURI());
            properties.load(new FileInputStream(configFile));
            return true;
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find configuration file", e);
        } catch (Exception e) {
            LOGGER.error("Could not read configuration file", e);
        }
        return false;
    }
}
