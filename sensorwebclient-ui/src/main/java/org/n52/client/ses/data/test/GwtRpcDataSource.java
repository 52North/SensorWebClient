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
package org.n52.client.ses.data.test;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSProtocol;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 *
 */
public abstract class GwtRpcDataSource extends DataSource {

    /**
     * Creates new data source which communicates with server by GWT RPC.
     * It is normal server side SmartClient data source with data protocol
     * set to <code>DSProtocol.CLIENTCUSTOM</code> ("clientCustom" - natively
     * supported by SmartClient but should be added to smartGWT) and with data
     * format <code>DSDataFormat.CUSTOM</code>.
     */
    public GwtRpcDataSource () {
        setDataProtocol (DSProtocol.CLIENTCUSTOM);
        setDataFormat (DSDataFormat.CUSTOM);
        setClientOnly (false);
    }

    /**
     * Executes request to server.
     *
     * @param request <code>DSRequest</code> being processed.
     * @return <code>Object</code> data from original request.
     */
    protected Object transformRequest (DSRequest request) {
        String requestId = request.getRequestId ();
        DSResponse response = new DSResponse ();
        response.setAttribute ("clientContext", request.getAttributeAsObject ("clientContext"));
        // Asume success
        response.setStatus (0);
        switch (request.getOperationType ()) {
        case FETCH:
            executeFetch (requestId, request, response);
            break;
        case ADD:
            executeAdd (requestId, request, response);
            break;
        case UPDATE:
            executeUpdate (requestId, request, response);
            break;
        case REMOVE:
            executeRemove (requestId, request, response);
            break;
        default:
            // Operation not implemented.
            break;
        }
        return request.getData ();
    }

    /**
     * Executed on <code>FETCH</code> operation. <code>processResponse (requestId, response)</code>
     * should be called when operation completes (either successful or failure).
     *
     * @param requestId <code>String</code> extracted from <code>DSRequest.getRequestId ()</code>.
     * @param request <code>DSRequest</code> being processed.
     * @param response <code>DSResponse</code>. <code>setData (list)</code> should be called on
     *      successful execution of this method. <code>setStatus (&lt;0)</code> should be called
     *      on failure.
     */
    protected abstract void executeFetch (String requestId, DSRequest request, DSResponse response);

    /**
     * Executed on <code>ADD</code> operation. <code>processResponse (requestId, response)</code>
     * should be called when operation completes (either successful or failure).
     *
     * @param requestId <code>String</code> extracted from <code>DSRequest.getRequestId ()</code>.
     * @param request <code>DSRequest</code> being processed. <code>request.getData ()</code>
     *      contains record should be added.
     * @param response <code>DSResponse</code>. <code>setData (list)</code> should be called on
     *      successful execution of this method. Array should contain single element representing
     *      added row. <code>setStatus (&lt;0)</code> should be called on failure.
     */
    protected abstract void executeAdd (String requestId, DSRequest request, DSResponse response);

    /**
     * Executed on <code>UPDATE</code> operation. <code>processResponse (requestId, response)</code>
     * should be called when operation completes (either successful or failure).
     *
     * @param requestId <code>String</code> extracted from <code>DSRequest.getRequestId ()</code>.
     * @param request <code>DSRequest</code> being processed. <code>request.getData ()</code>
     *      contains record should be updated.
     * @param response <code>DSResponse</code>. <code>setData (list)</code> should be called on
     *      successful execution of this method. Array should contain single element representing
     *      updated row. <code>setStatus (&lt;0)</code> should be called on failure.
     */
    protected abstract void executeUpdate (String requestId, DSRequest request, DSResponse response);

    /**
     * Executed on <code>REMOVE</code> operation. <code>processResponse (requestId, response)</code>
     * should be called when operation completes (either successful or failure).
     *
     * @param requestId <code>String</code> extracted from <code>DSRequest.getRequestId ()</code>.
     * @param request <code>DSRequest</code> being processed. <code>request.getData ()</code>
     *      contains record should be removed.
     * @param response <code>DSResponse</code>. <code>setData (list)</code> should be called on
     *      successful execution of this method. Array should contain single element representing
     *      removed row. <code>setStatus (&lt;0)</code> should be called on failure.
     */
    protected abstract void executeRemove (String requestId, DSRequest request, DSResponse response);

}
