package org.n52.io.render;

import org.jfree.chart.renderer.xy.XYItemRenderer;

interface Renderer {
    
    public XYItemRenderer getXYRenderer();
    
    public String getRendererType();
    
    public void setColorForSeriesAt(int index);
}
