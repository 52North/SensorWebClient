
package org.n52.io;

import static org.n52.io.MimeType.IMAGE_PNG;

import org.n52.io.render.ChartRenderer;
import org.n52.io.render.DefaultChartRenderer;
import org.n52.io.render.RenderingContext;

public class IOFactory {

    private String language = "en";

    private boolean tooltips = false;
    
    private boolean drawLegend = false;

    private MimeType mimeType = IMAGE_PNG;

    private IOFactory() {
        // use static constructor
    }

    /**
     * @return An {@link IOFactory} instance with default values set. Configure factory by chaining parameter
     *         methods. After configuring the factory a <code>create*</code> method creates appropriate IO
     *         generators.
     */
    public static IOFactory create() {
        return new IOFactory();
    }

    /**
     * @param language
     *        the language (default is <code>en</code>).
     * @return this instance for parameter chaining.
     */
    public IOFactory withLanguage(String language) {
        this.language = language;
        return this;
    }

    /**
     * @param tooltips
     *        <code>true</code> if tooltips shall be shown (default is <code>false</code>).
     * @return this instance for parameter chaining.
     */
    public IOFactory showTooltips(boolean tooltips) {
        this.tooltips = tooltips;
        return this;
    }

    /**
     * @param drawLegend
     *        <code>true</code> if a legend shall be drawn (default is <code>false</code>).
     * @return this instance for parameter chaining.
     */
    public IOFactory withLegend(boolean drawLegend) {
        this.drawLegend = drawLegend;
        return this;
    }
    
    /**
     * @param mimeType
     *        the MIME-Type of the image to be rendered (default is {@link MimeType#IMAGE_PNG}).
     * @return this instance for parameter chaining.
     */
    public IOFactory forMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public ChartRenderer createChartRenderer(RenderingContext context) {
        DefaultChartRenderer chartRenderer = new DefaultChartRenderer();
        chartRenderer.setContext(context);
        chartRenderer.setLanguage(language);
        chartRenderer.setShowTooltips(tooltips);
        chartRenderer.setDrawLegend(drawLegend);
        chartRenderer.setMimeType(mimeType.getMimeType());

        // TODO do further settings
        
        // TODO create an OverviewChartRenderer

        return chartRenderer;
    }

}
