/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.opengis.sensorML.x101.AbstractComponentType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.CapabilitiesDocument.Capabilities;
import net.opengis.sensorML.x101.CharacteristicsDocument.Characteristics;
import net.opengis.sensorML.x101.ClassificationDocument.Classification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification;
import net.opengis.sensorML.x101.IdentificationDocument.Identification.IdentifierList.Identifier;
import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.OutputsDocument.Outputs.OutputList;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.impl.ProcessModelTypeImpl;
import net.opengis.sensorML.x101.impl.SystemDocumentImpl;
import net.opengis.swe.x101.AbstractDataRecordType;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.QuantityDocument.Quantity;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.TextDocument.Text;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swe.x101.VectorType.Coordinate;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.DescribeSensorResponseType.Description;
import net.opengis.swes.x20.SensorDescriptionType.Data;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.util.IOHelper;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.xml.NcNameResolver;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.util.SensorMLToHtml;
import org.n52.shared.MD5HashGenerator;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

public class DescribeSensorParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeSensorParser.class);

    private SensorMLDocument smlDoc = null;

    private CRSUtils referenceHelper = CRSUtils.createEpsgStrictAxisOrder();

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
     */
    public DescribeSensorParser(final InputStream inputStream, final SOSMetadata metadata) throws XmlException,
            IOException,
            XMLHandlingException,
            FactoryException {
        setDataStreamToParse(inputStream);
        if (metadata.isForceXYAxisOrder()) {
            referenceHelper = CRSUtils.createEpsgForcedXYAxisOrder();
        }
    }

    public String buildUpSensorMetadataStationName() {
        String stationName = "";
        final AbstractProcessType abstractProcessType = smlDoc.getSensorML().getMemberArray(0).getProcess();
        if (abstractProcessType instanceof AbstractComponentType) {
            stationName = getStationNameByAbstractComponentType((AbstractComponentType) abstractProcessType);
        }
        return stationName;
    }

    public String buildUpSensorMetadataUom(final String phenomenonID) {
        String uom = "";
        final AbstractProcessType abstractProcessType = smlDoc.getSensorML().getMemberArray(0).getProcess();
        if (abstractProcessType instanceof AbstractComponentType) {
            uom = getUomByAbstractComponentType(phenomenonID, (AbstractComponentType) abstractProcessType);
        }
        else if (abstractProcessType instanceof ProcessModelTypeImpl) {
            uom = getUomByProcessModelTypeImpl(phenomenonID, (ProcessModelTypeImpl) abstractProcessType);
        }
        return uom;
    }

    public String buildUpSensorMetadataHtmlUrl(final SosTimeseries timeseries) throws OXFException {
        try {
        	final String serviceUrl = timeseries.getServiceUrl();
            final String smlVersion = ConfigurationContext.getSOSMetadata(serviceUrl).getSensorMLVersion();
            final String filename = createSensorDescriptionFileName(timeseries);
            final File sensorMLFile = saveFile(filename);
            return SensorMLToHtml.createFromSensorML(sensorMLFile, smlVersion).transformSMLtoHTML(filename);
        }
        catch (final IOException e) {
            throw new OXFException("Could not write file.", e);
        }
    }

    public String createSensorDescriptionFileName(final SosTimeseries timeseries) {
        final String serviceUrl = timeseries.getServiceUrl();
        final String procedureId = timeseries.getProcedureId();
        final String phenomenonId = timeseries.getPhenomenonId();
        final MD5HashGenerator generator = new MD5HashGenerator("sensorML_");
        return generator.generate(new String[] {phenomenonId, procedureId, serviceUrl});
    }

    private File saveFile(final String filename) throws IOException {
        final String normalizedFilename = normalize(filename);
        final File sensorMLFile = JavaHelper.genFile(ConfigurationContext.GEN_DIR, normalizedFilename, "xml");
        IOHelper.saveFile(sensorMLFile, smlDoc.newInputStream());
        return sensorMLFile;
    }

    /**
     * @return a normalized String for use in a file path, i.e. all [\,/,:,*,?,",<,>,;,#] characters are
     *         replaced by '_'.
     */
    private String normalize(final String toNormalize) {
        return toNormalize.replaceAll("[\\\\,/,:,\\*,?,\",<,>,;,#]", "_");
    }

    public Point buildUpSensorMetadataPosition() throws FactoryException, TransformException {
        final SensorML sensorML = smlDoc.getSensorML();
        final Member[] members = sensorML.getMemberArray();
        if (members != null && members.length > 0) {
            final AbstractComponentType sysDoc = (AbstractComponentType) members[0].getProcess();
            final PositionType position = sysDoc.getPosition().getPosition();
            return createPoint(position);
        }
        return null;
    }

    protected Point createPoint(final PositionType position) throws FactoryException, TransformException {
        double x = 0d;
        double y = 0d; 
        double z = Double.NaN;

        final String outerReferenceFrame = position.getReferenceFrame();
        final String srs = referenceHelper.extractSRSCode(outerReferenceFrame);
        
        
        final VectorPropertyType location = position.getLocation();
        final net.opengis.swe.x101.VectorType.Coordinate[] coords = location.getVector().getCoordinateArray();
        for (final Coordinate coord : coords) {
            final String name = coord.getName();
            final Quantity quantity = coord.getQuantity();
            if (name.equalsIgnoreCase("latitude") || name.equalsIgnoreCase("lat") || name.equalsIgnoreCase("northing")) {
                if (referenceHelper.isLatLonAxesOrder(srs)) {
                    x = quantity.getValue();
                } else {
                    y = quantity.getValue();
                }
            }
            else if (name.equalsIgnoreCase("longitude") || name.equalsIgnoreCase("lng") || name.equalsIgnoreCase("lon") || name.equalsIgnoreCase("lgt") ||name.equalsIgnoreCase("easting")) {
                if (referenceHelper.isLatLonAxesOrder(srs)) {
                    y = quantity.getValue();
                } else {
                    x = quantity.getValue();
                }
            }
            else if (name.equalsIgnoreCase("altitude") || name.equalsIgnoreCase("alt") || name.equalsIgnoreCase("z") || name.equalsIgnoreCase("height")) {
                z = quantity.getValue();
            }
        }
        final Point point = referenceHelper.createPoint(x, y, z, srs);
        return referenceHelper.transformOuterToInner(point, srs);
    }

    private String getUomByProcessModelTypeImpl(final String phenomenonID, final ProcessModelTypeImpl processModel) {
        String uom = "";
        if (processModel.getOutputs() != null) {
            final OutputList outputList = processModel.getOutputs().getOutputList();
            final IoComponentPropertyType[] outputArray = outputList.getOutputArray();
            for (final IoComponentPropertyType output : outputArray) {
                if (output.getQuantity().getDefinition().equals(phenomenonID)) {
                    uom = output.getQuantity().getUom().getCode();
                }
            }
        }
        return uom;
    }

    private String getUomByAbstractComponentType(final String phenomenonID, final AbstractComponentType absComponent) {
        String uom = "";
        if (absComponent.isSetOutputs() && absComponent.getOutputs().isSetOutputList()) {
        	final OutputList outList = absComponent.getOutputs().getOutputList();
        	final IoComponentPropertyType[] outputs = outList.getOutputArray();
        	for (final IoComponentPropertyType output : outputs) {
        		if (output != null &&
        				isPhenomenonIdMatchingQuantityDefinition(phenomenonID, output) &&
        				isUomCodeSet(output.getQuantity())) {
        			uom = output.getQuantity().getUom().getCode();
        		}
        	}
        }

        try {
            // search in capabilities
            final Capabilities[] caps = getSensorMLCapabilities(smlDoc.getSensorML());
            for (final Capabilities cap : caps) {

                if (cap.getAbstractDataRecord() instanceof SimpleDataRecordType) {
                    final SimpleDataRecordType datarec = (SimpleDataRecordType) cap.getAbstractDataRecord();
                    for (int j = 0; j < datarec.getFieldArray().length; j++) {

                        if (datarec.getFieldArray(j).getName().equals("unit")) {
                            uom = datarec.getFieldArray(j).getText().getValue();
                        }
                    }
                }
                else if (cap.getAbstractDataRecord() instanceof DataRecordType) {
                    final DataRecordType datarec = (DataRecordType) cap.getAbstractDataRecord();
                    for (int j = 0; j < datarec.getFieldArray().length; j++) {
                        if (datarec.getFieldArray(j).getName().equals("unit")) {
                            uom = datarec.getFieldArray(j).getText().getValue();
                        }
                    }
                }

            }
        }
        catch (final NullPointerException e) {
            // FIXME dirty hack, improve above parsing
            LOGGER.trace("improve parsing here!", e);
        }

        if (uom.isEmpty()) {
            LOGGER.warn("UOM not found in Describe Sensor Document!");
        }
        else {
            LOGGER.debug("UOM found: " + uom);
        }

        return uom;
    }

	private boolean isUomCodeSet(final Quantity quantity) {
		return quantity.isSetUom() && quantity.getUom().isSetCode();
	}

	private boolean isPhenomenonIdMatchingQuantityDefinition(
			final String phenomenonID,
			final IoComponentPropertyType output){
		return output.isSetQuantity() && 
				output.getQuantity().isSetDefinition() && 
				output.getQuantity().getDefinition().equals(phenomenonID);
	}

    private String getStationNameByAbstractComponentType(final AbstractComponentType absComponentType) {
        String station = null;
        String uniqueId = null;
        final Identification[] identifications = getSensorMLIdentification(absComponentType);
        for (final Identification identification : identifications) {

            final Identifier[] identifiers = identification.getIdentifierList().getIdentifierArray();
            for (final Identifier identifier : identifiers) {
                // find shortname, if not present at all the uniqueID is chosen
                if (identifier.isSetName()) {
                    // supports discovery profile
                    if (identifier.getName().equalsIgnoreCase("shortname")) {
                        station = identifier.getTerm().getValue();
                        LOGGER.trace("use station shortname: " + station);
                        break;
                    }
                }
                final String termDefinition = identifier.getTerm().getDefinition();
                if (termDefinition != null && termDefinition.equals("urn:ogc:def:identifier:OGC:uniqueID")) {
                    uniqueId = identifier.getTerm().getValue();
                    LOGGER.trace("uniqueID found: " + uniqueId);
                }
            }
        }
        final String stationName = station != null ? station : uniqueId;
        LOGGER.debug(String.format("parsed '%s' as station name", stationName));
        return stationName;
    }

    private File saveSensorMLFile(final String filename) throws IOException {
        final String normalizedFilename = normalize(filename);
        final File sensorMLFile = JavaHelper.genFile(ConfigurationContext.GEN_DIR, normalizedFilename, "xml");
        IOHelper.saveFile(sensorMLFile, smlDoc.newInputStream());
        return sensorMLFile;
    }

    
    public HashMap<String, ReferenceValue> parseReferenceValues() {
        final Capabilities[] capabilities = getSensorMLCapabilities(smlDoc.getSensorML());
        final HashMap<String, ReferenceValue> map = new HashMap<String, ReferenceValue>();
        if (capabilities == null || capabilities.length == 0) {
            return map;
        }

        for (final Capabilities capability : capabilities) {
            final AbstractDataRecordType abstractDataRecord = capability.getAbstractDataRecord();
            if (abstractDataRecord instanceof SimpleDataRecordType) {
                final SimpleDataRecordType simpleDataRecord = (SimpleDataRecordType) abstractDataRecord;
                for (final AnyScalarPropertyType field : simpleDataRecord.getFieldArray()) {
                    if (field.isSetText()) {
                        final String fieldName = field.getName();
                        final Text textComponent = field.getText();
                        final String definition = textComponent.getDefinition();
                        if (isReferenceValue(definition)) {
                            final ReferenceValue referenceValue = parseReferenceValue(textComponent, fieldName);
                            if (referenceValue != null) {
                                map.put(fieldName, referenceValue);
                            }
                        }
                    }
                }
            }
            else if (abstractDataRecord instanceof DataRecordType) {
                final DataRecordType dataRecord = (DataRecordType) abstractDataRecord;
                for (final DataComponentPropertyType field : dataRecord.getFieldArray()) {
                    if (field.isSetText()) {
                        final String fieldName = field.getName();
                        final Text textComponent = field.getText();
                        final String definition = textComponent.getDefinition();
                        if (isReferenceValue(definition)) {
                            final ReferenceValue referenceValue = parseReferenceValue(textComponent, fieldName);
                            if (referenceValue != null) {
                                map.put(fieldName, referenceValue);
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * Checks for 'definition's known to declare not reference values. All definitions
     * 
     * @param definition
     * @return
     */
    private boolean isReferenceValue(final String definition) {
        return definition != null
                && ! ("urn:x-ogc:def:property:unit".equalsIgnoreCase(definition)
                        || "urn:x-ogc:def:property:equidistance".equalsIgnoreCase(definition)
                        || "FeatureOfInterest identifier".equalsIgnoreCase(definition)
                        || "FeatureOfInterestID".equalsIgnoreCase(definition));
    }

    private ReferenceValue parseReferenceValue(final Text text, final String fieldName) {

        final String stringValue = text.getValue();
        if (stringValue.matches("([0-9\\,\\.\\+\\-]+)")) {
            return new ReferenceValue(fieldName, new Double(stringValue));
        }
        if (stringValue.contains(" ")) {
            // special case: value + " " + uom(e.g. "637.0 cm")
            final String tmp = stringValue.substring(0, stringValue.indexOf(" "));
            if (tmp.matches("([0-9\\,\\.\\+\\-]+)")) {
                return new ReferenceValue(fieldName, new Double(tmp));
            }
        }
        return null;
    }

    /**
     * @param sml
     *        the sensorML document
     * @return the sensorML's capabilities modeled either in SensorML root or within Member/System
     */
    private Capabilities[] getSensorMLCapabilities(final SensorML sml) {
        final Capabilities[] capabilitiesArray = sml.getCapabilitiesArray();
        if (capabilitiesArray != null && capabilitiesArray.length != 0) {
            return capabilitiesArray;
        }
        else {
            final Member member = sml.getMemberArray(0);
            if (member.getProcess() instanceof AbstractComponentType) {
                return ((AbstractComponentType) member.getProcess()).getCapabilitiesArray();
            }
            else {
                final SchemaType type = member.getProcess() != null ? member.getProcess().schemaType() : null;
                LOGGER.warn("SensorML does not contain a process substitution: {}", type);
                return new Capabilities[0];
            }
        }
    }

    /**
     * @param absComponent
     *        the sensorML document
     * @return the sensorML's identification modeled either in SensorML root or within Member/System
     */
    private Identification[] getSensorMLIdentification(final AbstractComponentType absComponent) {
        final Identification[] identificationArray = absComponent.getIdentificationArray();
        if (identificationArray != null && identificationArray.length != 0) {
            return identificationArray;
        }
        else {
            return absComponent.getIdentificationArray();
        }
    }

    /**
     * @param sml
     *        the sensorML document
     * @return the sensorML's characteristics modeled either in SensorML root or within Member/System
     */
    private Characteristics[] getSensorMLCharacteristics(final SensorML sml) {
        // stub method for eventual later use
        final Characteristics[] characteristicsArray = sml.getCharacteristicsArray();
        if (characteristicsArray != null && characteristicsArray.length != 0) {
            return characteristicsArray;
        }
        else {
            final Member member = sml.getMemberArray(0);
            final AbstractComponentType absComponent = member.isSetProcess() ? (AbstractComponentType) member.getProcess()
                                                                      : null;
            if (absComponent == null) {
                LOGGER.warn("SensorML does not contain a process substitution.");
                return new Characteristics[0];
            }
            return absComponent.getCharacteristicsArray();
        }
    }

    /**
     * @param sml
     *        the sensorML document
     * @return the sensorML's classifications modeled either in SensorML root or within Member/System
     */
    private Classification[] getSensorMLClassifications(final SensorML sml) {
        // stub method for eventual later use
        final Classification[] classificationArray = sml.getClassificationArray();
        if (classificationArray != null && classificationArray.length != 0) {
            return classificationArray;
        }
        else {
            final Member member = sml.getMemberArray(0);
            final AbstractComponentType absComponent = member.isSetProcess() ? (AbstractComponentType) member.getProcess()
                                                                      : null;
            if (absComponent == null) {
                LOGGER.warn("SensorML does not contain a process substitution.");
                return new Classification[0];
            }
            return absComponent.getClassificationArray();
        }
    }

    public List<String> parseFOIReferences() {

        final List<String> fois = new ArrayList<String>();
        final Capabilities[] caps = getSensorMLCapabilities(smlDoc.getSensorML());

        // get linkage of procedure<->foi
        for (final Capabilities cap : caps) {
            if (cap.getAbstractDataRecord() instanceof SimpleDataRecordType) {
                final SimpleDataRecordType rec = (SimpleDataRecordType) cap.getAbstractDataRecord();
                // boolean foiRef = false;
                for (int j = 0; j < rec.getFieldArray().length; j++) {
                    final AnyScalarPropertyType field = rec.getFieldArray(j);
                    if (field.isSetText()) {
                        final String definition = field.getText().getDefinition();
                        if (isValidFeatureIdDefinition(definition)) {
                            // foiRef = true;
                            fois.add(field.getText().getValue());
                        }
                    }
                }
            }
            else if (cap.getAbstractDataRecord() instanceof DataRecordType) {
                final DataRecordType rec = (DataRecordType) cap.getAbstractDataRecord();
                // boolean foiRef = false;
                for (int j = 0; j < rec.getFieldArray().length; j++) {
                    final DataComponentPropertyType field = rec.getFieldArray(j);
                    if (field.isSetText()) {
                        final String definition = field.getText().getDefinition();
                        if (isValidFeatureIdDefinition(definition)) {
                            // foiRef = true;
                            fois.add(field.getText().getValue());
                        }
                    }
                }
            }
        }
        return fois;
    }

    // TODO parse all allowed values from a CSV list, that can be updated after compile time
	private boolean isValidFeatureIdDefinition(final String definition)	{
		return "FeatureOfInterest identifier".equalsIgnoreCase(definition)
		        || "FeatureOfInterestID".equalsIgnoreCase(definition) // TODO maybe change to startsWith
		        || "http://www.opengis.net/def/featureOfInterest/identifier".equalsIgnoreCase(definition);
	}

    public List<String> getPhenomenons() {
        final List<String> phenomenons = new ArrayList<String>();
        final Member member = smlDoc.getSensorML().getMemberArray()[0];
        final AbstractComponentType absComponent = (AbstractComponentType) member.getProcess();
        final OutputList outputs = absComponent.getOutputs().getOutputList();
        for (final IoComponentPropertyType output : outputs.getOutputArray()) {
            if (output.isSetObservableProperty()) {
                phenomenons.add(output.getObservableProperty().getDefinition());
            }
            else if (output.getAbstractDataArray1() != null) {
                phenomenons.add(output.getAbstractDataArray1().getDefinition());
            }
            else if (output.isSetQuantity()) {
                phenomenons.add(output.getQuantity().getDefinition());
            }
            else if (isCategoryCodeSpaceHrefSet(output)) {
            	phenomenons.add(output.getCategory().getDefinition());
            }
            else {
                phenomenons.add(output.getName());
            }
        }
        return phenomenons;
    }

	private boolean isCategoryCodeSpaceHrefSet(final IoComponentPropertyType output)
	{
		return output.isSetCategory() && output.getCategory().isSetDefinition();
	}

    protected void setDataStreamToParse(final InputStream incomingResultAsStream) throws XmlException,
            IOException,
            XMLHandlingException {
        final XmlObject xmlObject = XmlObject.Factory.parse(incomingResultAsStream);
        if (xmlObject instanceof SensorMLDocument) {
            smlDoc = (SensorMLDocument) xmlObject;
        }
        else if (xmlObject instanceof DescribeSensorResponseDocument) {
            final DescribeSensorResponseDocument responseDoc = (DescribeSensorResponseDocument) xmlObject;
            final DescribeSensorResponseType response = responseDoc.getDescribeSensorResponse();
            final Description[] descriptionArray = response.getDescriptionArray();
            if (descriptionArray.length == 0) {
                LOGGER.warn("No SensorDescription available in response!");
            }
            else {
                for (final Description description : descriptionArray) {
                    final Data dataDescription = description.getSensorDescription().getData();
                    final String namespace = "declare namespace gml='http://www.opengis.net/gml'; ";
                    for (final XmlObject xml : dataDescription.selectPath(namespace + "$this//*/@gml:id")) {
                        final XmlCursor cursor = xml.newCursor();
                        final String gmlId = cursor.getTextValue();
                        if ( !NcNameResolver.isNCName(gmlId)) {
                            cursor.setTextValue(NcNameResolver.fixNcName(gmlId));
                        }
                    }
                    final XmlObject object = XmlObject.Factory.parse(dataDescription.xmlText());
                    if (object instanceof SystemDocumentImpl) {
                        smlDoc = SensorMLDocument.Factory.newInstance();
                        final Member member = smlDoc.addNewSensorML().addNewMember();
                        member.set(XMLBeansParser.parse(object.newInputStream()));
                    }
                    else {
                        smlDoc = SensorMLDocument.Factory.parse(dataDescription.newInputStream());
                    }

                    break;
                }
            }
        } else {
            final String xmlText = xmlObject == null ? null : xmlObject.xmlText();
            throw new IllegalArgumentException("Illegal sensor description: " + xmlText);
        }
    }

    public void setReferencingHelper(final CRSUtils refHelper) {
        referenceHelper = refHelper;
    }
}
