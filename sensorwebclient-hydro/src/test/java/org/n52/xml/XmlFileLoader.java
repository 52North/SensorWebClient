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
package org.n52.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.server.oxf.util.connector.hydro.SoapUtil;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

public class XmlFileLoader {

    /**
     * Reads a SOAP XML file and parses its body into an {@link XmlObject}.
     * 
     * @param file
     *        file name of the file to parse
     * @param nodeName
     *        name of the node contained by the SOAP body
     * @return an XmlBeans {@link XmlObject} representation of the XML file
     * @throws XmlException
     *         if parsing to XML fails
     * @throws IOException
     *         if file could not be read
     */
    public static XmlObject loadSoapBodyFromXmlFileViaClassloader(String filePath, String nodeName, Class< ? > clazz) throws XmlException, IOException {
        EnvelopeDocument envelope = (EnvelopeDocument) loadXmlFileViaClassloader(filePath, clazz);
        return SoapUtil.readBodyNodeFrom(envelope, nodeName);
    }

    /**
     * Loads XML files which can be found via the <code>clazz</code>'s {@link ClassLoader}. If not found the
     * {@link FileContentLoader}'s {@link ClassLoader} is asked to load the file. If file could not be found
     * an exception is thrown.
     * 
     * @param filePath
     *        the path to the file to be loaded.
     * @param clazz
     *        the class which {@link ClassLoader} to be used.
     * @return an XmlObject of the loaded file.
     * @throws XmlException
     *         if file could not be parsed into XML
     * @throws IOException
     *         if file could not be read.
     * @throws IllegalArgumentException
     *         if file path is <code>null</code> or empty
     * @throws FileNotFoundException
     *         if the resource could not be found be the <code>clazz</code>'s {@link ClassLoader}
     */
    public static XmlObject loadXmlFileViaClassloader(String filePath, Class< ? > clazz) throws XmlException,
            IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Check file path: '" + filePath + "'.");
        }
        InputStream is = clazz.getResourceAsStream(filePath);
        if (is == null) {
            is = XmlFileLoader.class.getResourceAsStream(filePath);
            if (is == null) {
                throw new FileNotFoundException("The resource at '" + filePath + "' cannot be found.");
            }
        }
        return XmlObject.Factory.parse(is);
    }

}
