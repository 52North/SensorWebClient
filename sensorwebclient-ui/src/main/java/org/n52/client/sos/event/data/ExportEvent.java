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
package org.n52.client.sos.event.data;

import java.util.Collection;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.model.data.representations.TimeSeries;
import org.n52.client.sos.event.data.handler.ExportEventHandler;

public class ExportEvent extends FilteredDispatchGwtEvent<ExportEventHandler> {

    public static Type<ExportEventHandler> TYPE = new Type<ExportEventHandler>();

    public enum ExportType {
        PDF,
        XLS,
        CSV,
        PD_ZIP,
        XLS_ZIP,
        CSV_ZIP,
        PDF_ALL_IN_ONE;
    }

    private Collection<TimeSeries> timeseries;

    private ExportType type;

    public ExportEvent(Collection<TimeSeries> ts, ExportType type) {
        this.timeseries = ts;
        this.type = type;
    }

    public Collection<TimeSeries> getTimeseries() {
        return this.timeseries;
    }

    public ExportType getType() {
        return this.type;
    }

    @Override
    protected void onDispatch(ExportEventHandler handler) {
        handler.onExport(this);
    }

    @Override
    public Type<ExportEventHandler> getAssociatedType() {
        return TYPE;
    }

}
