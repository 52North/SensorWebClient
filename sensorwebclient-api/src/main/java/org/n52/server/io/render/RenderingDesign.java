package org.n52.server.io.render;

import java.awt.Color;
import java.io.Serializable;

import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;

/**
 * XXX suspect to be refactored (see {@link DesignOptions}, {@link TimeseriesProperties}, {@link TimeseriesRenderingOptions}, etc)
 */
public class RenderingDesign implements Serializable {

    private static final long serialVersionUID = 232118551592451740L;

    private Phenomenon phenomenon;

    private Procedure procedure;

    private Feature feature;

    private String label;

    private String uomLabel;

    private Color color;

    private String lineStyle;

    private int lineWidth;

    private boolean grid;

    public RenderingDesign(Phenomenon phenomenon, Procedure procedure,
                             Feature feature, String label, String uomLabel,
                             Color color, String lineStyle, int lineWidth, boolean grid) {
        this.phenomenon = phenomenon;
        this.procedure = procedure;
        this.feature = feature;
        this.label = label;
        this.uomLabel = uomLabel;
        this.color = color;
        this.lineStyle = lineStyle;
        this.lineWidth = lineWidth;
        this.grid = grid;
    }

    public Color getColor() {
        return this.color;
    }

    public boolean isGrid() {
        return this.grid;
    }

    public String getLineStyle() {
        return this.lineStyle;
    }

    public int getLineWidth() {
        return this.lineWidth;
    }

    public String getLabel() {
        return this.label;
    }

    public String getUomLabel() {
        return this.uomLabel;
    }

    public Phenomenon getPhenomenon() {
        return phenomenon;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public Feature getFeature() {
        return feature;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (feature == null) ? 0 : feature.hashCode());
        result = prime * result + ( (phenomenon == null) ? 0 : phenomenon.hashCode());
        result = prime * result + ( (procedure == null) ? 0 : procedure.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof RenderingDesign)) {
            return false;
        }
        RenderingDesign other = (RenderingDesign) obj;
        if (feature == null) {
            if (other.feature != null) {
                return false;
            }
        }
        else if ( !feature.equals(other.feature)) {
            return false;
        }
        if (phenomenon == null) {
            if (other.phenomenon != null) {
                return false;
            }
        }
        else if ( !phenomenon.equals(other.phenomenon)) {
            return false;
        }
        if (procedure == null) {
            if (other.procedure != null) {
                return false;
            }
        }
        else if ( !procedure.equals(other.procedure)) {
            return false;
        }
        return true;
    }

}

