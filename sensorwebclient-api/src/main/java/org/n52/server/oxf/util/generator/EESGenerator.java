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

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.servlet.ServletUtilities;
import org.n52.oxf.OXFException;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.server.oxf.render.sos.DiagramRenderer;
import org.n52.server.oxf.util.ConfigurationContext;
import org.n52.shared.responses.EESDataResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.Axis;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EESGenerator extends Generator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EESGenerator.class);

    public static final String FORMAT = "jpg";

    private boolean isOverview = false;

    private DiagramRenderer renderer;

    public EESGenerator(boolean isOverview) {
        this.isOverview = isOverview;
        this.renderer = new DiagramRenderer(isOverview);
    }

    public EESGenerator() {
        renderer = new DiagramRenderer(isOverview);
    }

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {
        ChartRenderingInfo renderingInfo = new ChartRenderingInfo(new StandardEntityCollection());
        String chartUrl = createChart(options, renderingInfo);
        
        Rectangle2D plotArea = renderingInfo.getPlotInfo().getDataArea();
        for (Axis axis : renderer.getAxisMapping().values()) {
            axis.setMaxY(plotArea.getMaxY());
            axis.setMinY(plotArea.getMinY());
        }

        ImageEntity[] entities = {};
        if (!this.isOverview) {
            LOGGER.debug("Produced EES diagram " + chartUrl);
            entities = createImageEntities(renderingInfo.getEntityCollection());
        } else {
            LOGGER.debug("Produced EES Overview diagram " + chartUrl);
        }

        Bounds chartArea = new Bounds(plotArea.getMinX(), plotArea.getMaxX(), plotArea.getMinY(), plotArea.getMaxY());
        return new EESDataResponse(chartUrl, options, chartArea, entities, renderer.getAxisMapping());
    }

    public String createChart(DesignOptions options, ChartRenderingInfo renderingInfo) throws GeneratorException {
        try {
            Map<String, OXFFeatureCollection> entireCollMap = getFeatureCollectionFor(options, true);
            JFreeChart chart = producePresentation(entireCollMap, options);
            chart.removeLegend();
            
            String chartFileName = createAndSaveImage(options, chart, renderingInfo);
            return ConfigurationContext.IMAGE_SERVICE + chartFileName; 
        } catch (Exception e) {
            throw new GeneratorException(e.getMessage(), e);
        }
    }

	public void createChartToOutputStream(DesignOptions options,
			ChartRenderingInfo renderingInfo, OutputStream outputStream) {
		try {
            Map<String, OXFFeatureCollection> entireCollMap = getFeatureCollectionFor(options, true);
            JFreeChart chart = producePresentation(entireCollMap, options);
            chart.removeLegend();
            int width = options.getWidth();
            int height = options.getHeight();
            ChartUtilities.writeChartAsPNG(outputStream, chart, width, height, renderingInfo);
        } catch (Exception e) {
            System.out.println("error");
        }
	}

	/**
	 * Creates the image entities.
	 * 
	 * @param entities
	 *            the entities
	 * @return the array list
	 */
	private ImageEntity[] createImageEntities(EntityCollection entities) {
		ArrayList<ImageEntity> imageEntities = new ArrayList<ImageEntity>();
		
		if (!this.isOverview) {
			// reducer
			int xyItemCount = 0;
			for (Iterator<?> iter = entities.iterator(); iter
					.hasNext();) {
				Object o = iter.next();
				if (o instanceof XYItemEntity) {
					xyItemCount++;
				}
			}
			int reducer = 1;
			if (xyItemCount > ConfigurationContext.TOOLTIP_MIN_COUNT) {
				reducer = xyItemCount
						/ ConfigurationContext.TOOLTIP_MIN_COUNT;
			}
			LOGGER.debug("Reduce " + xyItemCount + " Entities to "
					+ (xyItemCount / reducer) + " Tooltips");

			int counter = 0;
			for (Iterator<?> iter = entities.iterator(); iter.hasNext();) {
				counter++;
				Object o = iter.next();
				if ((counter % reducer) == 0) {
					if (o instanceof XYItemEntity) {
						XYItemEntity e = (XYItemEntity) o;

                        ImageEntity imageEntity =
                                new ImageEntity(new Bounds(e.getArea().getBounds2D().getMinX(), e.getArea()
                                        .getBounds2D().getMaxX(), e.getArea().getBounds2D().getMinY(), e.getArea()
                                        .getBounds2D().getMaxY()), e.getDataset().getGroup().getID());

                        double time = e.getDataset().getXValue(e.getSeriesIndex(), e.getItem());
                        double value = e.getDataset().getYValue(e.getSeriesIndex(), e.getItem());
                        String uom = e.getURLText().split(";")[0];
                        String color = e.getURLText().split(";")[1];
                        imageEntity.putHoverHtmlFragment(createHoverHtmlString(color, time, value, uom));
						imageEntities.add(imageEntity);
                    }
                }
            }
        }
        return imageEntities.toArray(new ImageEntity[imageEntities.size()]);
    }

    private String createHoverHtmlString(String color, double time, double value, String uom) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        StringBuilder html = new StringBuilder();
        html.append("<div style='background-color: #F6F3F2;border: 2px solid ");
        html.append("#").append(color).append(";");
        html.append("'>").append("<span class='n52_sensorweb_client_tooltip'>");
        html.append(f.format(new Date((long) time)));
        html.append(":&nbsp;").append(value).append(" ").append(uom);
        html.append("</span></div>");
        return html.toString();
    }

    private JFreeChart producePresentation(Map<String, OXFFeatureCollection> entireCollMap, DesignOptions options) throws OXFException, IOException {

        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTimeInMillis(options.getBegin());
        end.setTimeInMillis(options.getEnd());

        return renderer.renderChart(entireCollMap, options, begin, end, ConfigurationContext.FACADE_COMPRESSION);
    }

}
