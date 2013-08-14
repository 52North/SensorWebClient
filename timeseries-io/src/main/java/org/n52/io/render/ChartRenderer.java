
package org.n52.io.render;

import static org.n52.io.render.BarRenderer.BAR_CHART_TYPE;
import static org.n52.io.render.LineRenderer.LINE_CHART_TYPE;

import java.util.Date;

import javax.servlet.ServletOutputStream;

import org.joda.time.Interval;
import org.n52.io.v1.data.StyleProperties;
import org.n52.io.v1.data.TimeseriesDataCollection;

public abstract class ChartRenderer {

    public abstract void setLanguage(String language);

    public abstract void setMimeType(String mimetype);

    public abstract void setShowTooltips(boolean tooltips);

    public abstract void setDrawLegend(boolean drawLegend);

    public abstract void setContext(RenderingContext context);

    public abstract void writeToOutputStream(TimeseriesDataCollection data,
                                             ServletOutputStream stream);

    protected boolean isLineStyle(StyleProperties properties) {
        return LINE_CHART_TYPE.equals(properties.getType());
    }

    protected boolean isBarStyle(StyleProperties properties) {
        return BAR_CHART_TYPE.equals(properties.getType());
    }

    protected Date getStartTime(String timespan) {
        Interval interval = Interval.parse(timespan);
        return interval.getStart().toDate();
    }

    protected Date getEndTime(String timespan) {
        Interval interval = Interval.parse(timespan);
        return interval.getEnd().toDate();
    }

}
