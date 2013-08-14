
package org.n52.io.render;

import static java.awt.Color.BLACK;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static org.jfree.chart.ChartFactory.createTimeSeriesChart;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RectangleInsets;
import org.n52.io.v1.data.DesignedParameterSet;
import org.n52.io.v1.data.StyleProperties;
import org.n52.io.v1.data.TimeseriesDataCollection;
<<<<<<< HEAD
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultChartRenderer extends ChartRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultChartRenderer.class);

    private RenderingContext context;

    private boolean tooltips;

    private String language;

    private String mimeType;

    private boolean drawLegend;

    private boolean showGrid;

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public void setShowTooltips(boolean tooltips) {
        this.tooltips = tooltips;
    }

    @Override
    public void setDrawLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
    }
    
    @Override
    public void setMimeType(String mimetype) {
        this.mimeType = mimetype;
    }

    @Override
    public void setContext(RenderingContext context) {
        this.context = context;
    }
    
    public DesignedParameterSet getStyledTimeseries() {
        return context.getStyledTimeseries();
    }
    
    public TimeseriesMetadataOutput[] getTimeseriesMetadatas() {
        return context.getTimeseriesMetadatas();
    }

    @Override
    public void writeToOutputStream(TimeseriesDataCollection data,
                                    ServletOutputStream stream) {
        try {

            
            
            /*
             * As each timeseries may define its custom styling and different chart types we have to iterate
             * over all styles and process chart rendering iteratively
             */

            for (String timeseriesId : getStyledTimeseries().getTimeseries()) {
                StyleProperties properties = getStyledTimeseries().getStyleOptions(timeseriesId);
                if (isLineStyle(properties)) {

                    JFreeChart chart = createTimeSeriesChart(null, "Time", "Value", null, drawLegend, tooltips, true);
                    XYPlot xyPlot = createPlotArea(chart);
                    
                    String[] phenomena = getAllPhenomena(getTimeseriesMetadatas());
                    
                    
                    // do a line chart run

                }
                else if (isBarStyle(properties)) {

                    
                    // do a bar chart run

                }
            }

        }
        finally {
            try {
                stream.flush();
                stream.close();
            }
            catch (IOException e) {
                LOGGER.debug("Stream already flushed and closed.");
            }
        }

    }

    private String[] getAllPhenomena(TimeseriesMetadataOutput[] timeseriesMetadatas) {
        List<String> allPhenomena = new ArrayList<String>();
        for (TimeseriesMetadataOutput metadata : timeseriesMetadatas) {
=======
import org.n52.io.v1.data.TimeseriesMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultChartRenderer extends ChartRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultChartRenderer.class);

    private RenderingContext context;

    private boolean tooltips;

    private String language;

    private String mimeType;

    private boolean drawLegend;

    private boolean showGrid;

    @Override
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public void setShowTooltips(boolean tooltips) {
        this.tooltips = tooltips;
    }

    @Override
    public void setDrawLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
    }
    
    @Override
    public void setMimeType(String mimetype) {
        this.mimeType = mimetype;
    }

    @Override
    public void setContext(RenderingContext context) {
        this.context = context;
    }
    
    public DesignedParameterSet getStyledTimeseries() {
        return context.getStyledTimeseries();
    }
    
    public TimeseriesMetadata[] getTimeseriesMetadatas() {
        return context.getTimeseriesMetadatas();
    }

    @Override
    public void writeToOutputStream(TimeseriesDataCollection data,
                                    ServletOutputStream stream) {
        try {

            
            
            /*
             * As each timeseries may define its custom styling and different chart types we have to iterate
             * over all styles and process chart rendering iteratively
             */

            for (String timeseriesId : getStyledTimeseries().getTimeseries()) {
                StyleProperties properties = getStyledTimeseries().getStyleOptions(timeseriesId);
                if (isLineStyle(properties)) {

                    JFreeChart chart = createTimeSeriesChart(null, "Time", "Value", null, drawLegend, tooltips, true);
                    XYPlot xyPlot = createPlotArea(chart);
                    
                    String[] phenomena = getAllPhenomena(getTimeseriesMetadatas());
                    
                    
                    // do a line chart run

                }
                else if (isBarStyle(properties)) {

                    
                    // do a bar chart run

                }
            }

        }
        finally {
            try {
                stream.flush();
                stream.close();
            }
            catch (IOException e) {
                LOGGER.debug("Stream already flushed and closed.");
            }
        }

    }

    private String[] getAllPhenomena(TimeseriesMetadata[] timeseriesMetadatas) {
        List<String> allPhenomena = new ArrayList<String>();
        for (TimeseriesMetadata metadata : timeseriesMetadatas) {
>>>>>>> branch 'master' of ssh://git@github.com/ridoo/SensorWebClient.git
            
            // TODO get all phenomena labels from timeseriesIds
            
        }
        return null;
        
    }

    private XYPlot createPlotArea(JFreeChart chart) {
        XYPlot xyPlot = chart.getXYPlot();
        xyPlot.setBackgroundPaint(WHITE);
        xyPlot.setDomainGridlinePaint(LIGHT_GRAY);
        xyPlot.setRangeGridlinePaint(LIGHT_GRAY);
        xyPlot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        showCrosshairsOnAxes(xyPlot);
        showGridlinesOnChart(xyPlot);
        configureTimeAxis(xyPlot);
        return xyPlot;
    }

    private void showCrosshairsOnAxes(XYPlot xyPlot) {
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
    }

    private void showGridlinesOnChart(XYPlot xyPlot) {
        xyPlot.setDomainGridlinesVisible(showGrid);
        xyPlot.setRangeGridlinesVisible(showGrid);
    }

    private void configureTimeAxis(XYPlot xyPlot) {
        String timespan = getStyledTimeseries().getTimespan();
        DateAxis timeAxis = (DateAxis) xyPlot.getDomainAxis();
        timeAxis.setRange(getStartTime(timespan), getEndTime(timespan));
        timeAxis.setDateFormatOverride(new SimpleDateFormat());
    }

    static class LabelConstants {
        static final int FONT_SIZE = 12;
        static final Font LABEL = new Font("Arial", BOLD, FONT_SIZE);
        static final Color COLOR = BLACK;
    }

}
