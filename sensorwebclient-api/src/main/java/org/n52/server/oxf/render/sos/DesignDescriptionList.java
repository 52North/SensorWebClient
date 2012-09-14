/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.server.oxf.render.sos;

import java.awt.Color;
import java.util.ArrayList;

/**
 * The Class DesignDescriptionList.
 * 
 * @author <a href="mailto:broering@52north.org">Arne Broering</a>
 */
public class DesignDescriptionList extends ArrayList<DesignDescriptionList.DesignDescription> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2304572574771192746L;

    /** The domain axis label. */
    private String domainAxisLabel;

    /**
     * Instantiates a new design description list.
     * 
     * @param domainAxisLabel
     *            the domain axis label
     */
    public DesignDescriptionList(String domainAxisLabel) {
        this.domainAxisLabel = domainAxisLabel;
    }

    /**
     * Gets the domain axis label.
     * 
     * @return the domain axis label
     */
    public String getDomainAxisLabel() {
        return this.domainAxisLabel;
    }

    /**
     * Gets the.
     * 
     * @param observedPropertyID
     *            the observed property parameterId
     * @param procedureID
     *            the procedure parameterId
     * @param featureOfInterestID
     *            the feature of interest parameterId
     * @return the design description
     */
    public DesignDescription get(String observedPropertyID, String procedureID,
            String featureOfInterestID) {
        int index =
                super.indexOf(new DesignDescription(observedPropertyID, procedureID,
                        featureOfInterestID, null, null, null, null, null, null, null, 1, false));
        if (index != -1) {
            return super.get(index);
        }
        return null;
    }

    /**
     * Adds the.
     * 
     * @param observedPropertyID
     *            the observed property parameterId
     * @param procedureID
     *            the procedure parameterId
     * @param featureOfInterestID
     *            the feature of interest parameterId
     * @param obsPropDesc
     *            the obs prop desc
     * @param procDesc
     *            the proc desc
     * @param foiDesc
     *            the foi desc
     * @param label
     *            the label
     * @param uomLabel
     *            the uom label
     * @param color
     *            the color
     * @param lineStyle
     *            the line style
     * @param grid
     *            the grid
     */
    public void add(String observedPropertyID, String procedureID, String featureOfInterestID,
            String obsPropDesc, String procDesc, String foiDesc, String label,
            String uomLabel, Color color, String lineStyle, int lineWidth, boolean grid) {
        super.add(new DesignDescription(observedPropertyID, procedureID, featureOfInterestID,
                obsPropDesc, procDesc, foiDesc, label, uomLabel, color, lineStyle, lineWidth, grid));
    }

    /*
     * inner class:
     */
    /**
     * The Class DesignDescription.
     */
    public class DesignDescription {

        /** The observed property parameterId. */
        private String observedPropertyID;

        /** The procedure parameterId. */
        private String procedureID;

        /** The feature of interest parameterId. */
        private String featureOfInterestID;

        /** The obs prop desc. */
        private String obsPropDesc;

        /** The proc desc. */
        private String procDesc;

        /** The foi desc. */
        private String foiDesc;

        /** The label. */
        private String label;

        /** The uom label. */
        private String uomLabel;

        /** The color. */
        private Color color;

        /** The line style. */
        private String lineStyle;
        
        /** The line width */
        private int lineWidth;

        /** The grid. */
        private boolean grid;

        /**
         * Instantiates a new design description.
         * 
         * @param observedPropertyID
         *            the observed property parameterId
         * @param procedureID
         *            the procedure parameterId
         * @param featureOfInterestID
         *            the feature of interest parameterId
         * @param obsPropDesc
         *            the obs prop desc
         * @param procDesc
         *            the proc desc
         * @param foiDesc
         *            the foi desc
         * @param label
         *            the label
         * @param uomLabel
         *            the uom label
         * @param color
         *            the color
         * @param lineStyle
         *            the line style
         * @param grid
         *            the grid
         */
		public DesignDescription(String observedPropertyID, String procedureID,
				String featureOfInterestID, String obsPropDesc,
				String procDesc, String foiDesc, String label, String uomLabel,
				Color color, String lineStyle, int lineWidth, boolean grid) {
            super();
            this.observedPropertyID = observedPropertyID;
            this.procedureID = procedureID;
            this.featureOfInterestID = featureOfInterestID;
            this.obsPropDesc = obsPropDesc;
            this.procDesc = procDesc;
            this.foiDesc = foiDesc;
            this.label = label;
            this.uomLabel = uomLabel;
            this.color = color;
            this.lineStyle = lineStyle;
            this.lineWidth = lineWidth;
            this.grid = grid;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object o) {
            DesignDescription dDesc = (DesignDescription) o;

            if (getFeatureOfInterestID().equals(dDesc.getFeatureOfInterestID())) {
                if (getObservedPropertyID().equals(dDesc.getObservedPropertyID())) {
                    if (getProcedureID().equals(dDesc.getProcedureID())) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * Gets the color.
         * 
         * @return the color
         */
        public Color getColor() {
            return this.color;
        }

        /**
         * Checks if is grid.
         * 
         * @return true, if is grid
         */
        public boolean isGrid() {
            return this.grid;
        }

        /**
         * Gets the line style.
         * 
         * @return the line style
         */
        public String getLineStyle() {
            return this.lineStyle;
        }
        
        /**
         * Gets the line width.
         * 
         * @return the line width
         */
        public int getLineWidth() {
			return this.lineWidth;
		}

        /**
         * Gets the feature of interest parameterId.
         * 
         * @return the feature of interest parameterId
         */
        public String getFeatureOfInterestID() {
            return this.featureOfInterestID;
        }

        /**
         * Gets the observed property parameterId.
         * 
         * @return the observed property parameterId
         */
        public String getObservedPropertyID() {
            return this.observedPropertyID;
        }

        /**
         * Gets the procedure parameterId.
         * 
         * @return the procedure parameterId
         */
        public String getProcedureID() {
            return this.procedureID;
        }

        /**
         * Gets the label.
         * 
         * @return the label
         */
        public String getLabel() {
            return this.label;
        }

        /**
         * Gets the uom label.
         * 
         * @return the uom label
         */
        public String getUomLabel() {
            return this.uomLabel;
        }

        /**
         * Gets the observed property desc.
         * 
         * @return the observed property desc
         */
        public String getObservedPropertyDesc() {
            return this.obsPropDesc;
        }

        /**
         * Gets the feature of interest desc.
         * 
         * @return the feature of interest desc
         */
        public String getFeatureOfInterestDesc() {
            return this.foiDesc;
        }

        /**
         * Gets the procedure desc.
         * 
         * @return the procedure desc
         */
        public String getProcedureDesc() {
            return this.procDesc;
        }
    }

}