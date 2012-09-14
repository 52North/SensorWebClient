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

package org.n52.server.oxf.util;

import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SOSMetadataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SosInstanceContentHandler extends DefaultHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SosInstanceContentHandler.class);

    enum TagNames {
        INSTANCE, ITEMNAME, URL, VERSION, CONNECTOR, ADAPTER, WATERML, LLEASTING, LLNORTHING, UREASTING, URNORTHING, DEFAULTZOOM, AUTOZOOM, REQUESTCHUNK, NOELEMENT;
    }

    private SOSMetadataBuilder currentBuilder = new SOSMetadataBuilder();

    private StringBuffer currentContent = new StringBuffer();

    private TagNames currentElement;

    protected SosInstanceContentHandler() {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (TagNames.INSTANCE.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.INSTANCE;
        }
        else if (TagNames.ITEMNAME.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.ITEMNAME;
        }
        else if (TagNames.DEFAULTZOOM.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.DEFAULTZOOM;
        }
        else if (TagNames.AUTOZOOM.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.AUTOZOOM;
        }
        else if (TagNames.LLEASTING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.LLEASTING;
        }
        else if (TagNames.LLNORTHING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.LLNORTHING;
        }
        else if (TagNames.REQUESTCHUNK.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.REQUESTCHUNK;
        }
        else if (TagNames.UREASTING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.UREASTING;
        }
        else if (TagNames.URNORTHING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.URNORTHING;
        }
        else if (TagNames.URL.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.URL;
        }
        else if (TagNames.VERSION.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.VERSION;
        }
        else if (TagNames.WATERML.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.WATERML;
        }
        else if (TagNames.CONNECTOR.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.CONNECTOR;
        }
        else if (TagNames.ADAPTER.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.ADAPTER;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentContent.append(new String(ch, start, length).trim());
    }

    @Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (TagNames.INSTANCE.name().equalsIgnoreCase(qName)) {
			SOSMetadata metadata = currentBuilder.build();
			LOG.debug("New SOS metadata: {}", metadata);
			ConfigurationContext.addNewSOSMetadata(metadata);
		}

        String parsedCharacters = currentContent.toString();
        if (isParsableContent(parsedCharacters)) {
            switch (currentElement) {
            case NOELEMENT:
                break;
            case ITEMNAME:
                String itemName = parsedCharacters;
                currentBuilder.addServiceName(itemName);
                break;
            case DEFAULTZOOM:
                int defaultZoom = Integer.parseInt(parsedCharacters);
//                    currentBuilder.setDefaultZoom(defaultZoom);
                break;
            case AUTOZOOM:
                boolean autoZoom = Boolean.parseBoolean(parsedCharacters);
                currentBuilder.setAutoZoom(autoZoom);
                break;
            case REQUESTCHUNK:
                int requestChunk = Integer.parseInt(parsedCharacters);
                currentBuilder.setRequestChunk(requestChunk);
                break;
            case LLEASTING:
                double llEasting = Double.parseDouble(parsedCharacters);
                currentBuilder.addLowerLeftEasting(llEasting);
                break;
            case LLNORTHING:
                double llNorthing = Double.parseDouble(parsedCharacters);
                currentBuilder.addLowerLeftNorthing(llNorthing);
                break;
            case UREASTING:
                double urEasting = Double.parseDouble(parsedCharacters);
                currentBuilder.addUpperRightEasting(urEasting);
                break;
            case URNORTHING:
                double urNorthing = Double.parseDouble(parsedCharacters);
                currentBuilder.addUpperRightNorthing(urNorthing);
                break;
            case URL:
                String serviceURL = parsedCharacters;
                currentBuilder.addServiceURL(serviceURL);
                break;
            case VERSION:
                String version = parsedCharacters;
                currentBuilder.addServiceVersion(version);
                break;
            case WATERML:
                boolean waterML = Boolean.parseBoolean(parsedCharacters);
                currentBuilder.setWaterML(waterML);
                break;
            case CONNECTOR:
                String connector = parsedCharacters;
                currentBuilder.addConnector(connector);
                break;
            case ADAPTER:
                String adapter = parsedCharacters;
                currentBuilder.addAdapter(adapter);
                break;
            default:
                currentElement = TagNames.NOELEMENT; // reset
            }
            currentContent = new StringBuffer(); // reset
        }
	}

    private boolean isParsableContent(String parsedCharacters) {
        return !isNonParsableContent(parsedCharacters);
    }

    private boolean isNonParsableContent(String parsedCharacters) {
        return parsedCharacters == null || parsedCharacters.isEmpty();
    }

    @Override
    public void endDocument() throws SAXException {
        LOG.debug("End parsing preconfigured SOS instances.");
    }

}
