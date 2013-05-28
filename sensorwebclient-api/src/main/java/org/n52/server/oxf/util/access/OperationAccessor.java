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
package org.n52.server.oxf.util.access;

import java.util.concurrent.Callable;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.IServiceAdapter;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;

public class OperationAccessor implements Callable<OperationResult> {

    private final IServiceAdapter adapter;

    private final Operation operation;

    private final ParameterContainer parameterContainer;

    public OperationAccessor(IServiceAdapter adapter, Operation operation, ParameterContainer paramCon) {
        this.adapter = adapter;
        this.operation = operation;
        this.parameterContainer = paramCon;
    }

    public OperationResult call() throws OXFException {
        try {
            return adapter.doOperation(operation, parameterContainer);
        } catch (ExceptionReport e) {
            throw new OXFException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(operation.getName()).append(" to ").append(adapter.getServiceType());
        return sb.toString();
    }

}
