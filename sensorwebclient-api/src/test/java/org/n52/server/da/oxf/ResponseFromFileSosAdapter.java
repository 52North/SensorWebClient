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
package org.n52.server.da.oxf;

import static org.junit.Assert.fail;

import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.adapter.SOSAdapter;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.xmlbeans.tools.XmlFileLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseFromFileSosAdapter extends SOSAdapter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseFromFileSosAdapter.class);
    
    private String file;

    public ResponseFromFileSosAdapter(String fileToRespond) {
        super("2.0.0", (HttpClient) null);
        this.file = fileToRespond;
    }

    @Override
    public OperationResult doOperation(Operation operation, ParameterContainer parameters) throws ExceptionReport,
            OXFException {
        try {
            XmlObject response = XmlFileLoader.loadXmlFileViaClassloader(file, getClass());
            return new OperationResult(response.newInputStream(), null, null);
        }
        catch (Exception e) {
            LOGGER.error("Could not load response file for testing: {}", file, e);
            fail("Failed to load response file.");
            return null;
        }
    }

    
    
    
}
