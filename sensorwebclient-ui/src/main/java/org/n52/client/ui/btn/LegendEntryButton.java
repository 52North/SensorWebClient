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
        if (View.getInstance().isShowExtendedTooltip()) {
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