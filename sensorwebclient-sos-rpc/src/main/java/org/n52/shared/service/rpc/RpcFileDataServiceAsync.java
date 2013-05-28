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
package org.n52.shared.service.rpc;

import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.RepresentationResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RpcFileDataServiceAsync {

    /**
     * Gets the pDF.
     * 
     * @param req
     *            the req
     * @param callback
     *            the callback that will be called to receive the return value
     *            (see <code>@gwt.callbackReturn</code> tag)
     * @gwt.callbackReturn RepresentationResponse
     * @generated generated method with asynchronous callback parameter to be
     *            used on the client side
     */
    void getPDF(TimeSeriesDataRequest req, AsyncCallback<RepresentationResponse> callback);

    /**
     * Gets the xLS.
     * 
     * @param req
     *            the req
     * @param callback
     *            the callback that will be called to receive the return value
     *            (see <code>@gwt.callbackReturn</code> tag)
     * @gwt.callbackReturn RepresentationResponse
     * @generated generated method with asynchronous callback parameter to be
     *            used on the client side
     */
    void getXLS(TimeSeriesDataRequest req, AsyncCallback<RepresentationResponse> callback);

    /**
     * Gets the cSV.
     * 
     * @param req
     *            the req
     * @param callback
     *            the callback that will be called to receive the return value
     *            (see <code>@gwt.callbackReturn</code> tag)
     * @gwt.callbackReturn RepresentationResponse
     * @generated generated method with asynchronous callback parameter to be
     *            used on the client side
     */
    void getCSV(TimeSeriesDataRequest req, AsyncCallback<RepresentationResponse> callback);

    /**
     * Gets the pD fzip.
     * 
     * @param req
     *            the req
     * @param callback
     *            the callback that will be called to receive the return value
     *            (see <code>@gwt.callbackReturn</code> tag)
     * @gwt.callbackReturn RepresentationResponse
     * @generated generated method with asynchronous callback parameter to be
     *            used on the client side
     */
    void getPDFzip(TimeSeriesDataRequest req, AsyncCallback<RepresentationResponse> callback);

    /**
     * Gets the xL szip.
     * 
     * @param req
     *            the req
     * @param callback
     *            the callback that will be called to receive the return value
     *            (see <code>@gwt.callbackReturn</code> tag)
     * @gwt.callbackReturn RepresentationResponse
     * @generated generated method with asynchronous callback parameter to be
     *            used on the client side
     */
    void getXLSzip(TimeSeriesDataRequest req, AsyncCallback<RepresentationResponse> callback);

    /**
     * Gets the cS vzip.
     * 
     * @param req
     *            the req
     * @param callback
     *            the callback that will be called to receive the return value
     *            (see <code>@gwt.callbackReturn</code> tag)
     * @gwt.callbackReturn RepresentationResponse
     * @generated generated method with asynchronous callback parameter to be
     *            used on the client side
     */
    void getCSVzip(TimeSeriesDataRequest req, AsyncCallback<RepresentationResponse> callback);

}
