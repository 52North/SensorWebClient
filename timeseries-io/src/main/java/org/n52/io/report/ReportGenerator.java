
package org.n52.io.report;

import static org.n52.io.I18N.getDefaultLocalizer;
import static org.n52.io.I18N.getMessageLocalizer;

import java.util.Locale;

import org.n52.io.I18N;
import org.n52.io.IOHandler;
import org.n52.io.img.RenderingContext;
import org.n52.io.v1.data.TimeseriesMetadataOutput;

public abstract class ReportGenerator implements IOHandler {

    protected I18N i18n = getDefaultLocalizer();

    private RenderingContext context;

    /**
     * @param locale
     *        the ISO639 locale to be used.
     * 
     * @see Locale
     */
    public ReportGenerator(RenderingContext context, String language) {
        if (language != null) {
            i18n = getMessageLocalizer(language);
        }
        this.context = context;
    }
    

    public RenderingContext getContext() {
        return context;
    }

    protected TimeseriesMetadataOutput[] getTimeseriesMetadatas() {
        return getContext().getTimeseriesMetadatas();
    }

}
