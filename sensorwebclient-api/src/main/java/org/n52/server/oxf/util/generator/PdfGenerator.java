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

package org.n52.server.oxf.util.generator;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oxf.DocumentStructureDocument;
import org.n52.oxf.DocumentStructureType;
import org.n52.oxf.MetadataType;
import org.n52.oxf.MetadataType.GenericMetadataPair;
import org.n52.oxf.OXFException;
import org.n52.oxf.TableType;
import org.n52.oxf.TableType.Entry;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.feature.OXFAbstractObservationType;
import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.ISOSRequestBuilder;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.sos.util.SosUtil;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.access.oxfExtensions.SOSRequestBuilderFactory_OXFExtension;
import org.n52.shared.exceptions.ServerException;
import org.n52.shared.exceptions.TimeoutException;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PdfGenerator.
 * 
 * @author <a href="mailto:tremmersmann@uni-muenster.de">Thomas Remmersmann</a>
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 */
public class PdfGenerator extends Generator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfGenerator.class);

    // private static String ENCODING = "ISO-8859-1";
    /** The ENCODING. */
    private static String ENCODING = "UTF-8"; //$NON-NLS-1$

    /** The pdf url. */
    private String pdfURL;

    /** The zip. */
    private boolean zip;

    /** The pdf file. */
    private File pdfFile;


    /**
     * Instantiates a new pdf generator.
     * 
     * @param zip
     *            the zip
     * @param folder 
     */
    public PdfGenerator(boolean zip, String folder) {
        super();
        this.zip = zip;
        this.folderPostfix = folder;
    }

    /**
     * Builds the up document structure.
     * 
     * @param getRepresentationOp
     *            the get representation op
     * @param observationCollMap
     *            the observation coll map
     * @return the document structure document
     * @throws Exception
     *             the exception
     */
    private DocumentStructureDocument buildUpDocumentStructure(
            DesignOptions getRepresentationOp,
            Map<String, OXFFeatureCollection> observationCollMap) throws Exception {

        DocumentStructureDocument docStructureDoc =
                DocumentStructureDocument.Factory.newInstance();

        DocumentStructureType docStructure = docStructureDoc.addNewDocumentStructure();

        for (TimeSeriesProperties prop : getRepresentationOp.getProperties()) {

//            Offering offering = prop.getOffering();

//            OXFFeatureCollection obsColl =
//                    observationCollMap.get(offering.getId() + "@" + prop.getSosUrl());

            org.n52.oxf.DocumentStructureType.TimeSeries timeSeries =
                    docStructure.addNewTimeSeries();

            String foiDesc = prop.getFoi().getLabel();
            String obsPropsDesc = prop.getPhenomenon().getLabel();
            String procDesc = prop.getProcedure().getLabel();
            if (procDesc.contains("urn:ogc:generalizationMethod")) {
                procDesc = procDesc.substring(0, procDesc.indexOf(","));
            }

            // set timeSeries description attributes:
            timeSeries.setFeatureOfInterestID(foiDesc);
            timeSeries.setPhenomenID(obsPropsDesc);
            timeSeries.setProcedureID(procDesc);

//            String foiID = prop.getFoi().getID();
//            String obsPropsID = prop.getPhenomenon().getID();
            String procID = prop.getProcedure().getId();
            if (procID.contains("urn:ogc:generalizationMethod")) {
                procID = procID.substring(0, procID.indexOf(","));
            }

            // create Metadata for each timeseries:
            timeSeries.setMetadata(buildUpMetadata(prop.getSosUrl(), procID));

            // create a table for each timeseries:
//            timeSeries.setTable(buildUpTable(prop, obsColl, foiID, obsPropsID, procID));
        }

        return docStructureDoc;
    }

    /**
     * Builds the up metadata.
     * 
     * @param sosURL
     *            the sos url
     * @param procedureID
     *            the procedure parameterId
     * @return the metadata type
     * @throws Exception
     *             the exception
     */
    private MetadataType buildUpMetadata(String sosURL, String procedureID) throws Exception {

        SOSMetadata metadata = ConfigurationContext.getSOSMetadata(sosURL);
        String sosVersion = metadata.getSosVersion();
        String smlVersion = metadata.getSensorMLVersion();
        ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_SERVICE_PARAMETER, "SOS");
        paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_VERSION_PARAMETER, sosVersion);
        paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_PARAMETER, procedureID);
        if (SosUtil.isVersion100(sosVersion)) {
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_OUTPUT_FORMAT, smlVersion);
        } else if (SosUtil.isVersion200(sosVersion)) {
            paramCon.addParameterShell(ISOSRequestBuilder.DESCRIBE_SENSOR_PROCEDURE_DESCRIPTION_FORMAT, smlVersion);
        } else {
            throw new IllegalStateException("SOS Version (" + sosVersion + ") is not supported!");
        }

        Operation descSensorOperation = new Operation(SOSAdapter.DESCRIBE_SENSOR, sosURL, sosURL);
