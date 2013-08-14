package org.n52.io.render;

public class BarRenderer implements Renderer {
    
    public static final String BAR_CHART_TYPE = "bar";

    public BarRenderer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getRendererType() {
        return BAR_CHART_TYPE;
    }
}

