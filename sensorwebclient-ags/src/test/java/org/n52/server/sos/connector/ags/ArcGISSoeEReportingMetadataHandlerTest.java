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
package org.n52.server.sos.connector.ags;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.server.da.oxf.ResponseFromFileSosAdapter;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;

import com.vividsolutions.jts.geom.Point;

public class ArcGISSoeEReportingMetadataHandlerTest {
    
    private static final String FAKE_URL = "http://points.nowhere";
    
    private static final String VERSION_200 = "2.0.0";

    private ArcGISSoeEReportingMetadataHandlerSeam seam;

    private SOSMetadata metadata;

    
    @Before public void
    setUp() {
        seam = new ArcGISSoeEReportingMetadataHandlerSeam();
        metadata = seam.initMetadata();
    }
    
    @Test public void
    shouldInitEReportingCapabilities() {
        assertNotNull(metadata);
    }
    
//    @Test
    public void
    shouldPerformMetadataCompletion() throws Exception {
        
        // TODO
        SOSMetadata metadata = seam.performMetadataCompletion();
        System.out.println(metadata);
        
    }
    
    @Test public void
    shouldParseCategoryFromPhenomenonLabelWithSingleBraceGroup() {
        String category = seam.parseCategory("Cadmium lajsdf (aerosol)");
        assertThat(category, is("aerosol"));
    }
    
    @Test public void
    shouldParseCategoryFromPhenomenonLabelWithMultipleBraceGroup() {
        String category = seam.parseCategory("Benzo(a)anthracene in PM10 (air+aerosol)");
        assertThat(category, is("air+aerosol"));
    }
    
    @Test public void
    shouldParseWholePhenomenonWhenNoBraceGroupAvailable() {
        String category = seam.parseCategory("aerosol");
        assertThat(category, is("aerosol"));
    }
    
    static class ArcGISSoeEReportingMetadataHandlerSeam extends ArcGISSoeEReportingMetadataHandler {
        
        private static final String CAPABILITIES_EREPORTING = "/files/capabilities-ereporting.xml";
        
        private static final String SENSOR_NETWORK = "/files/describe-sensor-network_subset.xml";

        private static final String GET_FOI_RESPONSE = "/files/get-features_all.xml";

        public ArcGISSoeEReportingMetadataHandlerSeam() {
            super(createAgsSosMetadata());
        }

        @Override
        protected SOSMetadata initMetadata() {
            setSosAdapter(new ResponseFromFileSosAdapter(CAPABILITIES_EREPORTING));
            return super.initMetadata();
        }

        @Override
        protected void performDescribeSensor(String procedure) throws OXFException, ExceptionReport {
            setSosAdapter(new ResponseFromFileSosAdapter(SENSOR_NETWORK));
            super.performDescribeSensor(procedure);
        }
        
        @Override
        protected Map<Feature, Point> performGetFeatureOfInterest(TimeseriesParametersLookup lookup) throws OXFException, ExceptionReport {
            setSosAdapter(new ResponseFromFileSosAdapter(GET_FOI_RESPONSE));
            return super.performGetFeatureOfInterest(lookup);
        }
        

    }
    
    private static SOSMetadata createAgsSosMetadata() {
        SOSMetadataBuilder builder = new SOSMetadataBuilder();
        builder
            .addServiceVersion(VERSION_200)
            .addServiceURL(FAKE_URL);
        return new SOSMetadata(builder);
    }

}
