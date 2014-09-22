/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.client.ui.btn;

import org.n52.client.ui.View;
import org.n52.client.ui.legend.LegendData;
import org.n52.client.ui.legend.LegendElement;
import org.n52.client.ui.legend.LegendEntryTimeSeries;

import com.smartgwt.client.types.Cursor;

/**
 * Wrapperclass for the Buttons in a LegendEntry.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class LegendEntryButton extends Button {

    /** The dw. */
    private LegendData dw;

    /** The le. */
    private LegendElement le;

    /** The icon. */
    private String icon;

    /** The tool tip. */
    private String toolTip;

    /** The size. */
    private int size = 16;

    /** The show roll over. */
    private boolean showRollOver = false;

    /** The margin. */
    private int margin = 4;

    /** The extended tooltip. */
    private String extendedTooltip;

    /* (non-Javadoc)
     * @see com.smartgwt.client.widgets.BaseWidget#destroy()
     */
    @Override
    public void destroy() {
        this.le = null;
        super.destroy();
    }
    
    public LegendEntryButton(String icon, String title, String extendedTooltip,
            LegendData dw, LegendEntryTimeSeries le) {

        this.dw = dw;
        this.le = le;
        setStyleName("n52_sensorweb_client_legendEntryButton");
        this.icon = icon;
        this.toolTip = title;
        this.extendedTooltip = extendedTooltip;

        init();

    }

    private void init() {
        this.setWidth(this.size + 2 * this.margin);
        this.setHeight(this.size + 2 * this.margin);

        this.setSrc(this.icon);
        this.setShowRollOver(this.showRollOver);
        this.setShowDownIcon(false);
        this.setShowHover(true);
        this.setShowFocusedAsOver(false);
        this.setMargin(this.margin);
        this.setCursor(Cursor.POINTER);
        if (View.getView().isShowExtendedTooltip()) {
            this.setTooltip(this.extendedTooltip);
        } else {
            this.setTooltip(this.toolTip);
        }
    }

    /**
     * Gets the legend entry.
     * 
     * @return the legend entry
     */
    public LegendEntryTimeSeries getLegendEntry() {
        return (LegendEntryTimeSeries)this.le;
    }

    /**
     * Gets the data wrapper.
     * 
     * @return the data wrapper
     */
    public LegendData getDataWrapper() {
        return this.dw;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.view.gui.elements.Button#setExtendedTooltip()
     */
    @Override
    public void setExtendedTooltip() {
        this.setTooltip(this.extendedTooltip);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.view.gui.elements.Button#setNormalTooltip()
     */
    @Override
    public void setShortTooltip() {
        this.setTooltip(this.toolTip);
    }

}