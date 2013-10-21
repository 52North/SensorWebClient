package org.n52.server.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
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

public class SensorMLToHTMLTransformer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLToHTMLTransformer.class);

    private Source sensorMLSource;
    private Result htmlResult;
    private File xsltFile;
    
    protected SensorMLToHTMLTransformer() {
        // only for testing
    }
    
    public SensorMLToHTMLTransformer(File sensorMLFile, String smlVersion) throws OXFException {
        this.sensorMLSource = new StreamSource(sensorMLFile);
        this.xsltFile = getVersionDependentXSLTFile(smlVersion);
    }

    private File getVersionDependentXSLTFile(String smlVersion) throws OXFException {
        if (isVersion100(smlVersion)) {
            return new File(ConfigurationContext.XSL_DIR + "/SensorML_2_HTML_100.xslt");
        }
        else if (isVersion101(smlVersion)) {
            return new File(ConfigurationContext.XSL_DIR + "/SensorML_2_HTML_101.xslt");
        }
        else if (isVersion20(smlVersion)) {
            return new File(ConfigurationContext.XSL_DIR + "/SensorML_2_HTML_20.xslt");
        }
        else {
            throw new OXFException(String.format("Tranforming SensorML version '%s' is not supported", smlVersion));
        }
    }

    private boolean isVersion100(String version) {
        return version.contains("1.0.0");
    }

    private boolean isVersion101(String version) {
        return version.contains("1.0.1");
    }

    private boolean isVersion20(String version) {
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
            File htmlFile = getHTMLFilePath(filename);
            if ( !htmlFile.exists()) {
                fileOut = new FileOutputStream(htmlFile);
                htmlResult = new StreamResult(fileOut);
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

    protected String getExternalURLAsString(String filename) {
        String fileLocation = ConfigurationContext.GEN_URL + "/" + filename;
        try {
            URI filePath = new URI(null, fileLocation, null);
            return filePath.getRawPath();
        } catch (URISyntaxException e) {
            String msg = String.format("Could NOT encode %s to be used as URL.", fileLocation);
            throw new RuntimeException(msg, e);
        }
    }
    
    private File getHTMLFilePath(String filename) {
        return new File(ConfigurationContext.GEN_DIR + filename + ".html");
    }

    private Transformer getXSLTTansformer() throws TransformerFactoryConfigurationError,
            TransformerConfigurationException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        return tFactory.newTransformer(new StreamSource(xsltFile));
    }
}

