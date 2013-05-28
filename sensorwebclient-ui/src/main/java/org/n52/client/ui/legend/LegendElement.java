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
package org.n52.client.ui.legend;


import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;

public interface LegendElement  {

    public void update();

    public String getElemId();

    public LegendData getDataWrapper();

    public void updateLayout();

    public int getOrdering();

    public void setOrdering(int ordering);

    public void hideFooter();

    public void showFooter();

    public Canvas getClickTarget();

    public void setTargetToDrag(Canvas c);

    public boolean isVisible();

    public void setVisible(boolean b);

    public void setSelected(boolean b);

    public void setHasNoData(boolean b);

    public void setFooterVisible(boolean b);

    public boolean isFooterVisible();

    public Layout getLayout();

    public void setHasData(boolean b);

    public boolean getHasData();
    
    public boolean equals(Object object);
    
}