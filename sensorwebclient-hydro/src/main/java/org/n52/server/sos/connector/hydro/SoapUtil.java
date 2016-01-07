/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.server.sos.connector.hydro;

import java.io.IOException;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;
import org.w3.x2003.x05.soapEnvelope.Header;
import org.w3c.dom.Node;

public class SoapUtil {
	
	private final static String ns_addressing = "http://www.w3.org/2005/08/addressing";
	
	public static EnvelopeDocument wrapToSoapEnvelope(XmlObject bodyContent, String action) {
		EnvelopeDocument envelopeDoc = EnvelopeDocument.Factory.newInstance();
		
		Envelope envelope = envelopeDoc.addNewEnvelope();
		Header header = envelope.addNewHeader();
		
        XmlCursor cur = header.newCursor();

        cur.toFirstContentToken();
        cur.insertElementWithText(new QName(ns_addressing,"To","wsa"),"http://www.ogc.org/SOS");
        cur.insertElementWithText(new QName(ns_addressing,"Action","wsa"), action);
        cur.insertElementWithText(new QName(ns_addressing,"MessageID","wsa"),
                UUID.randomUUID().toString());
//        cur.beginElement(new QName(ns_addressing,"From","wsa"));
//        cur.insertElementWithText(new QName(ns_addressing,"Address","wsa"),
//        "http://www.w3.org/2005/08/addressing/role/anonymous");
        cur.dispose();
        
		Body body = envelope.addNewBody();
		body.set(bodyContent);
		
		XmlCursor cursor = envelopeDoc.newCursor();
		if (cursor.toFirstChild())
		{
		  cursor.setAttributeText(new QName("http://www.w3.org/2001/XMLSchema-instance","schemaLocation"), "http://www.w3.org/2003/05/soap-envelope http://www.w3.org/2003/05/soap-envelope/soap-envelope.xsd http://www.opengis.net/sos/2.0 http://schemas.opengis.net/sos/2.0/sos.xsd");
		}
		
		return envelopeDoc;
	}
	
    /**
     * @param envelope
     *        the SOAP envelope to read body from
     * @param nodeName
     *        the node's name of the expected body payload
     * @return an XmlBeans {@link XmlObject} representation of the body, or <code>null</code> if node could
     *         not be found.
     * @throws XmlException
     *         if parsing to XML fails
     * @throws IOException 
     */
    public static XmlObject readBodyNodeFrom(EnvelopeDocument envelope, String nodeName) throws XmlException, IOException {
        Body soapBody = envelope.getEnvelope().getBody();
        if (nodeName == null) {
//            XmlCursor bodyCursor = soapBody.newCursor();
//            return bodyCursor.toFirstChild() ? bodyCursor.getObject() : null;
        	return XmlObject.Factory.parse(soapBody.newInputStream());
        }
        return getXmlFromDomNode(soapBody, nodeName);
    }
    
    /**
     * @param xml
     *        the node containing xml
     * @param nodeName
     *        the node's name of the DOM node
     * @return an XmlBeans {@link XmlObject} representation of the body, or <code>null</code> if node could
     *         not be found.
     * @throws XmlException
     *         if parsing to XML fails
     */
    public static XmlObject getXmlFromDomNode(XmlObject xml, String nodeName) throws XmlException {
        Node bodyNode = XmlUtil.getDomNode(xml, nodeName);
        return bodyNode == null ? null : XmlObject.Factory.parse(bodyNode);
    }
}
