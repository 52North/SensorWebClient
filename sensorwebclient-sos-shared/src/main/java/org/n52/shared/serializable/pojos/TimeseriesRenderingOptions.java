package org.n52.shared.serializable.pojos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Planned successor to replace {@link TimeseriesProperties} in the future. Currently used to define client
 * side rendering options.
 */
public class TimeseriesRenderingOptions implements Serializable {

    private static final long serialVersionUID = -8863370584243957802L;

	public static final String GRAPH_STYLE_DEFAULT = "1";
	public static final String LINE_STYLE_DEFAULT = "1";
	public static final double OPACITY_DEFAULT = 100d;
	public static final int LINE_WIDTH_DEFAULT = 2;

    /**
     * Color as 6-digit hex value.
     */
    private String color = getRandomHexColor();

	private String graphStyle = GRAPH_STYLE_DEFAULT;

	private String lineStyle = LINE_STYLE_DEFAULT;

	private int lineWidth = LINE_WIDTH_DEFAULT; // default

	private double opacity = OPACITY_DEFAULT;

    private Scale scale = new Scale();
    
    private static final ColorManager colorManager = new ColorManager();
    
    /**
     * @return a default instance of rendering options and random hex color.
     */
    public static TimeseriesRenderingOptions createDefaultRenderingOptions() {
        return new TimeseriesRenderingOptions();
    }
    
    private static String getRandomHexColor() {
    	return colorManager.getNewRandomColor();
    }

    /**
     * @return color as 6-digit hex value.
     */
    public String getColor() {
        return color;
    }

    /**
     * @param hexColor
     *        color as 6-digit hex value.
     */
    public void setColor(String hexColor) {
        this.color = hexColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @return one-line formatted JSON represenation of the set values.
     */
    public String asJson() {
		HashMap<String, String> options = new HashMap<String, String>();
		if (color != null) {
			options.put("color", withQuotes(color));
		}
		if (lineWidth != LINE_WIDTH_DEFAULT) {
			options.put("lineWidth", String.valueOf(lineWidth));
		}
		if (graphStyle != null
				&& !graphStyle.equals(GRAPH_STYLE_DEFAULT)) {
			options.put("graphStyle", withQuotes(graphStyle));
		}
		if (lineStyle != null && !lineStyle.equals(LINE_STYLE_DEFAULT)) {
			options.put("lineStyle", withQuotes(lineStyle));
		}
		if (opacity != OPACITY_DEFAULT) {
			options.put("opacity", String.valueOf(opacity));
		}
		if (!scale.isAuto()) {
			options.put("scale", withQuotes(scale.getType().toString()));
			if (scale.isManual()) {
				options.put("scaleMin", String.valueOf(scale.getManualScaleMin()));
				options.put("scaleMax", String.valueOf(scale.getManualScaleMax()));
			}
		}

		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (String key : options.keySet()) {
			if (!first)
				sb.append(",");
			sb.append(withQuotes(key));
			sb.append(":").append(options.get(key));
			first = false;
		}
		sb.append("}").toString();

		return sb.toString();
	}
    
    private String withQuotes(String toQuote) {
        return "\"".concat(toQuote).concat("\"");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeseriesRenderingOptions [ ");
        sb.append("hexColor: ").append(color);
        sb.append(", lineWidth: ").append(lineWidth);
        return sb.append(" ]").toString();
    }

	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		if(scale != null){
			this.scale = scale;
		}
		
	}

	public String getGraphStyle() {
		return graphStyle;
	}

	public void setGraphStyle(String graphStyle) {
		this.graphStyle = graphStyle;
	}

	public String getLineStyle() {
		return lineStyle;
	}

	public void setLineStyle(String lineStyle) {
		this.lineStyle = lineStyle;
	}

	public double getOpacity() {
		return opacity;
	}

	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	
	public TimeseriesRenderingOptions copy(){
		TimeseriesRenderingOptions newOptions = new TimeseriesRenderingOptions();
		newOptions.setColor(getColor());
		newOptions.setGraphStyle(getGraphStyle());
		newOptions.setLineStyle(getLineStyle());
		newOptions.setLineWidth(getLineWidth());
		newOptions.setOpacity(getOpacity());
		newOptions.setScale(getScale().getCopy());
		
		return newOptions;
	}
}
