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
package org.n52.client.ui;

import org.n52.client.ui.btn.SmallButton;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class ApplyCancelButtonLayout extends HLayout {
    
    private Canvas loadingSpinner;
    
    private String applyImg = "../img/icons/acc.png";
    
    private String cancelImg = "../img/icons/del.png";
    
    private String loaderImg = "../img/loader_wide.gif";
    
    public ApplyCancelButtonLayout() {
        loadingSpinner = createLoadingSpinner();
        addMember(loadingSpinner);
    }

    private Canvas createLoadingSpinner() {
        LoadingSpinner loader = new LoadingSpinner(loaderImg, 43, 11);
        loader.setPadding(7);
        return loader;
    }

    public void createApplyButton(String tooltip, String longTooltip, ClickHandler handler) {
        Canvas applyButton = createSmallButton(applyImg, tooltip, longTooltip);
        applyButton.addClickHandler(handler);
        addMember(applyButton);
    }
    
    public void createCancelButton(String tooltip, String longTooltip, ClickHandler handler) {
        Canvas applyButton = createSmallButton(cancelImg, tooltip, longTooltip);
        applyButton.addClickHandler(handler);
        addMember(applyButton);
    }

    private Canvas createSmallButton(String img, String tooltip, String longTooltip) {
        return new SmallButton(new Img(img), tooltip, longTooltip);
    }
    
    public void setLoading() {
        loadingSpinner.show();
    }
    
    public void finishLoading() {
        loadingSpinner.hide();
    }
}
