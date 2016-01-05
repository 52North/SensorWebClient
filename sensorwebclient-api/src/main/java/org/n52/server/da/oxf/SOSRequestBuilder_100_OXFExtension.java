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
package org.n52.server.da.oxf;

import javax.xml.namespace.QName;

import net.opengis.gml.TimeInstantType;
import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;
import net.opengis.ogc.BinaryTemporalOpType;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.GetObservationDocument.GetObservation;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.EventTime;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.Result;
import net.opengis.sos.x10.ResponseModeType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.sos.adapter.SOSRequestBuilder_100;
import org.n52.oxf.valueDomains.time.ITimePeriod;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.oxf.valueDomains.time.TimePosition;
import org.n52.oxf.xmlbeans.tools.XmlUtil;

public class SOSRequestBuilder_100_OXFExtension extends SOSRequestBuilder_100 {
    
    @Override
    public String buildGetObservationRequest(ParameterContainer parameters) throws OXFException {

        GetObservationDocument xb_getObsDoc = GetObservationDocument.Factory.newInstance();

        GetObservation xb_getObs = xb_getObsDoc.addNewGetObservation();

        //
        // set required elements:
        //
        xb_getObs.setService((String) parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_SERVICE_PARAMETER).getSpecifiedValue());

        xb_getObs.setVersion((String) parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_VERSION_PARAMETER).getSpecifiedValue());

        xb_getObs.setOffering((String) parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_OFFERING_PARAMETER).getSpecifiedValue());

        xb_getObs.setResponseFormat((String) parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_RESPONSE_FORMAT_PARAMETER).getSpecifiedValue());

        ParameterShell observedPropertyPS = parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_OBSERVED_PROPERTY_PARAMETER);
        String[] observedProperties = observedPropertyPS.getSpecifiedTypedValueArray(String[].class);
        xb_getObs.setObservedPropertyArray(observedProperties);

        //
        // set optional elements:
        //
        if (parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_EVENT_TIME_PARAMETER) != null) {
            ITime specifiedTime;

            Object timeParamValue = parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_EVENT_TIME_PARAMETER).getSpecifiedValue();
            if (timeParamValue instanceof ITime) {
                specifiedTime = (ITime) timeParamValue;
            } else if (timeParamValue instanceof String) {
                String param = (String) timeParamValue;
                if (param.equals(TimePosition_OXFExtension.GET_OBSERVATION_TIME_PARAM_FIRST) || param.equals(TimePosition_OXFExtension.GET_OBSERVATION_TIME_PARAM_LAST)) {
                    // create a time pos with the param in it
                    specifiedTime = new TimePosition(param);
                } else {
                    specifiedTime = TimeFactory.createTime((String) timeParamValue);
                }
            } else {
                throw new OXFException("The class (" + timeParamValue.getClass() + ") of the value of the parameter 'eventTime' is not supported.");
            }

            BinaryTemporalOpType xb_binTempOp = BinaryTemporalOpType.Factory.newInstance();
            xb_binTempOp.addNewPropertyName();

            XmlCursor cursor = xb_binTempOp.newCursor();
            cursor.toChild(new QName("http://www.opengis.net/ogc", "PropertyName"));
            cursor.setTextValue("urn:ogc:data:time:iso8601");

            String timeType = null;

            if (specifiedTime instanceof ITimePeriod) {
                ITimePeriod oc_timePeriod = (ITimePeriod) specifiedTime;
                TimePeriodType xb_timePeriod = TimePeriodType.Factory.newInstance();

                TimePositionType xb_beginPosition = xb_timePeriod.addNewBeginPosition();
                xb_beginPosition.setStringValue(oc_timePeriod.getStart().toISO8601Format());

                TimePositionType xb_endPosition = xb_timePeriod.addNewEndPosition();
                xb_endPosition.setStringValue(oc_timePeriod.getEnd().toISO8601Format());

                xb_binTempOp.setTimeObject(xb_timePeriod);
                timeType = "TimePeriod";
            } else if (specifiedTime instanceof ITimePosition) {
                ITimePosition oc_timePosition = (ITimePosition) specifiedTime;
                TimeInstantType xb_timeInstant = TimeInstantType.Factory.newInstance();

                TimePositionType xb_timePosition = TimePositionType.Factory.newInstance();
                xb_timePosition.setStringValue(oc_timePosition.toISO8601Format());

                xb_timeInstant.setTimePosition(xb_timePosition);

                xb_binTempOp.setTimeObject(xb_timeInstant);
                timeType = "TimePosition";
            } else if (specifiedTime instanceof ITimePosition_OXFExtension) {

                ITimePosition_OXFExtension timePosition = (ITimePosition_OXFExtension) specifiedTime;
                TimeInstantType xb_timeInstant = TimeInstantType.Factory.newInstance();

                TimePositionType xb_timePosition = TimePositionType.Factory.newInstance();
                xb_timePosition.setStringValue(timePosition.toISO8601Format());

                xb_timeInstant.setTimePosition(xb_timePosition);

                xb_binTempOp.setTimeObject(xb_timeInstant);
                timeType = "TimeInstant";
            }

            EventTime eventTime = xb_getObs.addNewEventTime();
            eventTime.setTemporalOps(xb_binTempOp);

            // rename elements:
            cursor = eventTime.newCursor();
            cursor.toChild(new QName("http://www.opengis.net/ogc", "temporalOps"));
            cursor.setName(new QName("http://www.opengis.net/ogc", "TM_Equals"));

            cursor.toChild(new QName("http://www.opengis.net/gml", "_TimeObject"));
            cursor.setName(new QName("http://www.opengis.net/gml", timeType));

            // TODO Spec-Too-Flexible-Problem: for eventTime are several other
            // "temporalOps" possible (not only "TEquals")
        }

        ParameterShell proceduresShell = parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_PROCEDURE_PARAMETER);
        if (proceduresShell != null) {
            xb_getObs.setProcedureArray(proceduresShell.getSpecifiedTypedValueArray(String[].class));
        }

        ParameterShell featuresShell = parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_FEATURE_OF_INTEREST_PARAMETER);
        if (featuresShell != null) {
            xb_getObs.addNewFeatureOfInterest().setObjectIDArray(featuresShell.getSpecifiedTypedValueArray(String[].class));
//            if (foiParamShell.hasMultipleSpecifiedValues()) {
//                Object[] fois = foiParamShell.getSpecifiedValueArray();
//                xb_getObs.addNewFeatureOfInterest().setObjectIDArray((String[]) fois);
//            } else {
//                Object foi = foiParamShell.getSpecifiedValue();
//                xb_getObs.addNewFeatureOfInterest().setObjectIDArray(
//                        new String[] { (String) foi });
//            }
            // TODO Spec-Too-Flexible-Problem: it is also possible that the
            // FeatureOfInterest is specified as
            // a "spatialOps"
        }

        if (parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_RESULT_PARAMETER) != null) {
            String filter = (String) parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_RESULT_PARAMETER).getSpecifiedValue();

            Result resultFilter = xb_getObs.addNewResult();

            try {
                XmlObject xobj = XmlObject.Factory.parse(filter);
                resultFilter.set(xobj);
            } catch (XmlException e) {
                throw new OXFException(e);
            }
        }

        if (parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_RESULT_MODEL_PARAMETER) != null) {
            xb_getObs.setResultModel((new QName("http://www.opengis.net/om/1.0", // http://www.opengis.net/sos/0.0
                    (String) parameters.getParameterShellWithServiceSidedName(
                            GET_OBSERVATION_RESULT_MODEL_PARAMETER).getSpecifiedValue())));
        }

        if (parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_RESPONSE_MODE_PARAMETER) != null) {
            ResponseModeType.Enum responseModeEnum = ResponseModeType.Enum.forString((String) parameters.getParameterShellWithServiceSidedName(GET_OBSERVATION_RESPONSE_MODE_PARAMETER).getSpecifiedValue());
            xb_getObs.setResponseMode(responseModeEnum);
        }

        return xb_getObsDoc.xmlText(XmlUtil.PRETTYPRINT);

    }

}
