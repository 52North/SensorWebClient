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
package org.n52.server.io.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.util.JavaHelper;
import org.n52.server.io.MetadataInURLGenerator;
import org.n52.server.io.TimeseriesFactory;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.Axis;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;

public class DiagramRenderer {

    private static final String DOTTED = "3";

    private static final String AREA = "2";

    private static final String LINE = "1";

    private static final int TICK_FONT_SIZE = 10;

    private static final int LABEL_FONT_SIZE = 12;

    private static final Color LABEL_COLOR = new Color(0, 0, 0);

    private static final String LABEL_FONT = "Arial";

    private final Font label = new Font(LABEL_FONT, Font.BOLD, LABEL_FONT_SIZE);

    private final Font tickLabelDomain = new Font(LABEL_FONT, Font.PLAIN, TICK_FONT_SIZE);

    private HashMap<String, Axis> axisMapping = new HashMap<String, Axis>();

    private boolean isOverview = false;

    public DiagramRenderer(boolean isOverview) {
        this.isOverview = isOverview;
    }

    public HashMap<String, Axis> getAxisMapping() {
        return this.axisMapping;
    }

    /**
     * Builds up a DesignDescriptionList which stores the information
     * about the style of each timeseries.
     *
     * @param options
     *            the options
     * @return the design description list
     */
    private DesignDescriptionList buildUpDesignDescriptionList(DesignOptions options) {

        String domainAxisLabel;
        if (options.getLanguage() != null && options.getLanguage().equals("de")) {
            domainAxisLabel = "Zeit";
        } else { // default => "en"
            domainAxisLabel = "Time";
        }
        if (this.isOverview) {
            domainAxisLabel = null;
        }

        DesignDescriptionList designDescriptions = new DesignDescriptionList(domainAxisLabel);
        String observedPropertyWithGrid = options.getProperties().get(0).getPhenomenon();

        for (TimeseriesProperties tsProperties : options.getProperties()) {
            Color c = JavaHelper.transformToColor(tsProperties.getHexColor());
            String phenomenonId = tsProperties.getPhenomenon();
            boolean drawGrid = observedPropertyWithGrid.equals(phenomenonId);

            designDescriptions.add(tsProperties, new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) tsProperties
                    .getOpacity() * 255 / 100), tsProperties.getLineStyle(), tsProperties.getLineWidth(), drawGrid);
        }
        return designDescriptions;
    }


    /**
     * <pre>
     * dataset :=  associated to one range-axis;
     * corresponds to one observedProperty;
     * may contain multiple series;
     * series :=   corresponds to a time series for one foi
     * </pre>
     *
     * .
     *
     * @param entireCollMap
     *            the entire coll map
     * @param options
     *            the options
     * @param begin
     *            the begin
     * @param end
     *            the end
     * @param compress
     * @return the j free chart
     */
    public JFreeChart renderChart(Map<String, OXFFeatureCollection> entireCollMap,
            DesignOptions options, Calendar begin, Calendar end, boolean compress) {

        DesignDescriptionList designDescriptions = buildUpDesignDescriptionList(options);

        /*** FIRST RUN ***/
        JFreeChart chart = initializeTimeSeriesChart();
        chart.setBackgroundPaint(Color.white);

		if (!this.isOverview) {
            chart.addSubtitle(new TextTitle(ConfigurationContext.COPYRIGHT, new Font(LABEL_FONT, Font.PLAIN, 9),
                    Color.black, RectangleEdge.BOTTOM, HorizontalAlignment.RIGHT, VerticalAlignment.BOTTOM,
                    new RectangleInsets(0, 0, 20, 20)));
        }
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        plot.setDomainGridlinesVisible(options.getGrid());
        plot.setRangeGridlinesVisible(options.getGrid());

        // add additional datasets:
        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setRange(begin.getTime(), end.getTime());
        dateAxis.setDateFormatOverride(new SimpleDateFormat());
        dateAxis.setTimeZone(end.getTimeZone());




        // add all axes
        String[] phenomenaIds = options.getAllPhenomenIds();
        // all the axis indices to map them later
        HashMap<String, Integer> axes = new HashMap<String, Integer>();
        for (int i = 0; i < phenomenaIds.length; i++) {
            axes.put(phenomenaIds[i], i);
            plot.setRangeAxis(i, new NumberAxis(phenomenaIds[i]));
        }

        // list range markers
        ArrayList<ValueMarker> referenceMarkers = new ArrayList<ValueMarker>();
        HashMap<String, double[]> referenceBounds = new HashMap<String, double[]>();

        // create all TS collections
        for (int i = 0; i < options.getProperties().size(); i++) {

            TimeseriesProperties prop = options.getProperties().get(i);

            String phenomenonId = prop.getPhenomenon();

            TimeSeriesCollection dataset = createDataset(entireCollMap, prop, phenomenonId, compress);
            dataset.setGroup(new DatasetGroup(prop.getTimeseriesId()));
            XYDataset additionalDataset = dataset;

            NumberAxis axe = (NumberAxis) plot.getRangeAxis(axes.get(phenomenonId));

            if (this.isOverview) {
				axe.setAutoRange(true);
				axe.setAutoRangeIncludesZero(false);
			} else if (prop.getAxisUpperBound() == prop.getAxisLowerBound() || prop.isAutoScale()) {
				if (prop.isZeroScaled()) {
					axe.setAutoRangeIncludesZero(true);
				} else {
					axe.setAutoRangeIncludesZero(false);
				}
			} else {
				if (prop.isZeroScaled()) {
					if (axe.getUpperBound() < prop.getAxisUpperBound()) {
						axe.setUpperBound(prop.getAxisUpperBound());
					}
					if (axe.getLowerBound() > prop.getAxisLowerBound()) {
						axe.setLowerBound(prop.getAxisLowerBound());
					}
				} else {
					axe.setRange(prop.getAxisLowerBound(), prop.getAxisUpperBound());
					axe.setAutoRangeIncludesZero(false);
				}
			}

            plot.setDataset(i, additionalDataset);
            plot.mapDatasetToRangeAxis(i, axes.get(phenomenonId));

            // set bounds new for reference values
            if (!referenceBounds.containsKey(phenomenonId)) {
            	double[] bounds = new double[]{axe.getLowerBound(),axe.getUpperBound()};
            	referenceBounds.put(phenomenonId, bounds);
			} else {
				double[] bounds = referenceBounds.get(phenomenonId);
				if (bounds[0] >= axe.getLowerBound()) {
					bounds[0] = axe.getLowerBound();
				}
				if (bounds[1] <= axe.getUpperBound()) {
					bounds[1] = axe.getUpperBound();
				}
			}

            double[] bounds = referenceBounds.get(phenomenonId);
            for (String string : prop.getReferenceValues()) {
                if (prop.getRefValue(string).show()) {
                    Double value = prop.getRefValue(string).getValue();
                    if (value <= bounds[0]) {
                        bounds[0] = value;
                    } else if (value >= bounds[1]) {
                        bounds[1] = value;
                    }
                }
            }

            Axis axis = prop.getAxis();
            if (axis == null) {
                axis = new Axis(axe.getUpperBound(), axe.getLowerBound());
            } else if (prop.isAutoScale()) {
                axis.setLowerBound(axe.getLowerBound());
                axis.setUpperBound(axe.getUpperBound());
                axis.setMaxY(axis.getMaxY());
                axis.setMinY(axis.getMinY());
            }
            prop.setAxisData(axis);
            this.axisMapping.put(prop.getTimeseriesId(), axis);

            for (String string : prop.getReferenceValues()) {
                if (prop.getRefValue(string).show()) {
                	referenceMarkers.add(new ValueMarker(prop.getRefValue(string).getValue(), Color.decode(prop
                            .getRefValue(string).getColor()), new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND, 1.0f)));
                }
            }

            plot.mapDatasetToRangeAxis(i, axes.get(phenomenonId));
        }

        for (ValueMarker valueMarker : referenceMarkers) {
			plot.addRangeMarker(valueMarker);
		}

        // show actual time
        ValueMarker nowMarker = new ValueMarker(System.currentTimeMillis(), Color.orange, new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 1.0f));
		plot.addDomainMarker(nowMarker);

        if (!this.isOverview) {
        	Iterator<Entry<String, double[]>> iterator = referenceBounds.entrySet().iterator();
        	while (iterator.hasNext()) {
        		Entry<String, double[]> boundsEntry = iterator.next();
        		String phenId = boundsEntry.getKey();
                NumberAxis axe = (NumberAxis) plot.getRangeAxis(axes.get(phenId));
        		axe.setAutoRange(true);
        		// add a margin
        		double marginOffset = (boundsEntry.getValue()[1] - boundsEntry.getValue()[0]) / 25;
        		boundsEntry.getValue()[0] -= marginOffset;
        		boundsEntry.getValue()[1] += marginOffset;
                axe.setRange(boundsEntry.getValue()[0], boundsEntry.getValue()[1]);
        	}
		}










        /**** SECOND RUN ***/

        // set domain axis labels:
        plot.getDomainAxis().setLabelFont(label);
        plot.getDomainAxis().setLabelPaint(LABEL_COLOR);
        plot.getDomainAxis().setTickLabelFont(tickLabelDomain);
        plot.getDomainAxis().setTickLabelPaint(LABEL_COLOR);
        plot.getDomainAxis().setLabel(designDescriptions.getDomainAxisLabel());

        // define the design for each series:
        for (int datasetIndex = 0; datasetIndex < plot.getDatasetCount(); datasetIndex++) {
            TimeSeriesCollection dataset = (TimeSeriesCollection) plot.getDataset(datasetIndex);

            for (int seriesIndex = 0; seriesIndex < dataset.getSeriesCount(); seriesIndex++) {

                String timeseriesId = (String) dataset.getSeries(seriesIndex).getKey();
                RenderingDesign dd = designDescriptions.get(timeseriesId);

                if (dd != null) {

                    // LINESTYLE:
                    String lineStyle = dd.getLineStyle();
                    int width = dd.getLineWidth();
                    if (this.isOverview) {
                    	width = width / 2;
                    	width = (width == 0) ? 1 : width;
					}
                    // "1" is lineStyle "line"
                    if (lineStyle.equalsIgnoreCase(LINE)) {
                    	XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(true, false);
                    	ren.setStroke(new BasicStroke(width));
                        plot.setRenderer(datasetIndex, ren);
                    }
                    // "2" is lineStyle "area"
                    else if (lineStyle.equalsIgnoreCase(AREA)) {
                        plot.setRenderer(datasetIndex, new XYAreaRenderer());
                    }
                    // "3" is lineStyle "dotted"
                    else if (lineStyle.equalsIgnoreCase(DOTTED)) {
                        XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(false, true);
						ren.setShape(new Ellipse2D.Double(-width, -width,
								2 * width, 2 * width));
                        plot.setRenderer(datasetIndex, ren);

                    }
                    // "4" is dashed
                    else if (lineStyle.equalsIgnoreCase("4")) { // dashed
                        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
						renderer.setSeriesStroke(0, new BasicStroke(width,
								BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
								1.0f,
								new float[] { 4.0f * width, 4.0f * width },
								0.0f));
                        plot.setRenderer(datasetIndex, renderer);
                    } else if (lineStyle.equalsIgnoreCase("5")) {
                        // lines and dots
                        XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(true, true);
                        int thickness = 2 * width;
						ren.setShape(new Ellipse2D.Double(-width, -width, thickness, thickness));
                        ren.setStroke(new BasicStroke(width));
                        plot.setRenderer(datasetIndex, ren);
                    } else {
                        // default is lineStyle "line"
                        plot.setRenderer(datasetIndex, new XYLineAndShapeRenderer(true, false));
                    }

                    plot.getRenderer(datasetIndex).setSeriesPaint(seriesIndex, dd.getColor());

                    // plot.getRenderer(datasetIndex).setShapesVisible(true);

                    XYToolTipGenerator toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();
                    XYURLGenerator urlGenerator = new MetadataInURLGenerator(designDescriptions);

                    plot.getRenderer(datasetIndex).setBaseToolTipGenerator(toolTipGenerator);
                    plot.getRenderer(datasetIndex).setURLGenerator(urlGenerator);

                    // GRID:
                    // PROBLEM: JFreeChart only allows to switch the grid on/off
                    // for the whole XYPlot. And the
                    // grid will always be displayed for the first series in the
                    // plot. I'll always show the
                    // grid.
                    // --> plot.setDomainGridlinesVisible(visible)

                    // RANGE AXIS LABELS:
                    if (isOverview) {
                        plot.getRangeAxisForDataset(datasetIndex).setTickLabelsVisible(false);
                        plot.getRangeAxisForDataset(datasetIndex).setTickMarksVisible(false);
                        plot.getRangeAxisForDataset(datasetIndex).setVisible(false);
					} else {
						plot.getRangeAxisForDataset(datasetIndex).setLabelFont(label);
                        plot.getRangeAxisForDataset(datasetIndex).setLabelPaint(LABEL_COLOR);
                        plot.getRangeAxisForDataset(datasetIndex).setTickLabelFont(tickLabelDomain);
                        plot.getRangeAxisForDataset(datasetIndex).setTickLabelPaint(LABEL_COLOR);
                        StringBuilder unitOfMeasure = new StringBuilder();
                        unitOfMeasure.append(dd.getPhenomenon().getLabel());
                        String uomLabel = dd.getUomLabel();
                        if (uomLabel != null && !uomLabel.isEmpty()) {
                            unitOfMeasure.append(" (").append(uomLabel).append(")");
                        }
                        plot.getRangeAxisForDataset(datasetIndex).setLabel(unitOfMeasure.toString());
					}
				}
			}
		}
        return chart;
    }

    private JFreeChart initializeTimeSeriesChart() {
        String title = null;
        String xLabel = "Date";
        String yLabel = "";
        XYDataset data = null;
        boolean isCreateLegend = false;
        boolean isCreateTooltips = true;
        boolean isCreateURLs = true;
        return ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, data, isCreateLegend, isCreateTooltips, isCreateURLs);
    }

    protected JFreeChart renderPreChart(Map<String, OXFFeatureCollection> entireCollMap, String[] observedProperties,
            ArrayList<TimeSeriesCollection> timeSeries, Calendar begin, Calendar end) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // title
                "Date", // x-axis label
                observedProperties[0], // y-axis label
                timeSeries.get(0), // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
                );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);

        // add additional datasets:
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setRange(begin.getTime(), end.getTime());
        axis.setDateFormatOverride(new SimpleDateFormat());
        axis.setTimeZone(end.getTimeZone());
        for (int i = 1; i < observedProperties.length; i++) {
            XYDataset additionalDataset = timeSeries.get(i);
            plot.setDataset(i, additionalDataset);
            plot.setRangeAxis(i, new NumberAxis(observedProperties[i]));
            // plot.getRangeAxis(i).setRange((Double)
            // overAllSeriesCollection.getMinimum(i),
            // (Double) overAllSeriesCollection.getMaximum(i));
            plot.mapDatasetToRangeAxis(i, i);
            // plot.getDataset().getXValue(i, i);
        }
        return chart;
    }

    public TimeSeriesCollection createDataset(Map<String, OXFFeatureCollection> entireCollMap, TimeseriesProperties prop, String observedProperty, boolean compress) {

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        OXFFeatureCollection obsColl = entireCollMap.get(prop.getOffering() + "@" + prop.getServiceUrl());

        String foiID = prop.getFeature();
        String obsPropID = prop.getPhenomenon();
        String procID = prop.getProcedure();

        // only if the observation concerns the observedProperty, it
        // will be added to the dataset
        if (obsPropID.equals(observedProperty)) {

            String[] foiIds = new String[] { foiID };
            String[] procedureIds = new String[] { procID };
            String[] observedPropertyIds = new String[] { obsPropID };
            ObservationSeriesCollection seriesCollection = new ObservationSeriesCollection(obsColl, foiIds, observedPropertyIds, procedureIds, true);


            //
            // now let's put in the date-value pairs.
            // ! But put it only in if it differs from the previous
            // one !
            //



            TimeSeries timeSeries = new TimeSeries(prop.getTimeseriesId(), FixedMillisecond.class);




            TimeseriesFactory factory = new TimeseriesFactory(seriesCollection);
            if (seriesCollection.getSortedTimeArray().length > 0) {
                if (compress) {
                    timeSeries = factory.compressToTimeSeries(prop.getTimeseries(), isOverview, prop.getGraphStyle());
                } else {
                    timeSeries = factory.createTimeSeries(prop.getTimeseries(), prop.getGraphStyle());
                }
            }
            dataset.addSeries(timeSeries);
        }

        dataset.setDomainIsPointsInTime(true);

        return dataset;
    }
}