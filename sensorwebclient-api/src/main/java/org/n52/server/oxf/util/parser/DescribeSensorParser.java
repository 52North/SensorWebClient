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

package org.n52.server.oxf.util.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sensorML.x101.impl.ProcessModelTypeImpl;
import net.opengis.swe.x101.AnyScalarPropertyType;
import net.opengis.swe.x101.DataComponentPropertyType;
import net.opengis.swe.x101.DataRecordType;
import net.opengis.swe.x101.PositionType;
import net.opengis.swe.x101.SimpleDataRecordType;
import net.opengis.swe.x101.VectorPropertyType;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType;
import net.opengis.swes.x20.DescribeSensorResponseType.Description;
import net.opengis.swes.x20.SensorDescriptionType.Data;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.util.IOHelper;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.xml.XMLTools;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.n52.oxf.xmlbeans.parser.XMLHandlingException;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.server.oxf.util.crs.AReferencingHelper;
import org.n52.server.oxf.util.parser.utils.ParsedPoint;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class DescribeSensorParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescribeSensorParser.class);

    private SensorMLDocument smlDoc = null;
    
    private AReferencingHelper referenceHelper;

    public DescribeSensorParser(InputStream inputStream, String sosVersion) throws XmlException,
            IOException,
            XMLHandlingException {
        setDataStreamToParse(inputStream, sosVersion);
        referenceHelper = AReferencingHelper.createEpsgStrictAxisOrder();
    }
    
    public String buildUpSensorMetadataStationName() {
        String stationName = "";
        AbstractProcessType abstractProcessType = smlDoc.getSensorML().getMemberArray(0).getProcess();
        if (abstractProcessType instanceof SystemType) {
            stationName = getStationNameBySystemType((SystemType) abstractProcessType);
        }
        return stationName;
    }

    public String buildUpSensorMetadataUom(String phenomenonID) {
        String uom = "";
        AbstractProcessType abstractProcessType = smlDoc.getSensorML().getMemberArray(0).getProcess();
        if (abstractProcessType instanceof SystemType) {
            uom = getUomBySystemType(phenomenonID, (SystemType) abstractProcessType);
        }
        else if (abstractProcessType instanceof ProcessModelTypeImpl) {
            uom = getUomByProcessModelTypeImpl(phenomenonID, (ProcessModelTypeImpl) abstractProcessType);
        }
        return uom;
    }

    public String buildUpSensorMetadataHtmlUrl(String procedureID, String sosUrlString) throws OXFException {
        try {
            String smlVersion = ConfigurationContext.getSOSMetadata(sosUrlString).getSensorMLVersion();
            String filename = "sensorML_" + normalize(procedureID + "_at_" + sosUrlString);
            File sensorMLFile = saveSensorMLFile(filename);
            return new SensorMLToHTMLTransformer(sensorMLFile, smlVersion).transformSMLtoHTML(filename);
        }
        catch (IOException e) {
            throw new OXFException("Could not write file.", e);
        }
    }

    public ParsedPoint buildUpSensorMetadataPosition() {

        ParsedPoint parsedPoint = new ParsedPoint();

        Double lat = null;
        Double lng = null;
        Double h = null;
        String srs = "";

        SensorML sensorML = smlDoc.getSensorML();
        for (Member member : sensorML.getMemberArray()) {

            SystemType sysDoc = (SystemType) member.getProcess();
            PositionType position = sysDoc.getPosition().getPosition();
            String srsUrn = position.getReferenceFrame();
            VectorPropertyType location = position.getLocation();
            net.opengis.swe.x101.VectorType.Coordinate[] coords = location.getVector().getCoordinateArray();

            for (int j = 0; j < coords.length; j++) {
                String name = coords[j].getName();
                double value = coords[j].getQuantity().getValue();
                if (name.equalsIgnoreCase("latitude") || name.equalsIgnoreCase("lat")) {
                    lat = new Double(value);
                }
                else if (name.equalsIgnoreCase("longitude") || name.equalsIgnoreCase("lng")) {
                    lng = new Double(value);
                }
                else if (name.equalsIgnoreCase("northing") || name.equalsIgnoreCase("y")) {
                    lat = new Double(value);
                }
                else if (name.equalsIgnoreCase("easting") || name.equalsIgnoreCase("x")) {
                    lng = new Double(value);
                }
                else if (name.equalsIgnoreCase("altitude") || name.equalsIgnoreCase("z")) {
                    h = new Double(value);
                }
            }

            srs = referenceHelper.extractSRSCode(srsUrn);
            String wgs84 = "EPSG:4326";
            if ( !srs.equals(wgs84)) {
                try {
                    GeometryFactory geometryFactory = referenceHelper.createGeometryFactory(srs);
                    Coordinate coordinate = referenceHelper.createCoordinate(srs, lng, lat, h);
                    Point point = geometryFactory.createPoint(coordinate);
                    point = referenceHelper.transform(point, srs, wgs84);
                    srs = wgs84;
                    lat = point.getX();
                    lng = point.getY();
                    LOGGER.trace(lng + "," + lat + " (" + srs + ")");
                }
                catch (Exception e) {
                    LOGGER.debug("Could not transform! Keeping old SRS: " + srs, e);
                }
            }
        }

        if (lat != null && lng != null) {
            parsedPoint.setLat(lat + "");
            parsedPoint.setLon(lng + "");
            parsedPoint.setSrs(srs);
        }

        return parsedPoint;
    }

    private String getUomByProcessModelTypeImpl(String phenomenonID, ProcessModelTypeImpl processModel) {
        String uom = "";
        OutputList outputList = processModel.getOutputs().getOutputList();
        IoComponentPropertyType[] outputArray = outputList.getOutputArray();
        for (IoComponentPropertyType output : outputArray) {
            if (output.getQuantity().getDefinition().equals(phenomenonID)) {
                uom = output.getQuantity().getUom().getCode();
            }
        }
        return uom;
    }

    private String getUomBySystemType(String phenomenonID, SystemType system) {
        String uom = "";
        try {
            OutputList outList = system.getOutputs().getOutputList();
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

    private String getStationNameBySystemType(SystemType system) {
        String station = null;
        String uniqueId = null;
        Identification[] identifications = getSensorMLIdentification(system);
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
        LOGGER.debug(String.format("parsed '%s' as station name", uniqueId));
        return stationName;
    }

    private File saveSensorMLFile(String filename) throws IOException {
        String normalizedFilename = normalize(filename);
        File sensorMLFile = JavaHelper.genFile(ConfigurationContext.GEN_DIR, normalizedFilename, "xml");
        IOHelper.saveFile(sensorMLFile, smlDoc.newInputStream());
        return sensorMLFile;
    }

    private class SensorMLToHTMLTransformer {

        private Source sensorMLSource;
        private Result htmlResult;
        private File xsltFile;

        SensorMLToHTMLTransformer(File sensorMLFile, String smlVersion) throws OXFException {
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
        private String transformSMLtoHTML(String filename) throws OXFException {
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
                return getExternalURLAsString(htmlFile);
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

        private String getExternalURLAsString(File file) {
            return ConfigurationContext.GEN_URL + "/" + file.getName();
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

    /**
     * @return a normalized String for use in a file path, i.e. all [\,/,:,*,?,",<,>,;] characters are
     *         replaced by '_'.
     */
    private String normalize(String toNormalize) {
        return toNormalize.replaceAll("[\\\\,/,:,\\*,?,\",<,>,;]", "_");
    }

    public HashMap<String, ReferenceValue> parseCapsDataFields() {

        HashMap<String, ReferenceValue> map = new HashMap<String, ReferenceValue>();
        Capabilities[] caps = getSensorMLCapabilities(smlDoc.getSensorML());

        for (int i = 0; i < caps.length; i++) {
            if (caps[i].getAbstractDataRecord() instanceof SimpleDataRecordType) {
                SimpleDataRecordType rec = (SimpleDataRecordType) caps[i].getAbstractDataRecord();
                for (int j = 0; j < rec.getFieldArray().length; j++) {

                    AnyScalarPropertyType field = rec.getFieldArray(j);
                    // FIXME put in config and define definition for
                    // reference values
                    if (field.isSetText()) {
                        // FIXME merge redundant code
                        String definition = field.getText().getDefinition();
                        if (definition.equals("urn:x-ogc:def:property:unit")
                                || definition.equals("urn:x-ogc:def:property:equidistance")
                                || definition.equals("FeatureOfInterest identifier")
                                || definition.equals("FeatureOfInterestID")
                                || definition.equals("Pegelnullpunkt ?ber NN")) {
                            // ignore
                        }
                        else {
                            Double d = null;
                            String val = field.getText().getValue();
                            if (val.matches("([0-9\\,\\.\\+\\-]+)")) {
                                d = new Double(val);
                            }
                            else {
                                // special case: value + " " + uom(e.g.
                                // "637.0 cm")
                                String tmp = val.substring(0, val.indexOf(" "));
                                if (tmp.matches("([0-9\\,\\.\\+\\-]+)")) {
                                    d = new Double(tmp);
                                }
                            }
                            if (d != null) {
                                map.put(field.getName(), new ReferenceValue(field.getName(), d));
                            }
                        }
                    }
                }
            }
            else if (caps[i].getAbstractDataRecord() instanceof DataRecordType) {
                DataRecordType rec = (DataRecordType) caps[i].getAbstractDataRecord();
                for (int j = 0; j < rec.getFieldArray().length; j++) {

                    DataComponentPropertyType field = rec.getFieldArray(j);
                    // FIXME put in config and define definition for
                    // reference values
                    if (field.isSetText()) {
                        // FIXME merge redundant code
                        String definition = field.getText().getDefinition();
                        if (definition.equals("urn:x-ogc:def:property:unit")
                                || definition.equals("urn:x-ogc:def:property:equidistance")
                                || definition.equals("FeatureOfInterest identifier")
                                || definition.equals("FeatureOfInterestID")
                                || definition.equals("Pegelnullpunkt ?ber NN")) {
                            // ignore
                        }
                        else {
                            Double d = null;
                            String val = field.getText().getValue();
                            if (val.matches("([0-9\\,\\.\\+\\-]+)")) {
                                d = new Double(val);
                            }
                            else {
                                // special case: value + " " + uom(e.g.
                                // "637.0 cm")
                                String tmp = val.substring(0, val.indexOf(" "));
                                if (tmp.matches("([0-9\\,\\.\\+\\-]+)")) {
                                    d = new Double(tmp);
                                }
                            }
                            if (d != null) {
                                map.put(field.getName(), new ReferenceValue(field.getName(), d));
                            }
                        }
                    }
                }
            }
        }
        return map;
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
            if (member.getProcess() instanceof SystemType) {
                return ((SystemType) member.getProcess()).getCapabilitiesArray();
            }
            else {
                SchemaType type = member.getProcess() != null ? member.getProcess().schemaType() : null;
                LOGGER.warn("SensorML does not contain a process substitution: {}", type);
                return new Capabilities[0];
            }
        }
    }

    /**
     * @param system
     *        the sensorML document
     * @return the sensorML's identification modeled either in SensorML root or within Member/System
     */
    private Identification[] getSensorMLIdentification(SystemType system) {
        Identification[] identificationArray = system.getIdentificationArray();
        if (identificationArray != null && identificationArray.length != 0) {
            return identificationArray;
        }
        else {
            return system.getIdentificationArray();
        }
    }

    /**
     * @param sml
     *        the sensorML document
     * @return the sensorML's characteristics modeled either in SensorML root or within Member/System
     */
    @SuppressWarnings("unused")
    private Characteristics[] getSensorMLCharacteristics(SensorML sml) {
        // stub method for eventual later use
        Characteristics[] characteristicsArray = sml.getCharacteristicsArray();
        if (characteristicsArray != null && characteristicsArray.length != 0) {
            return characteristicsArray;
        }
        else {
            Member member = sml.getMemberArray(0);
            SystemType system = member.isSetProcess() ? (SystemType) member.getProcess() : null;
            if (system == null) {
                LOGGER.warn("SensorML does not contain a process substitution.");
                return new Characteristics[0];
            }
            return system.getCharacteristicsArray();
        }
    }

    /**
     * @param sml
     *        the sensorML document
     * @return the sensorML's classifications modeled either in SensorML root or within Member/System
     */
    @SuppressWarnings("unused")
    private Classification[] getSensorMLClassifications(SensorML sml) {
        // stub method for eventual later use
        Classification[] classificationArray = sml.getClassificationArray();
        if (classificationArray != null && classificationArray.length != 0) {
            return classificationArray;
        }
        else {
            Member member = sml.getMemberArray(0);
            SystemType system = member.isSetProcess() ? (SystemType) member.getProcess() : null;
            if (system == null) {
                LOGGER.warn("SensorML does not contain a process substitution.");
                return new Classification[0];
            }
            return system.getClassificationArray();
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
        SystemType system = (SystemType) member.getProcess();
        OutputList outputs = system.getOutputs().getOutputList();
        for (IoComponentPropertyType output : outputs.getOutputArray()) {
            if (output.isSetObservableProperty()) {
                phenomenons.add(output.getObservableProperty().getDefinition());
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

    private void setDataStreamToParse(InputStream incomingResultAsStream, String sosVersion) throws XmlException,
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
                    smlDoc = SensorMLDocument.Factory.newInstance();
                    Member member = smlDoc.addNewSensorML().addNewMember();
                    Data dataDescription = description.getSensorDescription().getData();

                    String namespace = "declare namespace gml='http://www.opengis.net/gml'; ";
                    for (XmlObject xml : dataDescription.selectPath(namespace + "$this//*/@gml:id")) {
                        XmlCursor cursor = xml.newCursor();
                        String gmlId = cursor.getTextValue();
                        if ( !XMLTools.isNCName(gmlId)) {
                            cursor.setTextValue(normalizeGmlId(gmlId));
                        }
                    }

                    member.set(XMLBeansParser.parse(dataDescription.newInputStream()));
                    break;
                }
            }
        }
    }

    private String normalizeGmlId(String invalidGmlId) {

        // TODO extract to OXF XML parsing/validation

        StringBuilder sb = new StringBuilder();

        // Check first character
        char c = invalidGmlId.charAt(0);
        if ( ! (c == '_' && XMLTools.isLetter(c))) {
            sb.append('_');
        }
        // Check the rest of the characters
        for (int i = 1; i < invalidGmlId.length(); i++) {
            char currentChar = invalidGmlId.charAt(i);
            if (XMLTools.isNCNameChar(currentChar)) {
                sb.append(currentChar);
            }
            else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    public void setReferencingHelper(AReferencingHelper refHelper) {
        this.referenceHelper = refHelper;
    }
}
