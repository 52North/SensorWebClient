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

package org.n52.server.sos.render;

import java.awt.Color;
import java.util.ArrayList;

public class DesignDescriptionList extends ArrayList<DesignDescriptionList.DesignDescription> {

    private static final long serialVersionUID = -2304572574771192746L;

    private String domainAxisLabel;

    public DesignDescriptionList(String domainAxisLabel) {
        this.domainAxisLabel = domainAxisLabel;
    }

    public String getDomainAxisLabel() {
        return this.domainAxisLabel;
    }

    public DesignDescription get(String observedPropertyID, String procedureID, String featureOfInterestID) {
        int index =
                super.indexOf(new DesignDescription(observedPropertyID, procedureID,
                        featureOfInterestID, null, null, null, null, null, null, null, 1, false));
        if (index != -1) {
            return super.get(index);
        }
        return null;
    }

    public void add(String observedPropertyID, String procedureID, String featureOfInterestID,
            String obsPropDesc, String procDesc, String foiDesc, String label,
            String uomLabel, Color color, String lineStyle, int lineWidth, boolean grid) {
        super.add(new DesignDescription(observedPropertyID, procedureID, featureOfInterestID,
                obsPropDesc, procDesc, foiDesc, label, uomLabel, color, lineStyle, lineWidth, grid));
    }

    public class DesignDescription {

        private String observedPropertyID;

        private String procedureID;

        private String featureOfInterestID;

        private String obsPropDesc;

        private String procDesc;

        private String foiDesc;

        private String label;

        private String uomLabel;

        private Color color;

        private String lineStyle;
        
        private int lineWidth;

        private boolean grid;

		public DesignDescription(String observedPropertyID, String procedureID,
				String featureOfInterestID, String obsPropDesc,
				String procDesc, String foiDesc, String label, String uomLabel,
				Color color, String lineStyle, int lineWidth, boolean grid) {
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

        public String getFeatureOfInterestID() {
            return this.featureOfInterestID;
        }

        public String getObservedPropertyID() {
            return this.observedPropertyID;
        }

        public String getProcedureID() {
            return this.procedureID;
        }

        public String getLabel() {
            return this.label;
        }

        public String getUomLabel() {
            return this.uomLabel;
        }

        public String getObservedPropertyDesc() {
            return this.obsPropDesc;
        }

        public String getFeatureOfInterestDesc() {
            return this.foiDesc;
        }

        public String getProcedureDesc() {
            return this.procDesc;
        }
    }

}