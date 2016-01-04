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
package org.n52.server.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.xml.NcNameResolver;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.oxf.xmlbeans.tools.SoapUtil;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.impl.SystemDocumentImpl;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponsePropertyType;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.SensorDescriptionType;

public class DescribeSensorParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeSensorParser.class);
    
    private static final String NS_SML_101 = "http://www.opengis.net/sensorML/1.0.1";
    
    private static final String NS_SML_20 = "http://www.opengis.net/sensorml/2.0";

    private final SensorMLParser parser;

    /**
     * Creates a SensorML Parser considering individual service settings contained by the {@link SOSMetadata},
     * e.g. if coordinate axes ordering shall be considered strict or classic XY ordering shall be used.
     *
     * @param inputStream
     *        the SensorML data stream to parse.
     * @param metadata
     *        the individual settings of the SOS.
     * @throws XmlException
     *         if parsing data stream failed.
     * @throws IOException
     *         if data stream could not be read.
     * @throws XMLHandlingException
     *         if SensorML is not valid.
     * @throws FactoryException
     *         if creating default spatial reference failed.
	 * @throws OXFException
	 *		   if the sensor description is not supported
	 */
    public DescribeSensorParser(final InputStream inputStream, final SOSMetadata metadata) throws XmlException,
            IOException,
            XMLHandlingException,
            FactoryException, OXFException {
        XmlObject xmlObject = getDataStreamToParse(inputStream);
        this.parser = createParser(xmlObject, metadata);
        if (metadata.isForceXYAxisOrder()) {
        	getParser().setReferencingHelper(CRSUtils.createEpsgForcedXYAxisOrder());
        }
    }

    public String buildUpSensorMetadataStationName() {
        return getParser().buildUpSensorMetadataStationName();
    }

    public String buildUpSensorMetadataUom(final String phenomenonID) {
        return getParser().buildUpSensorMetadataUom(phenomenonID);
    }

    public String buildUpSensorMetadataHtmlUrl(final SosTimeseries timeseries) throws OXFException {
        return getParser().buildUpSensorMetadataHtmlUrl(timeseries);
    }

    public Point buildUpSensorMetadataPosition() throws FactoryException, TransformException {
        return getParser().buildUpSensorMetadataPosition();
    }

    public HashMap<String, ReferenceValue> parseReferenceValues() {
    	return getParser().parseReferenceValues();
    }

    public List<String> parseFOIReferences() {
        return getParser().parseFOIReferences();
    }

    public List<String> getPhenomenons() {
        return getParser().getPhenomenons();
    }

    public void setReferencingHelper(final CRSUtils refHelper) {
    	getParser().setReferencingHelper(refHelper);
	}
    
    public SensorMLParser getParser() {
    	return parser;
    }

	protected XmlObject getDataStreamToParse(InputStream incomingResultAsStream) throws XmlException,
            IOException,
            XMLHandlingException {
        XmlObject xmlObject = XmlObject.Factory.parse(incomingResultAsStream);
        return unwrapSensorDescriptionFrom(xmlObject);
    }

    public static SensorMLDocument unwrapSensorMLFrom(XmlObject xmlObject) throws XmlException, XMLHandlingException, IOException {
        if (SoapUtil.isSoapEnvelope(xmlObject)) {
            xmlObject = SoapUtil.stripSoapEnvelope(xmlObject);
        }
        if (xmlObject instanceof SensorMLDocument) {
            return (SensorMLDocument) xmlObject;
        }
        if (xmlObject instanceof DescribeSensorResponseDocument) {
            DescribeSensorResponseDocument responseDoc = (DescribeSensorResponseDocument) xmlObject;
            DescribeSensorResponseType response = responseDoc.getDescribeSensorResponse();
            DescribeSensorResponseType.Description[] descriptionArray = response.getDescriptionArray();
            if (descriptionArray.length == 0) {
                LOGGER.warn("No SensorDescription available in response!");
            }
            else {
                for (DescribeSensorResponseType.Description description : descriptionArray) {
                    SensorDescriptionType.Data dataDescription = description.getSensorDescription().getData();
                    String namespace = "declare namespace gml='http://www.opengis.net/gml'; ";
                    for (XmlObject xml : dataDescription.selectPath(namespace + "$this//*/@gml:id")) {
                        XmlCursor cursor = xml.newCursor();
                        String gmlId = cursor.getTextValue();
                        if ( !NcNameResolver.isNCName(gmlId)) {
                            cursor.setTextValue(NcNameResolver.fixNcName(gmlId));
                        }
                    }
                    XmlObject object = XmlObject.Factory.parse(dataDescription.xmlText());
                    if (object instanceof SystemDocumentImpl) {
                        SensorMLDocument smlDoc = SensorMLDocument.Factory.newInstance();
                        SensorMLDocument.SensorML.Member member = smlDoc.addNewSensorML().addNewMember();
                        member.set(XMLBeansParser.parse(object.newInputStream()));
                        return smlDoc;
                    }

                    return SensorMLDocument.Factory.parse(dataDescription.newInputStream());
                }
            }
        }

        LOGGER.warn("Failed to unwrap SensorML from '{}'. Return an empty description.", xmlObject.xmlText());
        return SensorMLDocument.Factory.newInstance();
    }
    
    public static XmlObject unwrapSensorDescriptionFrom(XmlObject xmlObject) throws XmlException, XMLHandlingException, IOException {
        if (SoapUtil.isSoapEnvelope(xmlObject)) {
            xmlObject = SoapUtil.stripSoapEnvelope(xmlObject);
        }
        DescribeSensorResponseType response = getDescribeSensorResponse(xmlObject);
        if (response != null) {
            DescribeSensorResponseType.Description[] descriptionArray = response.getDescriptionArray();
            if (descriptionArray.length == 0) {
                LOGGER.warn("No SensorDescription available in response!");
            }
            else {
                for (DescribeSensorResponseType.Description description : descriptionArray) {
                    SensorDescriptionType.Data dataDescription = description.getSensorDescription().getData();
                    String namespace = "declare namespace gml='http://www.opengis.net/gml'; ";
                    for (XmlObject xml : dataDescription.selectPath(namespace + "$this//*/@gml:id")) {
                        XmlCursor cursor = xml.newCursor();
                        String gmlId = cursor.getTextValue();
                        if ( !NcNameResolver.isNCName(gmlId)) {
                            cursor.setTextValue(NcNameResolver.fixNcName(gmlId));
                        }
                    }
                    return XmlObject.Factory.parse(dataDescription.xmlText());
                }
            }
        }
        return xmlObject;
    }

    private static DescribeSensorResponseType getDescribeSensorResponse(XmlObject xmlObject) {
    	 if (xmlObject instanceof DescribeSensorResponseDocument) {
    		 return ((DescribeSensorResponseDocument)xmlObject).getDescribeSensorResponse();
    	 } else if (xmlObject instanceof DescribeSensorResponsePropertyType) {
    		 return ((DescribeSensorResponsePropertyType)xmlObject).getDescribeSensorResponse();
    	 } else if (xmlObject instanceof DescribeSensorResponseType) {
    		 return (DescribeSensorResponseType)xmlObject;
    	 }
		return null;
	}

	private SensorMLParser createParser(XmlObject xmlObject, SOSMetadata metadata) throws XmlException, XMLHandlingException, IOException, OXFException {
    	String namespace = getNamespace(xmlObject);
		if (NS_SML_101.equals(namespace)) {
			return new SensorMLParser_v101(unwrapSensorMLFrom(xmlObject), metadata);
		} else if (NS_SML_20.equals(namespace)) {
			return new SensorMLParser_v20(xmlObject, metadata);
		}
		throw new OXFException(String.format("The sensor description of '%s' is not supported!", xmlObject.getClass().getName()));
	}
    
    private String getNamespace(final XmlObject doc) {
    	if (doc != null) {
	        String namespaceURI = doc.getDomNode().getNamespaceURI();
	        if (namespaceURI == null && doc.getDomNode().getFirstChild() != null) {
	            namespaceURI = doc.getDomNode().getFirstChild().getNamespaceURI();
	        }
	        /*
	         * if document starts with a comment, get next sibling (and ignore
	         * initial comment)
	         */
	        if (namespaceURI == null && doc.getDomNode().getFirstChild() != null
	                && doc.getDomNode().getFirstChild().getNextSibling() != null) {
	            namespaceURI = doc.getDomNode().getFirstChild().getNextSibling().getNamespaceURI();
	        }
	        // check with schemaType namespace, necessary for anyType elements
	        final String schemaTypeNamespace = getSchemaTypeNamespace(doc);
	        if (schemaTypeNamespace == null) {
	            return namespaceURI;
	        } else {
	            if (schemaTypeNamespace.equals(namespaceURI)) {
	                return namespaceURI;
	            } else {
	                return schemaTypeNamespace;
	            }
	        }
    	}
    	return null;
    }
    
    private String getSchemaTypeNamespace(final XmlObject doc) {
        QName name = null;
        if (doc.schemaType().isAttributeType()) {
            name = doc.schemaType().getAttributeTypeAttributeName();
        } else {
            // TODO check else/if for ...schemaType().isDocumentType ?
            name = doc.schemaType().getName();
        }
        if (name != null) {
            return name.getNamespaceURI();
        }
        return null;
    }
}
