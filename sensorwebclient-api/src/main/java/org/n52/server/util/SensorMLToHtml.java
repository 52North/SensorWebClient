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
package org.n52.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.n52.oxf.OXFException;
import org.n52.server.mgmt.ConfigurationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SensorMLToHtml {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLToHtml.class);

    private StreamSource sensorMLSource;
    
    private File xsltFile;
    
    public static SensorMLToHtml createFromSensorML(File sensorMLFile, String smlVersion) throws OXFException {
        return new SensorMLToHtml(sensorMLFile, smlVersion);
    }

    private SensorMLToHtml(File sensorMLFile, String smlVersion) throws OXFException {
        sensorMLSource = new StreamSource(sensorMLFile);
        xsltFile = getVersionDependentXSLTFile(smlVersion);
    }
    
    private File getVersionDependentXSLTFile(String smlVersion) throws OXFException {
        if (isSmlVersion100(smlVersion)) {
            return new File(ConfigurationContext.XSL_DIR + "/SensorML_2_HTML_100.xslt");
        }
        else if (isSmlVersion101(smlVersion)) {
            return new File(ConfigurationContext.XSL_DIR + "/SensorML_2_HTML_101.xslt");
        }
        else if (isSmlVersion20(smlVersion)) {
            return new File(ConfigurationContext.XSL_DIR + "/SensorML_2_HTML_20.xslt");
        }
        else {
            throw new OXFException(String.format("Tranforming SensorML version '%s' is not supported", smlVersion));
        }
    }

    private boolean isSmlVersion100(String version) {
        return version.contains("1.0.0");
    }

    private boolean isSmlVersion101(String version) {
        return version.contains("1.0.1");
    }

    private boolean isSmlVersion20(String version) {
        return version.contains("2.0");
    }

    /**
     * @param filename
     * @return Returns the path to transformed HTML file.
     */
    public String transformSMLtoHTML(String filename) throws OXFException {
        LOGGER.trace("Performing XSLT transformation ...");
        

        FileOutputStream fileOut = null;
        try {
            File htmlFile = getHtmlFile(filename);
            if ( !htmlFile.exists()) {
                fileOut = new FileOutputStream(htmlFile);
                StreamResult htmlResult = new StreamResult(fileOut);
                getXSLTTansformer().transform(sensorMLSource, htmlResult);
                LOGGER.trace(String.format("Transformed successfully to '%s'", htmlFile));
            }
            return getExternalURLAsString(htmlFile.getName());
        }
        catch (Exception e) {
            throw new OXFException("Could not transform SensorML to HTML.", e);
        }
        finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                }
                catch (IOException e) {
                    LOGGER.debug("Could not close file stream!", e);
                    fileOut = null;
                }
            }
        }
    }

    private File getHtmlFile(String filename) {
        return new File(ConfigurationContext.GEN_DIR + filename + ".html");
    }

    private Transformer getXSLTTansformer() throws TransformerFactoryConfigurationError,
            TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        return tFactory.newTransformer(new StreamSource(xsltFile));
    }
    
    private String getExternalURLAsString(String filename) {
        String fileLocation = ConfigurationContext.GEN_URL + "/" + filename;
        try {
            URI filePath = new URI(null, fileLocation, null);
            return filePath.getRawPath();
        } catch (URISyntaxException e) {
            String msg = String.format("Could NOT encode %s to be used as URL.", fileLocation);
            throw new RuntimeException(msg, e);
        }
    }
}

