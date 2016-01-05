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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.io.crs.CRSUtils;
import org.n52.oxf.OXFException;
import org.n52.oxf.util.IOHelper;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.util.SensorMLToHtml;
import org.n52.shared.MD5HashGenerator;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Point;

public abstract class SensorMLParser {

	private CRSUtils referenceHelper = CRSUtils.createEpsgStrictAxisOrder();

	private SOSMetadata metadata;

	public SensorMLParser(SOSMetadata metadata) {
		this.metadata = metadata;
	}

	public abstract String buildUpSensorMetadataStationName();

	public abstract String buildUpSensorMetadataUom(final String phenomenonID);

	public abstract Point buildUpSensorMetadataPosition() throws FactoryException, TransformException;

	public abstract HashMap<String, ReferenceValue> parseReferenceValues();

	public abstract List<String> parseFOIReferences();

	public abstract List<String> getPhenomenons();

	public String buildUpSensorMetadataHtmlUrl(final SosTimeseries timeseries) throws OXFException {
		try {
			final String serviceUrl = timeseries.getServiceUrl();
			final String smlVersion = ConfigurationContext.getSOSMetadata(serviceUrl).getSensorMLVersion();
			final String filename = createSensorDescriptionFileName(timeseries);
			final File sensorMLFile = saveFile(filename);
			return SensorMLToHtml.createFromSensorML(sensorMLFile, smlVersion).transformSMLtoHTML(filename);
		} catch (final IOException e) {
			throw new OXFException("Could not write file.", e);
		}
	}

	protected abstract XmlObject getSensorDescription();

	protected void setReferencingHelper(final CRSUtils refHelper) {
		referenceHelper = refHelper;
	}

	protected CRSUtils getReferenceHelper() {
		return referenceHelper;
	}

	protected SOSMetadata getMetadata() {
		return metadata;
	}

	protected File saveFile(final String filename) throws IOException {
		final String normalizedFilename = normalize(filename);
		final File sensorMLFile = JavaHelper.genFile(ConfigurationContext.GEN_DIR, normalizedFilename, "xml");
		IOHelper.saveFile(sensorMLFile, getSensorDescription().newInputStream());
		return sensorMLFile;
	}

	protected String createSensorDescriptionFileName(final SosTimeseries timeseries) {
		final String serviceUrl = timeseries.getServiceUrl();
		final String procedureId = timeseries.getProcedureId();
		final String phenomenonId = timeseries.getPhenomenonId();
		final MD5HashGenerator generator = new MD5HashGenerator("sensorML_");
		return generator.generate(new String[] { phenomenonId, procedureId, serviceUrl });
	}
	
	/**
	 * Checks for 'definition's known to declare not reference values. All
	 * definitions
	 *
	 * @param definition
	 * @return
	 */
	protected boolean isReferenceValue(final String definition) {
		return definition != null && !("urn:x-ogc:def:property:unit".equalsIgnoreCase(definition)
				|| "urn:x-ogc:def:property:equidistance".equalsIgnoreCase(definition)
				|| "FeatureOfInterest identifier".equalsIgnoreCase(definition)
				|| "FeatureOfInterestID".equalsIgnoreCase(definition));
	}
	
	protected ReferenceValue checkReferenceValue(String value, String fieldName) {
		if (value.matches("([0-9\\,\\.\\+\\-]+)")) {
			return new ReferenceValue(fieldName, new Double(value));
		}
		if (value.contains(" ")) {
			// special case: value + " " + uom(e.g. "637.0 cm")
			final String tmp = value.substring(0, value.indexOf(" "));
			if (tmp.matches("([0-9\\,\\.\\+\\-]+)")) {
				return new ReferenceValue(fieldName, new Double(tmp));
			}
		}
		return null;
	}
	
	// TODO parse all allowed values from a CSV list, that can be updated after
	// compile time
	protected boolean isValidFeatureIdDefinition(final String definition) {
		return "FeatureOfInterest identifier".equalsIgnoreCase(definition)
				|| "FeatureOfInterestID".equalsIgnoreCase(definition) // TODO
																		// maybe
																		// change
																		// to
																		// startsWith
				|| "http://www.opengis.net/def/featureOfInterest/identifier".equalsIgnoreCase(definition);
	}


	/**
	 * @return a normalized String for use in a file path, i.e. all
	 *         [\,/,:,*,?,",<,>,;,#] characters are replaced by '_'.
	 */
	private String normalize(final String toNormalize) {
		return toNormalize.replaceAll("[\\\\,/,:,\\*,?,\",<,>,;,#]", "_");
	}

}