//		Class<SOSAdapter> adapterClass = (Class<SOSAdapter>) Class.forName(metadata.getAdapter());
//        Constructor<SOSAdapter> constructor = adapterClass.getConstructor(new Class[]{String.class, ISOSRequestBuilder.class});
//        ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.createRequestBuilder(sosVersion);
//		SOSAdapter adapter = constructor.newInstance(sosVersion, requestBuilder);

//      ISOSRequestBuilder requestBuilder = SOSRequestBuilderFactory_OXFExtension.createRequestBuilder(sosVersion);
//      Constructor<SOSAdapter> constructor = clazz.getConstructor(new Class[]{String.class, ISOSRequestBuilder.class});
//      SOSAdapter adapter = constructor.newInstance(sosVersion, requestBuilder);
        Class<SOSAdapter> clazz = (Class<SOSAdapter>) Class.forName(metadata.getAdapter());
        Constructor<SOSAdapter> constructor = clazz.getConstructor(new Class[]{String.class});
        SOSAdapter adapter = constructor.newInstance(sosVersion);
		
		
        OperationResult opResult = adapter.doOperation(descSensorOperation, paramCon);

        // parse resulting SensorML doc and store information in the
        // MetadataType object:
        XmlOptions xmlOpts = new XmlOptions();
        xmlOpts.setCharacterEncoding(ENCODING);

        XmlObject xmlObject =
                XmlObject.Factory.parse(opResult.getIncomingResultAsStream(), xmlOpts);
        MetadataType metadataType = MetadataType.Factory.newInstance();

        String namespaceDecl = "declare namespace sml='http://www.opengis.net/sensorML/1.0'; "; //$NON-NLS-1$

        for (XmlObject termObj : xmlObject.selectPath(namespaceDecl + "$this//sml:Term")) { //$NON-NLS-1$
            String attributeVal = termObj.selectAttribute(new QName("definition")).newCursor() //$NON-NLS-1$
                    .getTextValue();

            String name = null;
            String value;

            if (attributeVal.equals("urn:ogc:identifier:stationName")) {
                name = "Station"; //$NON-NLS-1$
            }

            if (attributeVal.equals("urn:ogc:identifier:operator")) {
                name = "Operator"; //$NON-NLS-1$
            }

            if (attributeVal.equals("urn:ogc:identifier:stationID")) {
                name = "ID"; //$NON-NLS-1$
            }

            if (attributeVal.equals("urn:ogc:identifier:sensorType")) {
                name = "Sensor"; //$NON-NLS-1$
            }

            XmlCursor cursor = termObj.newCursor();
            cursor.toChild("value"); //$NON-NLS-1$
            value = cursor.getTextValue();

            if (name != null) {
                GenericMetadataPair genMetaPair = metadataType.addNewGenericMetadataPair();
                genMetaPair.setName(name);
                genMetaPair.setValue(value);
            }
        }

        return metadataType;
    }

    /**
     * builds up a new {@link TableType} object.
     * 
     * @param prop
     *            the prop
     * @param obsColl
     *            the obs coll
     * @param foiID
     *            the foi parameterId
     * @param obsPropID
     *            the obs prop parameterId
     * @param procID
     *            the proc parameterId
     * @return the table type
     */
    @SuppressWarnings("unused")
	private TableType buildUpTable(TimeSeriesProperties prop, OXFFeatureCollection obsColl,
            String foiID, String obsPropID, String procID) {

        TableType table = TableType.Factory.newInstance();

        //
        // set header:
        //
        String leftColHeader = "";
        if (prop.getLanguage().equalsIgnoreCase("de")) {
            leftColHeader = "Datum";
        } else {
            leftColHeader = "Date";
        }

        table.setLeftColHeader(leftColHeader);

        table.setRightColHeader(prop.getPhenomenon().getLabel() + " ("
                + prop.getPhenomenon().getUnitOfMeasure() + ")");

        //
        // fill cells:
        //
        //fix generalizer procedures
        for (OXFFeature observation : obsColl) {
            
            String p = (String)observation.getAttribute(OXFAbstractObservationType.PROCEDURE);
            if (p.contains("urn:ogc:generalizationMethod")) {
                p = p.substring(0, p.indexOf(","));
            }
            observation.setAttribute(OXFAbstractObservationType.PROCEDURE, p);
            
        }
        
        ObservationSeriesCollection seriesCollection =
                new ObservationSeriesCollection(obsColl, new String[] { foiID },
                        new String[] { obsPropID }, new String[] { procID }, true);
        ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();

        int counter = 0;

        if (timeArray.length > 0) {
			ObservedValueTuple prevObservation;
			ObservedValueTuple nextObservation = seriesCollection.getTuple(
					new OXFFeature(foiID, null), timeArray[0]);
			ObservedValueTuple observation = nextObservation;
			for (int i = 0; i < timeArray.length; i++) {

				prevObservation = observation;
				observation = nextObservation;

				if (i + 1 < timeArray.length) {
					nextObservation = seriesCollection.getTuple(new OXFFeature(
							foiID, null), timeArray[i + 1]);
				}

				String obsVal = observation.getValue(0).toString();
				String prevObsVal = prevObservation.getValue(0).toString();
				String nextObsVal = nextObservation.getValue(0).toString();

				//            if ((i == 0) || // first observation --> in
				//                    (i == timeArray.length - 1) || // last observation --> in
				//                    (!(prevObsVal.equals(obsVal) && nextObsVal.equals(obsVal)))) {

				counter++;

				ITimePosition timePos = (ITimePosition) observation.getTime();
				double resultVal = (Double) observation.getValue(0);

				Entry entry = table.addNewEntry();
				entry.setTime(timePos.toISO8601Format());
				entry.setValue("" + resultVal);
				//            }
			}
		}
		LOGGER.info("Reduced timeArray from " + timeArray.length + " to " + counter);

        return table;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.server.oxf.ui.sosFacade.generator.Generator#producePresentation
     * (org.n52.shared.pojos.RepresentationDesignOptions)
     */
    @Override
    public RepresentationResponse producePresentation(DesignOptions options)
            throws TimeoutException, ServerException {
        Map<String, OXFFeatureCollection> observationCollMap = getFeatureCollectionFor(options, false);

        try {
            // produce document structure:
            DocumentStructureDocument docStructureDoc =
                    buildUpDocumentStructure(options, observationCollMap);

            DiagramGenerator diagramGen = new DiagramGenerator();

            options.setHeight(700);
            options.setWidth(1000);

            // produce chart image:
            File imageFile =
                    JavaHelper.genRndFile(ConfigurationContext.GEN_DIR, "chartImage_",
                            DiagramGenerator.FORMAT);
            File legendFile =
                JavaHelper.genRndFile(ConfigurationContext.GEN_DIR, "legendImage_",
                        DiagramGenerator.FORMAT);
            FileOutputStream imageOut = new FileOutputStream(imageFile);
            FileOutputStream legendOut = new FileOutputStream(legendFile);
            try {
                
                diagramGen.producePresentation(observationCollMap, options, imageOut, false);
                LOGGER.debug("imageFile: " + imageFile);
    
               
                diagramGen.produceLegend(options, legendOut);
                LOGGER.debug("legendFile: " + legendFile);

            } catch (Exception e) {
               throw new ServerException("Error producing legend", e);
                
            } finally {
                imageOut.flush();
                imageOut.close();
                legendOut.flush();
                legendOut.close();
            }
            // set chart image and legend URL in document:
            // String imageURL = genURL + "/" + imageFile.getName();
            String imageURL = imageFile.getAbsolutePath();
            docStructureDoc.getDocumentStructure().setDiagramURL(imageURL);

            // String legendURL = genURL + "/" + legendFile.getName();
            String legendURL = legendFile.getAbsolutePath();
            docStructureDoc.getDocumentStructure().setLegendURL(legendURL);

            // store the docStructureDocument as an XML file in the
            // cocoonXslDir:
            File docStructureFile =
                    JavaHelper.genRndFile(ConfigurationContext.GEN_DIR, "pdfDoc_", "xml");

            docStructureDoc.save(docStructureFile);

            File xsltFile;

            if (options.getLanguage().equalsIgnoreCase("de")) {
                xsltFile = new File(ConfigurationContext.XSL_DIR + "/Document_2_PDF_de.xslt");
            } else if (options.getLanguage().equalsIgnoreCase("nl")) {
                xsltFile = new File(ConfigurationContext.XSL_DIR + "/Document_2_PDF_nl.xslt");
            } else {
                xsltFile = new File(ConfigurationContext.XSL_DIR + "/Document_2_PDF_en.xslt");
            }

            if (this.zip) {
                TimeSeriesProperties pc = options.getProperties().get(0);
                this.pdfFile =
                    JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, pc.getProcedure().getId().replaceAll("/", "_")+"_"+formatDate(new Date(options.getBegin()))+
                            "_"+formatDate(new Date(options.getEnd()))+"_", "pdf");
                this.pdfURL = ConfigurationContext.GEN_URL + this.pdfFile.getName();
            } else {
                if (options.getProperties().size()>1) {
                    this.pdfFile =
                        JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, "Cumulated_PDF_", "pdf");
                    this.pdfURL = ConfigurationContext.GEN_URL+folderPostfix +"/"+ this.pdfFile.getName();
                } else {
                    TimeSeriesProperties pc = options.getProperties().get(0);
                    this.pdfFile =
                        JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, pc.getProcedure().getId().replaceAll("/", "_")
                        		+formatDate(new Date(options.getBegin()))+
                                "_"+formatDate(new Date(options.getEnd()))+"_", "pdf");
                    this.pdfURL = ConfigurationContext.GEN_URL +folderPostfix +"/"+ this.pdfFile.getName();
                }
               
            }

            LOGGER.debug("Transforming content to PDF.");

            OutputStream outStream = new java.io.FileOutputStream(this.pdfFile);
            outStream = new java.io.BufferedOutputStream(outStream);

            try {

                // new instance of FopFactory
                FopFactory fopFactory = FopFactory.newInstance();

                // creating a Fop with the needed output format
                Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);

                // TransformerFactory for the transformation with .xslt input
                TransformerFactory transFact = TransformerFactory.newInstance();
                Transformer transformer = transFact.newTransformer(new StreamSource(xsltFile));

                // sourcefile
                Source source = new StreamSource(docStructureFile);

                // the created FO as a SAX event
                Result result = new SAXResult(fop.getDefaultHandler());

                // begin transformation
                transformer.transform(source, result);
                LOGGER.debug("PDF url: " + this.pdfURL);
                LOGGER.info("PDF File created.");
            } catch (TransformerException e) {
                throw new OXFException("Error transforming xml", e);
            } finally {
                outStream.flush();
                outStream.close();
                JavaHelper.cleanUpDir(ConfigurationContext.GEN_DIR, ConfigurationContext.FILE_KEEPING_TIME);
            }
        } catch (Exception e) {
            throw new ServerException("Error creating PDF", e);
        }
        return new FileResponse(this.pdfURL);
    }
}