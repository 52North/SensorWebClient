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

package org.n52.server.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
    public DescribeSensorParser(InputStream inputStream, SOSMetadata metadata) throws XmlException,
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
        AbstractProcessType abstractProcessType = smlDoc.getSensorML().getMemberArray(0).getProcess();
        if (abstractProcessType instanceof AbstractComponentType) {
            stationName = getStationNameByAbstractComponentType((AbstractComponentType) abstractProcessType);
        }
        return stationName;
    }

    public String buildUpSensorMetadataUom(String phenomenonID) {
        String uom = "";
        AbstractProcessType abstractProcessType = smlDoc.getSensorML().getMemberArray(0).getProcess();
        if (abstractProcessType instanceof AbstractComponentType) {
            uom = getUomByAbstractComponentType(phenomenonID, (AbstractComponentType) abstractProcessType);
        }
        else if (abstractProcessType instanceof ProcessModelTypeImpl) {
            uom = getUomByProcessModelTypeImpl(phenomenonID, (ProcessModelTypeImpl) abstractProcessType);
        }
        return uom;
    }

    public String buildUpSensorMetadataHtmlUrl(SosTimeseries timeseries) throws OXFException {
        try {
            String serviceUrl = timeseries.getServiceUrl();
            String smlVersion = ConfigurationContext.getSOSMetadata(serviceUrl).getSensorMLVersion();
            String filename = "sensorML_" + normalize(createSensorDescriptionFileName(timeseries));
            File sensorMLFile = saveSensorMLFile(filename);
            return new SensorMLToHTMLTransformer(sensorMLFile, smlVersion).transformSMLtoHTML(filename);
        }
        catch (IOException e) {
            throw new OXFException("Could not write file.", e);
        }
    }

    public String createSensorDescriptionFileName(SosTimeseries timeseries) {
        String serviceUrl = timeseries.getServiceUrl();
        String procedureId = timeseries.getProcedureId();
        String phenomenonId = timeseries.getPhenomenonId();
        return phenomenonId + "_via_" + procedureId + "_at_" + serviceUrl;
    }

    public Point buildUpSensorMetadataPosition() throws FactoryException, TransformException {
        SensorML sensorML = smlDoc.getSensorML();
        Member[] members = sensorML.getMemberArray();
        if (members != null && members.length > 0) {
            AbstractComponentType sysDoc = (AbstractComponentType) members[0].getProcess();
            PositionType position = sysDoc.getPosition().getPosition();
            return createPoint(position);
        }
        return null;
    }

    protected Point createPoint(PositionType position) throws FactoryException, TransformException {
        double x = 0d;
        double y = 0d; 
        double z = Double.NaN;

        String outerReferenceFrame = position.getReferenceFrame();
        String srs = referenceHelper.extractSRSCode(outerReferenceFrame);
        
        
        VectorPropertyType location = position.getLocation();
        net.opengis.swe.x101.VectorType.Coordinate[] coords = location.getVector().getCoordinateArray();
        for (int j = 0; j < coords.length; j++) {
            String name = coords[j].getName();
            Quantity quantity = coords[j].getQuantity();
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
        Point point = referenceHelper.createPoint(x, y, z, srs);
        return referenceHelper.transformOuterToInner(point, srs);
    }

    private String getUomByProcessModelTypeImpl(String phenomenonID, ProcessModelTypeImpl processModel) {
        String uom = "";
        if (processModel.getOutputs() != null) {
            OutputList outputList = processModel.getOutputs().getOutputList();
            IoComponentPropertyType[] outputArray = outputList.getOutputArray();
            for (IoComponentPropertyType output : outputArray) {
                if (output.getQuantity().getDefinition().equals(phenomenonID)) {
                    uom = output.getQuantity().getUom().getCode();
                }
            }
        }
        return uom;
    }

    private String getUomByAbstractComponentType(String phenomenonID, AbstractComponentType absComponent) {
        String uom = "";
        try {
            OutputList outList = absComponent.getOutputs().getOutputList();
            IoComponentPropertyType[] outputs = outList.getOutputArray();
            for (int j = 0; j < outputs.length; j++) {
                if (outputs[j].getQuantity().getDefinition().equals(phenomenonID)) {
                    uom = outputs[j].getQuantity().getUom().getCode();
                }
            }
        }
        catch (NullPointerException e) {
            // FIXME dirty hack, improve above parsing
            LOGGER.trace("improve parsing here!", e);
        }

        try {
            // search in capabilities
            Capabilities[] caps = getSensorMLCapabilities(smlDoc.getSensorML());
            for (int i = 0; i < caps.length; i++) {

                if (caps[i].getAbstractDataRecord() instanceof SimpleDataRecordType) {
                    SimpleDataRecordType datarec = (SimpleDataRecordType) caps[i].getAbstractDataRecord();
                    for (int j = 0; j < datarec.getFieldArray().length; j++) {

                        if (datarec.getFieldArray(j).getName().equals("unit")) {
                            uom = datarec.getFieldArray(j).getText().getValue();
                        }
                    }
                }
                else if (caps[i].getAbstractDataRecord() instanceof DataRecordType) {
                    DataRecordType datarec = (DataRecordType) caps[i].getAbstractDataRecord();
                    for (int j = 0; j < datarec.getFieldArray().length; j++) {
                        if (datarec.getFieldArray(j).getName().equals("unit")) {
                            uom = datarec.getFieldArray(j).getText().getValue();
                        }
                    }
                }

            }
        }
        catch (NullPointerException e) {
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

    private String getStationNameByAbstractComponentType(AbstractComponentType absComponentType) {
        String station = null;
        String uniqueId = null;
        Identification[] identifications = getSensorMLIdentification(absComponentType);
        for (Identification identification : identifications) {

            Identifier[] identifiers = identification.getIdentifierList().getIdentifierArray();
            for (Identifier identifier : identifiers) {
                // find shortname, if not present at all the uniqueID is chosen
                if (identifier.isSetName()) {
                    // supports discovery profile
                    if (identifier.getName().equalsIgnoreCase("shortname")) {
                        station = identifier.getTerm().getValue();
                        LOGGER.trace("use station shortname: " + station);
                        break;
                    }
                }
                String termDefinition = identifier.getTerm().getDefinition();
                if (termDefinition != null && termDefinition.equals("urn:ogc:def:identifier:OGC:uniqueID")) {
                    uniqueId = identifier.getTerm().getValue();
                    LOGGER.trace("uniqueID found: " + uniqueId);
                }
            }
        }
        String stationName = station != null ? station : uniqueId;
        LOGGER.debug(String.format("parsed '%s' as station name", stationName));
        return stationName;
    }

    private File saveSensorMLFile(String filename) throws IOException {
        String normalizedFilename = normalize(filename);
        File sensorMLFile = JavaHelper.genFile(ConfigurationContext.GEN_DIR, normalizedFilename, "xml");
        IOHelper.saveFile(sensorMLFile, smlDoc.newInputStream());
        return sensorMLFile;
    }

    
    /**
     * @return a normalized String for use in a file path, i.e. all [\,/,:,*,?,",<,>,;] characters are
     *         replaced by '_'.
     */
    private String normalize(String toNormalize) {
        return toNormalize.replaceAll("[\\\\,/,:,\\*,?,\",<,>,;]", "_");
    }

    public HashMap<String, ReferenceValue> parseReferenceValues() {
        Capabilities[] capabilities = getSensorMLCapabilities(smlDoc.getSensorML());
        HashMap<String, ReferenceValue> map = new HashMap<String, ReferenceValue>();
        if (capabilities == null || capabilities.length == 0) {
            return map;
        }

        for (Capabilities capability : capabilities) {
            AbstractDataRecordType abstractDataRecord = capability.getAbstractDataRecord();
            if (abstractDataRecord instanceof SimpleDataRecordType) {
                SimpleDataRecordType simpleDataRecord = (SimpleDataRecordType) abstractDataRecord;
                for (AnyScalarPropertyType field : simpleDataRecord.getFieldArray()) {
                    if (field.isSetText()) {
                        String fieldName = field.getName();
                        Text textComponent = field.getText();
                        String definition = textComponent.getDefinition();
                        if (isReferenceValue(definition)) {
                            ReferenceValue referenceValue = parseReferenceValue(textComponent, fieldName);
                            if (referenceValue != null) {
                                map.put(fieldName, referenceValue);
                            }
                        }
                    }
                }
            }
            else if (abstractDataRecord instanceof DataRecordType) {
                DataRecordType dataRecord = (DataRecordType) abstractDataRecord;
                for (DataComponentPropertyType field : dataRecord.getFieldArray()) {
                    if (field.isSetText()) {
                        String fieldName = field.getName();
                        Text textComponent = field.getText();
                        String definition = textComponent.getDefinition();
                        if (isReferenceValue(definition)) {
                            ReferenceValue referenceValue = parseReferenceValue(textComponent, fieldName);
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
    private boolean isReferenceValue(String definition) {
        return definition != null
                && ! ("urn:x-ogc:def:property:unit".equals(definition)
                        || "urn:x-ogc:def:property:equidistance".equals(definition)
                        || "FeatureOfInterest identifier".equals(definition) || "FeatureOfInterestID".equals(definition));
    }

    private ReferenceValue parseReferenceValue(Text text, String fieldName) {

        String stringValue = text.getValue();
        if (stringValue.matches("([0-9\\,\\.\\+\\-]+)")) {
            return new ReferenceValue(fieldName, new Double(stringValue));
        }
        if (stringValue.contains(" ")) {
            // special case: value + " " + uom(e.g. "637.0 cm")
            String tmp = stringValue.substring(0, stringValue.indexOf(" "));
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
    private Capabilities[] getSensorMLCapabilities(SensorML sml) {
        Capabilities[] capabilitiesArray = sml.getCapabilitiesArray();
        if (capabilitiesArray != null && capabilitiesArray.length != 0) {
            return capabilitiesArray;
        }
        else {
            Member member = sml.getMemberArray(0);
            if (member.getProcess() instanceof AbstractComponentType) {
                return ((AbstractComponentType) member.getProcess()).getCapabilitiesArray();
            }
            else {
                SchemaType type = member.getProcess() != null ? member.getProcess().schemaType() : null;
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
    private Identification[] getSensorMLIdentification(AbstractComponentType absComponent) {
        Identification[] identificationArray = absComponent.getIdentificationArray();
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
    private Characteristics[] getSensorMLCharacteristics(SensorML sml) {
        // stub method for eventual later use
        Characteristics[] characteristicsArray = sml.getCharacteristicsArray();
        if (characteristicsArray != null && characteristicsArray.length != 0) {
            return characteristicsArray;
        }
        else {
            Member member = sml.getMemberArray(0);
            AbstractComponentType absComponent = member.isSetProcess() ? (AbstractComponentType) member.getProcess()
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
    private Classification[] getSensorMLClassifications(SensorML sml) {
        // stub method for eventual later use
        Classification[] classificationArray = sml.getClassificationArray();
        if (classificationArray != null && classificationArray.length != 0) {
            return classificationArray;
        }
        else {
            Member member = sml.getMemberArray(0);
            AbstractComponentType absComponent = member.isSetProcess() ? (AbstractComponentType) member.getProcess()
                                                                      : null;
            if (absComponent == null) {
                LOGGER.warn("SensorML does not contain a process substitution.");
                return new Classification[0];
            }
            return absComponent.getClassificationArray();
        }
    }

    public List<String> parseFOIReferences() {

        List<String> fois = new ArrayList<String>();
        Capabilities[] caps = getSensorMLCapabilities(smlDoc.getSensorML());

        // get linkage of procedure<->foi
        for (int i = 0; i < caps.length; i++) {
            if (caps[i].getAbstractDataRecord() instanceof SimpleDataRecordType) {
                SimpleDataRecordType rec = (SimpleDataRecordType) caps[i].getAbstractDataRecord();
                // boolean foiRef = false;
                for (int j = 0; j < rec.getFieldArray().length; j++) {
                    AnyScalarPropertyType field = rec.getFieldArray(j);
                    if (field.isSetText()) {
                        String definition = field.getText().getDefinition();
                        if ("FeatureOfInterest identifier".equalsIgnoreCase(definition)
                                || "FeatureOfInterestID".equalsIgnoreCase(definition)) {
                            // foiRef = true;
                            fois.add(field.getText().getValue());
                        }
                    }
                }
            }
            else if (caps[i].getAbstractDataRecord() instanceof DataRecordType) {
                DataRecordType rec = (DataRecordType) caps[i].getAbstractDataRecord();
                // boolean foiRef = false;
                for (int j = 0; j < rec.getFieldArray().length; j++) {
                    DataComponentPropertyType field = rec.getFieldArray(j);
                    if (field.isSetText()) {
                        String definition = field.getText().getDefinition();
                        if ("FeatureOfInterest identifier".equalsIgnoreCase(definition)
                                || "FeatureOfInterestID".equalsIgnoreCase(definition)) {
                            // foiRef = true;
                            fois.add(field.getText().getValue());
                        }
                    }
                }
            }
        }
        return fois;
    }

    public List<String> getPhenomenons() {
        List<String> phenomenons = new ArrayList<String>();
        Member member = smlDoc.getSensorML().getMemberArray()[0];
        AbstractComponentType absComponent = (AbstractComponentType) member.getProcess();
        OutputList outputs = absComponent.getOutputs().getOutputList();
        for (IoComponentPropertyType output : outputs.getOutputArray()) {
            if (output.isSetObservableProperty()) {
                phenomenons.add(output.getObservableProperty().getDefinition());
            }
            else if (output.getAbstractDataArray1() != null) {
                phenomenons.add(output.getAbstractDataArray1().getDefinition());
            }
            else if (output.isSetQuantity()) {
                phenomenons.add(output.getQuantity().getDefinition());
            }
            else {
                phenomenons.add(output.getName());
            }
        }
        return phenomenons;
    }

    protected void setDataStreamToParse(InputStream incomingResultAsStream) throws XmlException,
            IOException,
            XMLHandlingException {
        XmlObject xmlObject = XmlObject.Factory.parse(incomingResultAsStream);
        if (xmlObject instanceof SensorMLDocument) {
            smlDoc = (SensorMLDocument) xmlObject;
        }
        else if (xmlObject instanceof DescribeSensorResponseDocument) {
            DescribeSensorResponseDocument responseDoc = (DescribeSensorResponseDocument) xmlObject;
            DescribeSensorResponseType response = responseDoc.getDescribeSensorResponse();
            Description[] descriptionArray = response.getDescriptionArray();
            if (descriptionArray.length == 0) {
                LOGGER.warn("No SensorDescription available in response!");
            }
            else {
                for (Description description : descriptionArray) {
                    Data dataDescription = description.getSensorDescription().getData();
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
                        smlDoc = SensorMLDocument.Factory.newInstance();
                        Member member = smlDoc.addNewSensorML().addNewMember();
                        member.set(XMLBeansParser.parse(object.newInputStream()));
                    }
                    else {
                        smlDoc = SensorMLDocument.Factory.parse(dataDescription.newInputStream());
                    }

                    break;
                }
            }
        } else {
            String xmlText = xmlObject == null ? null : xmlObject.xmlText();
            throw new IllegalArgumentException("Could not parse sensor description: " + xmlText);
        }
    }

    public void setReferencingHelper(CRSUtils refHelper) {
        this.referenceHelper = refHelper;
    }
}
