
package org.n52.io.render;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.Color.decode;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.n52.io.style.LineStyle;

/**
 * Renders lines of different styles. Supported line styles are
 * <ul>
 * <li><code>solid</code></li>
 * <li><code>solidWithDots</code></li>
 * <li><code>dashed</code></li>
 * <li><code>dotted</code></li>
 * </ul>
 */
class LineRenderer implements Renderer {

    static final String LINE_CHART_TYPE = "line";

    private XYLineAndShapeRenderer lineRenderer;

    private LineStyle style;

    private LineRenderer(LineStyle style) {
        this.style = style;
        if (style.getLineType().equals("dashed")) {
            this.lineRenderer = new XYLineAndShapeRenderer(true, false);
            this.lineRenderer.setSeriesStroke(0, getDashedLineDefinition(style));
        }
        else if (style.getLineType().equals("dotted")) {
            this.lineRenderer = new XYLineAndShapeRenderer(false, true);
            lineRenderer.setBaseShape(getDotsDefinition(style));
        } else if (style.getLineType().equals("solidWithDots")) {
            this.lineRenderer = new XYLineAndShapeRenderer(true, true);
            this.lineRenderer.setSeriesStroke(0, getSolidLineDefinition(style));
            this.lineRenderer.setSeriesShape(0, getDotsDefinition(style));
        }
        else { // solid is default
            this.lineRenderer = new XYLineAndShapeRenderer(true, false);
            this.lineRenderer.setSeriesStroke(0, getSolidLineDefinition(style));
        }
    }

    private Shape getDotsDefinition(LineStyle style) {
        int width = style.getDotWidth();
        return new Ellipse2D.Double( -width, -width, 2 * width, 2 * width);
    }

    private Stroke getSolidLineDefinition(LineStyle style) {
        return new BasicStroke(style.getLineWidth());
    }

    private BasicStroke getDashedLineDefinition(LineStyle style) {
        int width = style.getDashGapWidth();
        float[] dashSequence = new float[] {4.0f * width, 4.0f * width};
        return new BasicStroke(width, CAP_ROUND, JOIN_ROUND, 1.0f, dashSequence, 0.0f);
    }
    
    @Override
    public XYItemRenderer getXYRenderer() {
        return this.lineRenderer;
    }

    @Override
    public String getRendererType() {
        return LINE_CHART_TYPE;
    }

    @Override
    public void setColorForSeriesAt(int index) {
        lineRenderer.setSeriesPaint(index, decode(style.getColor()));
    }

    public static LineRenderer createStyledLineRenderer(LineStyle style) {
        return new LineRenderer(style);
    }

}
