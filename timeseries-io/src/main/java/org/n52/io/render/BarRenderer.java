package org.n52.io.render;

import org.jfree.chart.renderer.xy.XYItemRenderer;

class BarRenderer implements Renderer {
    
    static final String BAR_CHART_TYPE = "bar";

    BarRenderer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getRendererType() {
        return BAR_CHART_TYPE;
    }

    @Override
    public XYItemRenderer getXYRenderer() {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public void setColorForSeriesAt(int index) {
        // TODO Auto-generated method stub
        
    }
}

