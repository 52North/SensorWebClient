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
package org.n52.server.da.oxf;

import net.opengis.fes.x20.BinaryTemporalOpType;
import net.opengis.fes.x20.DuringDocument;
import net.opengis.fes.x20.TEqualsDocument;
import net.opengis.gml.x32.TimeInstantDocument;
import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodDocument;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionDocument;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.sos.x20.GetObservationType;
import net.opengis.sos.x20.GetObservationType.TemporalFilter;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterShell;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.sos.adapter.SOSRequestBuilder_200;
import org.n52.oxf.valueDomains.time.ITimePeriod;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.oxf.valueDomains.time.TimePosition;

/**
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 * 
 */
public class SOSRequestBuilder_200_OXFExtension extends SOSRequestBuilder_200 {
    
    public static final String GET_OBSERVATION_TIME_PARAM_FIRST = "getFirst";
    
    public static final String GET_ABSERVATION_NEW_TIME_PARAM_FIRST = "first";

    public static final String GET_OBSERVATION_TIME_PARAM_LAST = "latest";
    
    @Override
    protected void processTemporalFilter(GetObservationType xb_getObs, ParameterShell shell) throws OXFException {
        if (shell == null) {
            return; // optional parameter
        }
        
        ITime specifiedTime;
        Object timeParamValue = shell.getSpecifiedValue();
        if (timeParamValue instanceof ITime) {
            specifiedTime = (ITime) timeParamValue;
        } else if (timeParamValue instanceof String) {
            specifiedTime = TimeFactory.createTime((String) timeParamValue);
        } else if (timeParamValue instanceof String) {
            String param = (String) timeParamValue;
            if (isFirstOrLast(param)) {
                specifiedTime = new TimePosition(param);
            } else {
                specifiedTime = TimeFactory.createTime(param);
            }
        } else {
            throw new OXFException("The class (" + timeParamValue.getClass()
                    + ") of the value of the parameter 'eventTime' is not supported.");
        }

        String gmlId = "tp_" + System.currentTimeMillis();
        if (specifiedTime instanceof ITimePeriod) {
            ITimePeriod timePeriod = (ITimePeriod) specifiedTime;
            TimePeriodDocument periodDocument = TimePeriodDocument.Factory.newInstance();
            TimePeriodType timePeriodType = periodDocument.addNewTimePeriod();
            timePeriodType.setId(gmlId);
            
            TimePositionType beginPosition = timePeriodType.addNewBeginPosition();
            TimePositionType endPosition = timePeriodType.addNewEndPosition();
            beginPosition.setStringValue(timePeriod.getStart().toISO8601Format());
            endPosition.setStringValue(timePeriod.getEnd().toISO8601Format());

            DuringDocument duringDoc = DuringDocument.Factory.newInstance();
            BinaryTemporalOpType xb_binTempOp = duringDoc.addNewDuring();
            xb_binTempOp.set(periodDocument);
            xb_binTempOp.setValueReference("phenomenonTime");

            TemporalFilter spatialFilter = xb_getObs.addNewTemporalFilter();
            spatialFilter.set(duringDoc);
        }
        else if (specifiedTime instanceof ITimePosition) {
            ITimePosition timePosition = (ITimePosition) specifiedTime;
            String timeIso8601Format = timePosition.toISO8601Format();
            addEqualsTimePositionFilter(xb_getObs, timeIso8601Format);
        } else if (specifiedTime instanceof ITime) {
            ITime timePosition = (ITime) specifiedTime;
            String timeIso8601Format = timePosition.toISO8601Format();
            // fix for time parameter: in 52N SOS 2.0.0 now the time parameter for "getFirst" changed to "first"
            if (timeIso8601Format.equals(GET_OBSERVATION_TIME_PARAM_FIRST)) {
            	timeIso8601Format = GET_ABSERVATION_NEW_TIME_PARAM_FIRST;
            }
            addEqualsTimePositionFilter(xb_getObs, timeIso8601Format);
        }
    }

    private void addEqualsTimePositionFilter(GetObservationType xb_getObs, String timeIso8601Format) {
        TimePositionDocument timePositionDoc = TimePositionDocument.Factory.newInstance();
        TimePositionType timePositionType = timePositionDoc.addNewTimePosition();
        timePositionType.setStringValue(timeIso8601Format);
        timePositionType.set(timePositionType);

        TimeInstantDocument timeInstanceDoc = TimeInstantDocument.Factory.newInstance();
        TimeInstantType xb_timeInstant = timeInstanceDoc.addNewTimeInstant();
        xb_timeInstant.setTimePosition(timePositionType);
        xb_timeInstant.setId("_1");

        TEqualsDocument equalsDoc = TEqualsDocument.Factory.newInstance();
        BinaryTemporalOpType xb_binTempOp = equalsDoc.addNewTEquals();
        
        xb_binTempOp.set(timeInstanceDoc);
        xb_binTempOp.setValueReference("phenomenonTime");

        TemporalFilter spatialFilter = xb_getObs.addNewTemporalFilter();
        spatialFilter.set(equalsDoc);
    }



    private boolean isFirstOrLast(String param) {
        return param.equals(GET_OBSERVATION_TIME_PARAM_FIRST) || param.equals(GET_OBSERVATION_TIME_PARAM_LAST);
    }
}
