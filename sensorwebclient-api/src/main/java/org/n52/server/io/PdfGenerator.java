/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
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
import org.n52.oxf.DocumentStructureType.TimeSeries;
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
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfGenerator extends Generator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PdfGenerator.class);

    private static String ENCODING = "UTF-8";

    private String pdfURL;

    private boolean zip;

    private File pdfFile;


    public PdfGenerator(boolean zip, String folder) {
        super();
        this.zip = zip;
        this.folderPostfix = folder;
    }

    private DocumentStructureDocument buildUpDocumentStructure(
            DesignOptions getRepresentationOp,
            Map<String, OXFFeatureCollection> observationCollMap) throws Exception {

        DocumentStructureDocument docStructureDoc = DocumentStructureDocument.Factory.newInstance();

        DocumentStructureType docStructure = docStructureDoc.addNewDocumentStructure();

        for (TimeseriesProperties prop : getRepresentationOp.getProperties()) {
            
            TimeseriesParametersLookup lookup = getParameterLookup(prop.getServiceUrl());

//            Offering offering = prop.getOffering();

//            OXFFeatureCollection obsColl =
//                    observationCollMap.get(offering.getId() + "@" + prop.getSosUrl());

            TimeSeries timeSeries = docStructure.addNewTimeSeries();

            String foiDesc = lookup.getFeature(prop.getFeature()).getLabel();
            String obsPropsDesc = lookup.getPhenomenon(prop.getPhenomenon()).getLabel();
            String procDesc = lookup.getProcedure(prop.getProcedure()).getLabel();
            if (procDesc.contains("urn:ogc:generalizationMethod")) {
                procDesc = procDesc.substring(0, procDesc.indexOf(","));
            }

            // set timeSeries description attributes:
            timeSeries.setFeatureOfInterestID(foiDesc);
            timeSeries.setPhenomenID(obsPropsDesc);
            timeSeries.setProcedureID(procDesc);

//            String foiID = prop.getFoi().getID();
//            String obsPropsID = prop.getPhenomenon().getID();
            String procID = prop.getProcedure();
            if (procID.contains("urn:ogc:generalizationMethod")) {
                procID = procID.substring(0, procID.indexOf(","));
            }

            // create Metadata for each timeseries:
            timeSeries.setMetadata(buildUpMetadata(prop.getServiceUrl(), procID));

            // create a table for each timeseries:
//            timeSeries.setTable(buildUpTable(prop, obsColl, foiID, obsPropsID, procID));
        }

        return docStructureDoc;
    }

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
        SOSAdapter adapter = SosAdapterFactory.createSosAdapter(metadata);
		
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
	private TableType buildUpTable(TimeseriesProperties prop, OXFFeatureCollection obsColl,
            String foiID, String obsPropID, String procID) {

	    TimeseriesParametersLookup lookup = getParameterLookup(prop.getServiceUrl());
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

        table.setRightColHeader(lookup.getPhenomenon(prop.getPhenomenon()).getLabel() + " ("
                + prop.getUnitOfMeasure() + ")");

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

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {
        try {

            Map<String, OXFFeatureCollection> observationCollMap = getFeatureCollectionFor(options, false);
            
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
    
               
                diagramGen.createLegend(options, legendOut);
                LOGGER.debug("legendFile: " + legendFile);

            } catch (Exception e) {
               throw new Exception("Error producing legend.", e);
                
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
                TimeseriesProperties pc = options.getProperties().get(0);
                this.pdfFile =
                    JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, pc.getProcedure().replaceAll("/", "_")+"_"+formatDate(new Date(options.getBegin()))+
                            "_"+formatDate(new Date(options.getEnd()))+"_", "pdf");
                this.pdfURL = ConfigurationContext.GEN_URL + this.pdfFile.getName();
            } else {
                if (options.getProperties().size()>1) {
                    this.pdfFile =
                        JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, "Cumulated_PDF_", "pdf");
                    this.pdfURL = ConfigurationContext.GEN_URL+folderPostfix +"/"+ this.pdfFile.getName();
                } else {
                    TimeseriesProperties pc = options.getProperties().get(0);
                    this.pdfFile =
                        JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, pc.getProcedure().replaceAll("/", "_")
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
            throw new GeneratorException("Error creating PDF", e);
        }
        return new FileResponse(this.pdfURL);
    }
}