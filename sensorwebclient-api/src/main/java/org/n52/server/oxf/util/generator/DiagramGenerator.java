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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.n52.oxf.OXFException;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.oxf.render.sos.DesignDescriptionList;
import org.n52.server.oxf.render.sos.DesignDescriptionList.DesignDescription;
import org.n52.server.oxf.render.sos.DiagramRenderer;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;

/**
 * The Class DiagramGenerator.
 * 
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 * @author <a href="mailto:tremmersmann@uni-muenster.de">Thomas Remmersmann</a>
 */
public class DiagramGenerator extends Generator {

    /** The Constant FORMAT. */
    public static final String FORMAT = "jpg"; //$NON-NLS-1$

    /**
     * Produce presentation.
     * 
     * @param entireCollMap
     *            the entire coll map
     * @param options
     *            the options
     * @param out
     *            the out
     * @throws OXFException
     *             the oXF exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    // public void producePresentation(GetRepresentation getRepresentationOp,
    // OutputStream out) throws OXFException {
    //
    // // request features:
    // Map<String, OXFFeatureCollection> entireCollMap = receiveObservations(
    // getRepresentationOp, false);
    //
    // producePresentation(entireCollMap, tupleMap, getRepresentationOp, out);
    // }

    /**
     * creates a time series chart diagram and writes it to the OutputStream.
     * 
     * @param entireCollMap
     * @param options
     * @param out
     * @throws OXFException
     * @throws IOException
     */
    public void producePresentation(Map<String, OXFFeatureCollection> entireCollMap,
            DesignOptions options, FileOutputStream out, boolean compress) throws OXFException,
            IOException {

        // render features:
        int width = options.getWidth();
        int height = options.getHeight();
        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(options.getBegin());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(options.getEnd());

        DiagramRenderer renderer = new DiagramRenderer(false);

        // String[] observedProperties = options.getAllObservedProperties();
        //
        // ArrayList<TimeSeriesCollection> timeSeries = new
        // ArrayList<TimeSeriesCollection>();

        // alle vorhandenen pehnomena hinzufügen
        // for (PropertiesContainer prop : options.getProperties()) {
        //
        // TimeSeriesCollection dataset = renderer.createDataset(entireCollMap,
        // prop, prop.getPhen().getID());
        //
        // timeSeries.add(dataset);
        //
        // }

        JFreeChart diagramChart = renderer.renderChart(entireCollMap, options, begin, end, compress);
        diagramChart.removeLegend();
        // draw chart into image:
        BufferedImage diagramImage =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D chartGraphics = diagramImage.createGraphics();
        chartGraphics.setColor(Color.white);
        chartGraphics.fillRect(0, 0, width, height);

        diagramChart.draw(chartGraphics, new Rectangle2D.Float(0, 0, width, height));

        JPEGEncodeParam p = new JPEGEncodeParam();
        p.setQuality(1f);
        ImageEncoder encoder = ImageCodec.createImageEncoder("jpeg", out, p);

        encoder.encode(diagramImage);
    }

    /**
     * Produce legend.
     * 
     * @param options
     *            the options
     * @param out
     *            the out
     * @throws OXFException
     *             the oXF exception
     */
    public void produceLegend(DesignOptions options, OutputStream out)
            throws OXFException, IOException {

        int topMargin = 10;
        int leftMargin = 30;
        int verticalSpaceBetweenEntries = 20;
        int iconWidth = 15;
        int iconHeight = 15;
        int horizontalSpaceBetweenIconAndText = 15;

        DesignDescriptionList ddList = buildUpDesignDescriptionList(options);
        int width = 800;
        int height = topMargin + (ddList.size() * (iconHeight + verticalSpaceBetweenEntries));

        BufferedImage legendImage =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D legendGraphics = legendImage.createGraphics();
        legendGraphics.setColor(Color.white);
        legendGraphics.fillRect(0, 0, width, height);

        for (int i = 0; i < ddList.size(); i++) {
            DesignDescription dd = ddList.get(i);

            // draw legend entry:
            int yPos = topMargin + i * iconHeight + i * verticalSpaceBetweenEntries;

            // icon:
            legendGraphics.setColor(dd.getColor());
            legendGraphics.fillRect(leftMargin, yPos, iconWidth, iconHeight);

            // text:
            legendGraphics.setColor(Color.black);

            legendGraphics.drawString(dd.getFeatureOfInterestDesc() + " - " //$NON-NLS-1$
                    + dd.getLabel(), leftMargin + iconWidth
                    + horizontalSpaceBetweenIconAndText, yPos + iconHeight);
        }

        // draw legend into image:
        JPEGEncodeParam p = new JPEGEncodeParam();
        p.setQuality(1f);
        ImageEncoder encoder = ImageCodec.createImageEncoder("jpeg", out, p);

        encoder.encode(legendImage);
    }

    /**
     * builds up a DesignDescriptionList object which stores the information
     * about the style of each timeseries.
     * 
     * @param options
     *            the options
     * @return the design description list
     */
    private DesignDescriptionList buildUpDesignDescriptionList(
            DesignOptions options) {

        String domainAxisLabel;
        if (options.getLanguage() != null && options.getLanguage().equals("de")) { //$NON-NLS-1$
            domainAxisLabel = "Zeit"; //$NON-NLS-1$
        } else { // default => "en"
            domainAxisLabel = "Time"; //$NON-NLS-1$
        }

        DesignDescriptionList ddList = new DesignDescriptionList(domainAxisLabel);

        String observedPropertyWithGrid = options.getProperties().get(0).getPhenomenon().getId();

        for (TimeseriesProperties prop : options.getProperties()) {

            Color c = JavaHelper.transformToColor(prop.getHexColor());
            boolean gridOn = observedPropertyWithGrid.equals(prop.getPhenomenon().getId());

            ddList.add(prop.getPhenomenon().getId(), prop.getProcedure().getId(), prop.getFoi().getId(),
                    prop.getPhenomenon().getLabel(), prop.getProcedure().getLabel(), prop
                            .getFoi().getLabel(), prop.getLabel(), prop.getUnitOfMeasure(), c,
                    prop.getGraphStyle(), prop.getLineWidth(), gridOn);
        }

        return ddList;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}