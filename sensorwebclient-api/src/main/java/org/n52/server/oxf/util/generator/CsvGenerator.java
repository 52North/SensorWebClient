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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;

import org.n52.oxf.feature.OXFFeature;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.feature.sos.ObservedValueTuple;
import org.n52.oxf.util.JavaHelper;
import org.n52.oxf.valueDomains.time.ITimePosition;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.exceptions.ServerException;
import org.n52.shared.exceptions.TimeoutException;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CsvGenerator.
 * 
 * @author <a href="mailto:tremmersmann@uni-muenster.de">Thomas Remmersmann</a>
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 */
public class CsvGenerator extends Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvGenerator.class);

    /** The zip. */
    @SuppressWarnings("unused")
	private boolean zip;

    /**
     * Instantiates a new csv generator.
     * 
     * @param zip
     *            the zip
     */
    public CsvGenerator(boolean zip, String folder) {
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
    public RepresentationResponse producePresentation(DesignOptions options)
            throws TimeoutException, ServerException {

        Collection<OXFFeatureCollection> observationCollList = getFeatureCollectionFor(options, false).values();

        if (observationCollList.size() != 1) {
            throw new IllegalArgumentException(
                    "Just ONE observation collection for ONE SOS-offering allowed.");
        }

        OXFFeatureCollection entireColl =
                (OXFFeatureCollection) observationCollList.toArray()[0];

        
        TimeSeriesProperties pc = options.getProperties().get(0);
        File csv = JavaHelper.genRndFile(ConfigurationContext.GEN_DIR+"/"+folderPostfix, pc.getProcedure().getId().replaceAll("/", "_")+"_"+formatDate(new Date(options.getBegin()))+"_"
                +formatDate(new Date(options.getEnd()))+"_", "csv");
        OutputStream out;
        try {
            out = new FileOutputStream(csv);
        } catch (FileNotFoundException e1) {
            LOGGER.error("Could not produce presentation.", e1);
            return null;
        }

        try {
            String csvString = "";

            // set header:
            csvString += "Sensor Station;Sensor Phenomenon;Date;Value\n";

            // fill cells:
			for (TimeSeriesProperties prop : options.getProperties()) {

				String foiID = prop.getFoi().getId();
				String obsPropsID = prop.getPhenomenon().getId();

				ObservationSeriesCollection seriesCollection = new ObservationSeriesCollection(
						entireColl, new String[] { foiID },
						new String[] { obsPropsID }, false);
				ITimePosition timeArray[] = seriesCollection
						.getSortedTimeArray();

				if (timeArray.length > 0) {
					ObservedValueTuple nextObservation = seriesCollection
							.getTuple(
									new OXFFeature(foiID, entireColl
											.getFeatureType()), timeArray[0]);
					ObservedValueTuple observation = nextObservation;

					for (int i = 0; i < timeArray.length; i++) {

						observation = nextObservation;

						if (i + 1 < timeArray.length) {
							nextObservation = seriesCollection.getTuple(
									new OXFFeature(foiID, null),
									timeArray[i + 1]);
						}

						csvString += prop.getFoi().getLabel() + ";";
						csvString += prop.getPhenomenon().getLabel()
								+ " (" + prop.getPhenomenon().getUnitOfMeasure() + ")"
								+ ";";
						csvString += observation.getTime().toISO8601Format()
								+ ";";
						csvString += observation.getValue(0).toString() + "\n";
						// csvString +=
						// observation.getValue(0).toString().replace(".", ",")
						// + "\n";
					}
				}
			}
            out.write(csvString.getBytes());
        } catch (Exception e) {
            throw new ServerException(e.getMessage(), e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                LOGGER.error("Could not produce presentation.", e);
            }
        }
        JavaHelper.cleanUpDir(ConfigurationContext.GEN_DIR, ConfigurationContext.FILE_KEEPING_TIME);
        LOGGER.debug("Produced CSV file url: " + ConfigurationContext.GEN_URL + "/" + csv.getName());
        return new FileResponse(ConfigurationContext.GEN_URL + "/" + csv.getName());
    }
}