/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.xmlbeans.XmlObject;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Point;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sensorml.x20.AbstractPhysicalProcessDocument;
import net.opengis.sensorml.x20.AbstractPhysicalProcessPropertyType;
import net.opengis.sensorml.x20.AbstractPhysicalProcessType;
import net.opengis.sensorml.x20.AbstractProcessDocument;
import net.opengis.sensorml.x20.AbstractProcessPropertyType;
import net.opengis.sensorml.x20.AbstractProcessType;
import net.opengis.sensorml.x20.CapabilityListType.Capability;
import net.opengis.sensorml.x20.ClassifierListPropertyType;
import net.opengis.sensorml.x20.DescribedObjectDocument;
import net.opengis.sensorml.x20.DescribedObjectPropertyType;
import net.opengis.sensorml.x20.DescribedObjectType;
import net.opengis.sensorml.x20.DescribedObjectType.Capabilities;
import net.opengis.sensorml.x20.IdentifierListPropertyType;
import net.opengis.sensorml.x20.IdentifierListType.Identifier;
import net.opengis.sensorml.x20.OutputListType;
import net.opengis.sensorml.x20.OutputListType.Output;
import net.opengis.sensorml.x20.PositionUnionPropertyType;
import net.opengis.sensorml.x20.TermType;
import net.opengis.swe.x20.AbstractDataComponentType;
import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;
import net.opengis.swe.x20.QuantityType;
import net.opengis.swe.x20.TextType;
import net.opengis.swe.x20.VectorType;
import net.opengis.swe.x20.VectorType.Coordinate;

