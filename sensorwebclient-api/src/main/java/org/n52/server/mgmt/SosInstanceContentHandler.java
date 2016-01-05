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
package org.n52.server.mgmt;

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

        INSTANCE, ITEMNAME, URL, VERSION, METADATAHANDLER, ADAPTER, WATERML, TIMEOUT, LLEASTING, LLNORTHING, UREASTING, URNORTHING, DEFAULTZOOM, AUTOZOOM, REQUESTCHUNK, FORCEXYAXISORDER, NOELEMENT, SUPPORTSFIRSTLATEST, ENABLEEVENTING, GDAPREFINAL, HTTPCONNECTIONPOOLSIZE;
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
        } else if (TagNames.ITEMNAME.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.ITEMNAME;
        } else if (TagNames.DEFAULTZOOM.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.DEFAULTZOOM;
        } else if (TagNames.AUTOZOOM.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.AUTOZOOM;
        } else if (TagNames.LLEASTING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.LLEASTING;
        } else if (TagNames.LLNORTHING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.LLNORTHING;
        } else if (TagNames.REQUESTCHUNK.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.REQUESTCHUNK;
        } else if (TagNames.FORCEXYAXISORDER.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.FORCEXYAXISORDER;
        } else if (TagNames.UREASTING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.UREASTING;
        } else if (TagNames.URNORTHING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.URNORTHING;
        } else if (TagNames.URL.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.URL;
        } else if (TagNames.VERSION.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.VERSION;
        } else if (TagNames.WATERML.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.WATERML;
        } else if (TagNames.TIMEOUT.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.TIMEOUT;
        } else if (TagNames.METADATAHANDLER.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.METADATAHANDLER;
        } else if (TagNames.ADAPTER.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.ADAPTER;
        } else if (TagNames.ENABLEEVENTING.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.ENABLEEVENTING;
        } else if (TagNames.SUPPORTSFIRSTLATEST.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.SUPPORTSFIRSTLATEST;
        } else if (TagNames.GDAPREFINAL.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.GDAPREFINAL;
        } else if (TagNames.HTTPCONNECTIONPOOLSIZE.name().equalsIgnoreCase(qName)) {
            currentElement = TagNames.HTTPCONNECTIONPOOLSIZE;
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
            currentBuilder = new SOSMetadataBuilder();
        }

        String parsedCharacters = currentContent.toString();
        try {
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
                        // currentBuilder.setDefaultZoom(defaultZoom);
                        break;
                    case AUTOZOOM:
                        boolean autoZoom = Boolean.parseBoolean(parsedCharacters);
                        currentBuilder.setAutoZoom(autoZoom);
                        break;
                    case REQUESTCHUNK:
                        int requestChunk = Integer.parseInt(parsedCharacters);
                        currentBuilder.setRequestChunk(requestChunk);
                        break;
                    case FORCEXYAXISORDER:
                        boolean forceXYAxisOrder = Boolean.parseBoolean(parsedCharacters);
                        currentBuilder.setForceXYAxisOrder(forceXYAxisOrder);
                        break;

                    /*
                     * bbox coordinates are only relevant on client side configuration is parsed on client side
                     */
            // case LLEASTING:
                    // double llEasting = Double.parseDouble(parsedCharacters);
                    // currentBuilder.addLowerLeftEasting(llEasting);
                    // break;
                    // case LLNORTHING:
                    // double llNorthing = Double.parseDouble(parsedCharacters);
                    // currentBuilder.addLowerLeftNorthing(llNorthing);
                    // break;
                    // case UREASTING:
                    // double urEasting = Double.parseDouble(parsedCharacters);
                    // currentBuilder.addUpperRightEasting(urEasting);
                    // break;
                    // case URNORTHING:
                    // double urNorthing = Double.parseDouble(parsedCharacters);
                    // currentBuilder.addUpperRightNorthing(urNorthing);
                    // break;
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
                    case TIMEOUT:
                        int timeout = Integer.parseInt(parsedCharacters);
                        currentBuilder.setTimeout(timeout);
                        break;
                    case METADATAHANDLER:
                        String connector = parsedCharacters;
                        currentBuilder.addSosMetadataHandler(connector);
                        break;
                    case ADAPTER:
                        String adapter = parsedCharacters;
                        currentBuilder.addAdapter(adapter);
                        break;
                    case SUPPORTSFIRSTLATEST:
                        boolean supportsFirstLast = Boolean.parseBoolean(parsedCharacters);
                        currentBuilder.addSupportsFirstLatest(supportsFirstLast);
                        break;
                    case ENABLEEVENTING:
                        boolean enableEventing = Boolean.parseBoolean(parsedCharacters);
                        currentBuilder.setEnableEventing(enableEventing);
                        break;
                    case GDAPREFINAL:
                        boolean gdaPrefinal = Boolean.parseBoolean(parsedCharacters);
                        currentBuilder.setGdaPrefinal(gdaPrefinal);
                        break;
                    case HTTPCONNECTIONPOOLSIZE:
                        int httpConnectionPoolSize = Integer.parseInt(parsedCharacters);
                        currentBuilder.setHttpConnectionPoolSize(httpConnectionPoolSize);
                        break;
                    default:
                        currentElement = TagNames.NOELEMENT; // reset
                }
            }
        } catch (NumberFormatException e) {
            LOG.warn("Could not parse config element '{}' with value '{}'", currentElement, parsedCharacters, e);
        } finally {
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
