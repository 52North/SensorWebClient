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
package org.n52.server.oxf.util.generator;


import java.io.File;
import java.util.Collection;
import java.util.Date;

import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsGenerator extends Generator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(XlsGenerator.class);

    /** The zip. */
    private boolean zip;


    /**
     * Instantiates a new xls generator.
     * 
     * @param zip
     *            the zip
     */
    public XlsGenerator(boolean zip, String folder) {
        this.zip = zip;
        this.folderPostfix = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.server.oxf.util.generator.Generator#producePresentation(org.n52
     * .shared.serializable.pojos.RepresentationDesignOptions)
     */
    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws Exception {
        Collection<OXFFeatureCollection> observationCollList = getFeatureCollectionFor(options, false).values();

        if (observationCollList.size() != 1) {
            throw new IllegalArgumentException(
                    "Just ONE observation collection for ONE SOS-offering allowed."); //$NON-NLS-1$
        }

        OXFFeatureCollection entireColl =
                (OXFFeatureCollection) observationCollList.toArray()[0];
        
        TimeseriesProperties pc = options.getProperties().get(0);
        File xls = JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, pc.getProcedure().getId().replaceAll("/", "_")+"_"+formatDate(new Date(options.getBegin()))+"_"+formatDate(
                        new Date(options.getEnd()))+"_", "xls");
        try {

            WritableWorkbook workbook = Workbook.createWorkbook(xls);
            WritableSheet sheet = workbook.createSheet("Export", 0); //$NON-NLS-1$

            //
            // set header:
            //
            sheet.addCell(new Label(0, 0, "Sensor Station")); //$NON-NLS-1$
            sheet.addCell(new Label(1, 0, "Sensor Phenomenon")); //$NON-NLS-1$
            sheet.addCell(new Label(2, 0, "Date")); //$NON-NLS-1$
            sheet.addCell(new Label(3, 0, "Value")); //$NON-NLS-1$

            //
            // fill cells:
            // //
            for (TimeseriesProperties prop : options.getProperties()) {
                String foiID = prop.getFoi().getId();
                String obsPropsID = prop.getPhenomenon().getId();

                ObservationSeriesCollection seriesCollection =
                        new ObservationSeriesCollection(entireColl, new String[] { foiID },
                                new String[] { obsPropsID }, false);
                ITimePosition timeArray[] = seriesCollection.getSortedTimeArray();
                

                int counter = 0;
                if (timeArray.length > 0) {
					ObservedValueTuple prevObservation;
					ObservedValueTuple nextObservation = seriesCollection
							.getTuple(
									new OXFFeature(foiID, entireColl
											.getFeatureType()), timeArray[0]);
					ObservedValueTuple observation = nextObservation;
					
					DateFormat customDateFormat = new DateFormat(
							"dd MMM yyyy hh:mm:ss");
					WritableCellFormat dateFormat = new WritableCellFormat(
							customDateFormat);
					for (int i = 0; i < timeArray.length; i++) {

						prevObservation = observation;
						observation = nextObservation;

						if (i + 1 < timeArray.length) {
							nextObservation = seriesCollection.getTuple(
									new OXFFeature(foiID, null),
									timeArray[i + 1]);
						}

						String obsVal = observation.getValue(0).toString();
						String prevObsVal = prevObservation.getValue(0)
								.toString();
						String nextObsVal = nextObservation.getValue(0)
								.toString();

						//                    if ((i == 0) || // first observation --> in
						//                            (i == timeArray.length - 1) || // last
						//                            // observation
						//                            // --> in
						//                            (!(prevObsVal.equals(obsVal) && nextObsVal.equals(obsVal)))) {

						// FOI
						sheet.addCell(new Label(0, counter + 1, prop.getFoi()
								.getLabel()));

						// ObservedProperty
						sheet.addCell(new Label(1, counter + 1, prop.getPhenomenon()
								.getLabel()
								+ " ("
								+ prop.getPhenomenon().getUnitOfMeasure() + ")"));

						// Time
						ITimePosition timePos = (ITimePosition) observation
								.getTime();
						Date date = timePos.getCalendar().getTime();

						sheet.addCell(new DateTime(2, counter + 1, date,
								dateFormat));

						// Value
						Object value = observation.getValue(0);
						if (value instanceof Double) {
							WritableCellFormat customFormat = new WritableCellFormat(
									NumberFormats.FLOAT);
							sheet.addCell(new jxl.write.Number(3, counter + 1,
									(Double) value, customFormat));
						} else {
							sheet.addCell(new Label(3, counter + 1, value
									.toString()));
						}

						counter++;
						//                    }
					}
				}
				LOGGER.info("Reduced timeArray from {} to {}", timeArray.length, counter);

            }
            workbook.write();
            workbook.close();
        } catch (Exception e) {
            throw new Exception("Error creating XLS!", e);
        }
        LOGGER.debug("Produced XLS file url '{}'.", ConfigurationContext.GEN_URL + "/" + xls.getName());
        JavaHelper.cleanUpDir(ConfigurationContext.GEN_DIR, ConfigurationContext.FILE_KEEPING_TIME);
        return new FileResponse(ConfigurationContext.GEN_URL + "/" + xls.getName());
    }
}