public class SensorMLParser_v20 extends SensorMLParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(SensorMLParser_v20.class);

	private XmlObject xmlObject;

	public SensorMLParser_v20(XmlObject xmlObject, SOSMetadata metadata) {
		super(metadata);
		this.xmlObject = xmlObject;
	}

	@Override
	public String buildUpSensorMetadataStationName() {
		String stationName = "";
		DescribedObjectType describedObjectType = getDescribedObjectType();
		if (describedObjectType != null) {
			stationName = getStationNameByAbstractComponentType(describedObjectType);
		}
		return stationName;
	}

	@Override
	public String buildUpSensorMetadataUom(String phenomenonID) {
		String uom = "";
		AbstractProcessType abstractProcessType = getAbstractProcessType();
		if (abstractProcessType != null) {
			uom = getUomByAbstractProcessType(phenomenonID, abstractProcessType);
		}
		return uom;
	}

	@Override
	public Point buildUpSensorMetadataPosition() throws FactoryException, TransformException {
		AbstractPhysicalProcessType abstractPhysicalProcessType = getAbstractPhysicalProcessType();
		if (abstractPhysicalProcessType != null) {
			return buildUpSensorMetadataPosition(abstractPhysicalProcessType);
		}
		return null;
	}

	private Point buildUpSensorMetadataPosition(AbstractPhysicalProcessType appt)
			throws FactoryException, TransformException {
		if (appt.getPositionArray() != null) {
			for (PositionUnionPropertyType pupt : appt.getPositionArray()) {
				return createPoint(pupt);
			}
		}
		return null;
	}

	@Override
	public HashMap<String, ReferenceValue> parseReferenceValues() {
		final HashMap<String, ReferenceValue> map = new HashMap<String, ReferenceValue>();
		DescribedObjectType describedObjectType = getDescribedObjectType();
		if (describedObjectType != null) {
			final Capabilities[] capabilities = getSensorMLCapabilities(describedObjectType);
			if (capabilities == null || capabilities.length == 0) {
				return map;
			}
			for (final Capabilities caps : capabilities) {
				if ("referenceValues".equalsIgnoreCase(caps.getName())
						|| "referenceValue".equalsIgnoreCase(caps.getName())
						|| "valueReferences".equalsIgnoreCase(caps.getName())
						|| "valueReference".equalsIgnoreCase(caps.getName())) {
					if (caps.isSetCapabilityList()) {
						for (final Capability cap : caps.getCapabilityList().getCapabilityArray()) {
							if (cap.isSetAbstractDataComponent()) {
								final AbstractDataComponentType adct = cap.getAbstractDataComponent();
								if (adct instanceof DataRecordType) {
									final DataRecordType dataRecord = (DataRecordType) adct;
									for (final Field field : dataRecord.getFieldArray()) {
										if (field.isSetAbstractDataComponent()
												&& field.getAbstractDataComponent() instanceof TextType) {
											final TextType textComponent = (TextType) field.getAbstractDataComponent();
											final String fieldName;
											if (textComponent.isSetLabel()) {
												fieldName = textComponent.getLabel();
											} else {
												fieldName = field.getName();
											}
											final String definition = textComponent.getDefinition();
											if (isReferenceValue(definition)) {
												final ReferenceValue referenceValue = parseReferenceValue(textComponent,
														fieldName);
												if (referenceValue != null) {
													map.put(fieldName, referenceValue);
												}
											}
										}
									}
								} else if (adct instanceof TextType) {
									final TextType textComponent = (TextType) adct;
									final String fieldName;
									if (textComponent.isSetLabel()) {
										fieldName = textComponent.getLabel();
									} else {
										fieldName = cap.getName();
									}
									final String definition = textComponent.getDefinition();
									if (isReferenceValue(definition)) {
										final ReferenceValue referenceValue = parseReferenceValue(textComponent,
												fieldName);

										if (referenceValue != null) {
											map.put(fieldName, referenceValue);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return map;
	}

	private ReferenceValue parseReferenceValue(final TextType text, final String fieldName) {
		return checkReferenceValue(text.getValue(), fieldName);
	}

	@Override
	public List<String> parseFOIReferences() {
		final List<String> fois = new ArrayList<String>();
		AbstractProcessType abstractProcessType = getAbstractProcessType();
		if (abstractProcessType != null && abstractProcessType.isSetFeaturesOfInterest()) {
			for (FeaturePropertyType fpt : abstractProcessType.getFeaturesOfInterest().getFeatureList()
					.getFeatureArray()) {
				if (fpt.isSetHref()) {
					fois.add(fpt.getHref());
				} else if (fpt.isSetAbstractFeature() && fpt.getAbstractFeature().isSetIdentifier()) {
					fois.add(fpt.getAbstractFeature().getIdentifier().getStringValue());
				}
			}
		} else {
			DescribedObjectType describedObjectType = getDescribedObjectType();
			if (describedObjectType != null) {
				final Capabilities[] caps = getSensorMLCapabilities(describedObjectType);
				// get linkage of procedure<->foi
				for (final Capabilities cap : caps) {
					if (cap.isSetCapabilityList()) {
						for (Capability capability : cap.getCapabilityList().getCapabilityArray()) {
							if (capability.isSetAbstractDataComponent()
									&& capability.getAbstractDataComponent() instanceof DataRecordType) {
								final DataRecordType rec = (DataRecordType) capability.getAbstractDataComponent();
								// boolean foiRef = false;
								for (Field field : rec.getFieldArray()) {
									if (field.isSetAbstractDataComponent()
											&& field.getAbstractDataComponent() instanceof TextType) {
										final String definition = field.getAbstractDataComponent().getDefinition();
										if (isValidFeatureIdDefinition(definition)) {
											// foiRef = true;
											fois.add(((TextType) field.getAbstractDataComponent()).getValue());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return fois;
	}

	@Override
	public List<String> getPhenomenons() {
		final List<String> phenomenons = new ArrayList<String>();
		AbstractProcessType abstractProcessType = getAbstractProcessType();
		if (abstractProcessType != null && abstractProcessType.isSetOutputs()) {
			OutputListType outputList = abstractProcessType.getOutputs().getOutputList();
			for (final Output output : outputList.getOutputArray()) {
				if (output.isSetObservableProperty()) {
					phenomenons.add(output.getObservableProperty().getDefinition());
				} else if (output.isSetAbstractDataComponent()) {
					if (output.getAbstractDataComponent().isSetDefinition()) {
						phenomenons.add(output.getAbstractDataComponent().getDefinition());
					} else {
						phenomenons.add(output.getName());
					}
				}
			}
		}

		return phenomenons;
	}

	@Override
	protected XmlObject getSensorDescription() {
		return xmlObject;
	}

	public Point createPoint(final PositionUnionPropertyType position) throws FactoryException, TransformException {
		double x = 0d;
		double y = 0d;
		double z = Double.NaN;

		String srs = null;
		if (position.isSetVector()) {
			final VectorType location = position.getVector();
			srs = getReferenceHelper().extractSRSCode(location.getReferenceFrame());
			final Coordinate[] coords = location.getCoordinateArray();
			for (final Coordinate coord : coords) {
				final String name = coord.getName();
				final QuantityType quantity = coord.getQuantity();
				if (name.equalsIgnoreCase("latitude") || name.equalsIgnoreCase("lat")
						|| name.equalsIgnoreCase("northing")) {
					if (getReferenceHelper().isLatLonAxesOrder(srs)) {
						x = quantity.getValue();
					} else {
						y = quantity.getValue();
					}
				} else if (name.equalsIgnoreCase("longitude") || name.equalsIgnoreCase("lng")
						|| name.equalsIgnoreCase("lon") || name.equalsIgnoreCase("lgt")
						|| name.equalsIgnoreCase("easting")) {
					if (getReferenceHelper().isLatLonAxesOrder(srs)) {
						y = quantity.getValue();
					} else {
						x = quantity.getValue();
					}
				} else if (name.equalsIgnoreCase("altitude") || name.equalsIgnoreCase("alt")
						|| name.equalsIgnoreCase("z") || name.equalsIgnoreCase("height")) {
					z = quantity.getValue();
				}
			}
		} else if (position.isSetDataRecord()) {
			for (Field field : position.getDataRecord().getFieldArray()) {
				final String name = field.getName();
				if (field.getAbstractDataComponent() != null
						&& field.getAbstractDataComponent() instanceof QuantityType) {
					final QuantityType quantity = (QuantityType) field.getAbstractDataComponent();
					if (name.equalsIgnoreCase("latitude") || name.equalsIgnoreCase("lat")
							|| name.equalsIgnoreCase("northing")) {
						srs = checkReferenceFrame(srs, quantity);
						if (getReferenceHelper().isLatLonAxesOrder(srs)) {
							x = quantity.getValue();
						} else {
							y = quantity.getValue();
						}
					} else if (name.equalsIgnoreCase("longitude") || name.equalsIgnoreCase("lng")
							|| name.equalsIgnoreCase("lon") || name.equalsIgnoreCase("lgt")
							|| name.equalsIgnoreCase("easting")) {
						srs = checkReferenceFrame(srs, quantity);
						if (getReferenceHelper().isLatLonAxesOrder(srs)) {
							y = quantity.getValue();
						} else {
							x = quantity.getValue();
						}
					} else if (name.equalsIgnoreCase("altitude") || name.equalsIgnoreCase("alt")
							|| name.equalsIgnoreCase("z") || name.equalsIgnoreCase("height")) {
						z = quantity.getValue();
					}
				}
			}
		}

		final Point point = getReferenceHelper().createPoint(x, y, z, srs);
		return (Point) getReferenceHelper().transformOuterToInner(point, srs);
	}

	private DescribedObjectType getDescribedObjectType() {
		if (xmlObject instanceof DescribedObjectDocument) {
			return ((DescribedObjectDocument) xmlObject).getDescribedObject();
		} else if (xmlObject instanceof DescribedObjectPropertyType) {
			return ((DescribedObjectPropertyType) xmlObject).getDescribedObject();
		} else if (xmlObject instanceof DescribedObjectType) {
			return (DescribedObjectType) xmlObject;
		}
		return null;
	}

	private AbstractProcessType getAbstractProcessType() {
		if (xmlObject instanceof AbstractProcessDocument) {
			return ((AbstractProcessDocument) xmlObject).getAbstractProcess();
		} else if (xmlObject instanceof AbstractProcessPropertyType) {
			return ((AbstractProcessPropertyType) xmlObject).getAbstractProcess();
		} else if (xmlObject instanceof AbstractProcessType) {
			return (AbstractProcessType) xmlObject;
		}
		return null;
	}

	private AbstractPhysicalProcessType getAbstractPhysicalProcessType() {
		if (xmlObject instanceof AbstractPhysicalProcessDocument) {
			return ((AbstractPhysicalProcessDocument) xmlObject).getAbstractPhysicalProcess();
		} else if (xmlObject instanceof AbstractPhysicalProcessPropertyType) {
			return ((AbstractPhysicalProcessPropertyType) xmlObject).getAbstractPhysicalProcess();
		} else if (xmlObject instanceof AbstractPhysicalProcessType) {
			return (AbstractPhysicalProcessType) xmlObject;
		}
		return null;
	}

	private String checkReferenceFrame(String srs, QuantityType quantity) {
		if (quantity.isSetReferenceFrame() && srs == null) {
			return getReferenceHelper().extractSRSCode(quantity.getReferenceFrame());
		}
		return srs;
	}

	private String getStationNameByAbstractComponentType(final DescribedObjectType describedObjectType) {
		String station = null;
		String uniqueId = null;
		if (describedObjectType.isSetIdentifier()) {
			uniqueId = describedObjectType.getIdentifier().getStringValue();
		}
		final IdentifierListPropertyType[] identifications = getSensorMLIdentification(describedObjectType);
		for (final IdentifierListPropertyType identification : identifications) {
			final Identifier[] identifiers = identification.getIdentifierList().getIdentifier2Array();
			for (final Identifier identifier : identifiers) {
				// find shortname, if not present at all the uniqueID is chosen
				TermType term = identifier.getTerm();
				if (term.isSetDefinition()) {
					final String termDefinition = identifier.getTerm().getDefinition();
					if (termDefinition.equals("urn:ogc:def:identifier:OGC:uniqueID")) {
						if (uniqueId != null && !uniqueId.isEmpty()) {
							uniqueId = term.getValue();
							LOGGER.trace("uniqueID found: " + uniqueId);
						}
					}
					// supports discovery profile
					else if (termDefinition.toLowerCase(Locale.ROOT).contains("shortname")) {
						station = term.getValue();
						LOGGER.trace("use station shortname: " + station);
					}
				} else if (term.getLabel().toLowerCase(Locale.ROOT).contains("short")
						&& term.getLabel().toLowerCase(Locale.ROOT).contains("name")) {
					station = term.getValue();
					LOGGER.trace("use station shortname: " + station);
					break;
				}
				if (station != null && !station.isEmpty()) {
					break;
				}
			}
		}
		final String stationName = station != null ? station : uniqueId;
		LOGGER.debug(String.format("parsed '%s' as station name", stationName));
		return stationName;
	}

	/**
	 * @param absComponent
	 *            the sensorML document
	 * @return the sensorML's identification modeled either in SensorML root or
	 *         within Member/System
	 */
	private IdentifierListPropertyType[] getSensorMLIdentification(final DescribedObjectType describedObjectType) {
		final IdentifierListPropertyType[] identificationArray = describedObjectType.getIdentificationArray();
		if (identificationArray != null && identificationArray.length != 0) {
			return identificationArray;
		} else {
			return describedObjectType.getIdentificationArray();
		}
	}

	private String getUomByAbstractProcessType(String phenomenonID, AbstractProcessType abstractProcessType) {
		String uom = "";
		if (abstractProcessType.isSetOutputs()) {
			OutputListType outputList = abstractProcessType.getOutputs().getOutputList();
			Output[] outputArray = outputList.getOutputArray();
			for (final Output output : outputArray) {
				if (output != null && isPhenomenonIdMatchingQuantityDefinition(phenomenonID, output)
						&& output.isSetAbstractDataComponent() && isQuantity(output.getAbstractDataComponent())
						&& isUomCodeSet(output.getAbstractDataComponent())) {
					uom = ((QuantityType) output.getAbstractDataComponent()).getUom().getCode();
				}
			}
		}

		try {
			// search in capabilities
			final Capabilities[] caps = getSensorMLCapabilities(abstractProcessType);
			for (final Capabilities cap : caps) {
				if (cap.isSetCapabilityList()) {
					for (Capability capability : cap.getCapabilityList().getCapabilityArray()) {
						if (capability.isSetAbstractDataComponent()) {
							if (capability.getAbstractDataComponent() instanceof QuantityType) {
								if (isPhenomenonIdMatchingQuantityDefinition(phenomenonID,
										capability.getAbstractDataComponent())
										&& isQuantity(capability.getAbstractDataComponent())
										&& isUomCodeSet(capability.getAbstractDataComponent())) {
									uom = ((QuantityType) capability.getAbstractDataComponent()).getUom().getCode();
								}
							} else if (capability.getAbstractDataComponent() instanceof DataRecordType) {
								final DataRecordType datarec = (DataRecordType) capability.getAbstractDataComponent();
								for (int j = 0; j < datarec.getFieldArray().length; j++) {
									if (datarec.getFieldArray(j).getName().equals("unit")) {
										if (datarec.getFieldArray(j).isSetAbstractDataComponent() && datarec
												.getFieldArray(j).getAbstractDataComponent() instanceof TextType) {
											uom = ((TextType) datarec.getFieldArray(j).getAbstractDataComponent())
													.getValue();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (final NullPointerException e) {
			// FIXME dirty hack, improve above parsing
			LOGGER.trace("improve parsing here!", e);
		}

		if (uom.isEmpty()) {
			LOGGER.warn("UOM not found in Describe Sensor Document!");
		} else {
			LOGGER.debug("UOM found: " + uom);
		}

		return uom;
	}

	private boolean isPhenomenonIdMatchingQuantityDefinition(final String phenomenonID, final Output output) {
		return output.isSetAbstractDataComponent()
				&& isPhenomenonIdMatchingQuantityDefinition(phenomenonID, output.getAbstractDataComponent());
	}

	private boolean isPhenomenonIdMatchingQuantityDefinition(final String phenomenonID,
			final AbstractDataComponentType adct) {
		return adct.isSetDefinition() && adct.getDefinition().equals(phenomenonID);
	}

	private boolean isQuantity(AbstractDataComponentType adct) {
		if (adct instanceof QuantityType) {
			return true;
		}
		return false;
	}

	private boolean isUomCodeSet(final AbstractDataComponentType adct) {
		if (adct instanceof QuantityType) {
			return ((QuantityType) adct).getUom() != null && ((QuantityType) adct).getUom().isSetCode();
		}
		return false;
	}

	/**
	 * @param sml
	 *            the sensorML document
	 * @return the sensorML's capabilities modeled either in SensorML root or
	 *         within Member/System
	 */
	private Capabilities[] getSensorMLCapabilities(final DescribedObjectType dot) {
		final Capabilities[] capabilitiesArray = dot.getCapabilitiesArray();
		if (capabilitiesArray != null && capabilitiesArray.length != 0) {
			return capabilitiesArray;
		}
		return new Capabilities[0];
	}

	/**
	 * @param sml
	 *            the sensorML document
	 * @return the sensorML's classifications modeled either in SensorML root or
	 *         within Member/System
	 */
	private ClassifierListPropertyType[] getSensorMLClassifications(final DescribedObjectType dot) {
		// stub method for eventual later use
		final ClassifierListPropertyType[] classificationArray = dot.getClassificationArray();
		if (classificationArray != null && classificationArray.length != 0) {
			return classificationArray;
		}
		return new ClassifierListPropertyType[0];
	}

}
