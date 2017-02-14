/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.server.io;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.imageio.ImageIO.write;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Map;

import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.jfree.chart.JFreeChart;
import org.n52.oxf.OXFException;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.io.render.DesignDescriptionList;
import org.n52.server.io.render.DiagramRenderer;
import org.n52.server.io.render.RenderingDesign;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;

public class DiagramGenerator extends Generator {

    public static final String FORMAT = "jpg";

    /**
     * Creates a time series chart diagram and writes it to the OutputStream.
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

        JFreeChart diagramChart = renderer.renderChart(entireCollMap, options, begin, end, compress);
        diagramChart.removeLegend();
        
        // draw chart into image:
        BufferedImage diagramImage = new BufferedImage(width, height, TYPE_INT_RGB);
        Graphics2D chartGraphics = diagramImage.createGraphics();
        chartGraphics.setColor(Color.white);
        chartGraphics.fillRect(0, 0, width, height);

        diagramChart.draw(chartGraphics, new Rectangle2D.Float(0, 0, width, height));

        JPEGImageWriteParam p = new JPEGImageWriteParam(null);
        p.setCompressionMode(JPEGImageWriteParam.MODE_DEFAULT);
        write(diagramImage, FORMAT, out);
    }

    public void createLegend(DesignOptions options, OutputStream out) throws OXFException, IOException {

        int topMargin = 10;
        int leftMargin = 30;
        int iconWidth = 15;
        int iconHeight = 15;
        int verticalSpaceBetweenEntries = 20;
        int horizontalSpaceBetweenIconAndText = 15;

        DesignDescriptionList ddList = buildUpDesignDescriptionList(options);
        int width = 800;
        int height = topMargin + (ddList.size() * (iconHeight + verticalSpaceBetweenEntries));

        BufferedImage legendImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D legendGraphics = legendImage.createGraphics();
        legendGraphics.setColor(Color.white);
        legendGraphics.fillRect(0, 0, width, height);

        int offset = 0;
        for (RenderingDesign dd : ddList.getAllDesigns()) {
            int yPos = topMargin + offset * iconHeight + offset * verticalSpaceBetweenEntries;

            // icon:
            legendGraphics.setColor(dd.getColor());
            legendGraphics.fillRect(leftMargin, yPos, iconWidth, iconHeight);

            // text:
            legendGraphics.setColor(Color.black);

            legendGraphics.drawString(dd.getFeature().getLabel() + " - "
                    + dd.getLabel(), leftMargin + iconWidth
                    + horizontalSpaceBetweenIconAndText, yPos + iconHeight);
            
            offset++;
        }

        JPEGImageWriteParam p = new JPEGImageWriteParam(null);
        p.setCompressionMode(JPEGImageWriteParam.MODE_DEFAULT);
        write(legendImage, FORMAT, out);
    }

    /**
     * Builds up a DesignDescriptionList object which stores the information
     * about the style of each timeseries.
     */
    private DesignDescriptionList buildUpDesignDescriptionList(DesignOptions options) {

        String domainAxisLabel;
        if (options.getLanguage() != null && options.getLanguage().equals("de")) {
            domainAxisLabel = "Zeit";
        } else { // default => "en"
            domainAxisLabel = "Time";
        }

        DesignDescriptionList ddList = new DesignDescriptionList(domainAxisLabel);
        String observedPropertyWithGrid = options.getProperties().get(0).getPhenomenon();

        for (TimeseriesProperties prop : options.getProperties()) {
            Color c = JavaHelper.transformToColor(prop.getHexColor());
            boolean gridOn = observedPropertyWithGrid.equals(prop.getPhenomenon());
            ddList.add(prop, c,  prop.getGraphStyle(), prop.getLineWidth(), gridOn);
        }

        return ddList;
    }

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {
        throw new UnsupportedOperationException();
    }

}