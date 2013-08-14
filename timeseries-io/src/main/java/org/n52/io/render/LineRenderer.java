package org.n52.io.render;

public class LineRenderer implements Renderer {
    
    public static final String LINE_CHART_TYPE = "line";
    
    public LineRenderer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getRendererType() {
        return LINE_CHART_TYPE;
    }
    
}
