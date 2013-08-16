package org.n52.io.render;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.n52.io.v1.data.TimeseriesDataCollection;


public class ChartRendererTest {
    
    private static final String VALID_ISO8601_RELATIVE_START = "PT6H/2013-08-13TZ";
    
    private static final String VALID_ISO8601_ABSOLUTE_START = "2013-07-13TZ/2013-08-13TZ";
    
    private MyChartRenderer chartRenderer;

    @Before public void
    setUp() {
        this.chartRenderer = new MyChartRenderer(RenderingContext.createEmpty());
    }
    
    
    @Test public void
    shouldParseBeginFromIso8601PeriodWithRelativeStart() {
        Date start = chartRenderer.getStartTime(VALID_ISO8601_RELATIVE_START);
        assertThat(start, is(DateTime.parse("2013-08-13TZ").minusHours(6).toDate()));
    }
    
    @Test public void
    shouldParseBeginFromIso8601PeriodWithAbsoluteStart() {
        Date start = chartRenderer.getStartTime(VALID_ISO8601_ABSOLUTE_START);
        assertThat(start, is(DateTime.parse("2013-08-13TZ").minusMonths(1).toDate()));
    }

    static class MyChartRenderer extends ChartRenderer {

        public MyChartRenderer(RenderingContext context) {
            super(context);
        }

        @Override
        public void setLanguage(String language) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMimeType(String mimetype) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setShowTooltips(boolean tooltips) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDrawLegend(boolean drawLegend) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void renderChart(TimeseriesDataCollection data) {
            throw new UnsupportedOperationException();
        }

    }
}
