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
