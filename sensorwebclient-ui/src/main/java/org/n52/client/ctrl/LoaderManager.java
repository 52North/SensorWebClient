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

package org.n52.client.ctrl;

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.HashMap;

import org.n52.client.view.gui.elements.DataPanel;
import org.n52.client.view.gui.widgets.Loader;

/**
 * Controller for the (spinning) AJAX-loader images. Register {@link Loader} instances
 */
public class LoaderManager {

    private static LoaderManager instance;

    private HashMap<String, Loader> loaders;

    private int activeRequests = 0;

    private LoaderManager() {
        this.loaders = new HashMap<String, Loader>();
    }

    public void removeActiveRequest() {
        if (this.activeRequests > 0)
            this.activeRequests--;
        if (this.activeRequests == 0) {
            stopLoadingAnimations();
            DataPanel.requestCounter.setText("");
        }
    }

    public void addActiveRequest() {
        this.activeRequests++;
        startLoadingAnimations();
        DataPanel.requestCounter.setText(i18n.requestsLeft() + " " + this.activeRequests);
    }

    public static LoaderManager getInstance() {
        if (instance == null) {
            instance = new LoaderManager();
        }
        return instance;
    }

    public void registerLoader(Loader loader) {
        this.loaders.put(loader.getID(), loader);
    }

    public void unregisterLoader(String ID) {
        this.loaders.remove(ID);
    }

    public int getCount() {
        return this.loaders.size();
    }

    public void startLoadingAnimations() {
        for (Loader loader : this.loaders.values()) {
            loader.startAnimation();
        }
    }

    /**
     * Stops loading animations and signals that adding timeseries has finished.
     */
    public void stopLoadingAnimations() {
        for (Loader loader : this.loaders.values()) {
            loader.stopAnimation();
        }
//        EventBus.getMainEventBus().fireEvent(new FinishedLoadingTimeSeriesEvent());
    }

}