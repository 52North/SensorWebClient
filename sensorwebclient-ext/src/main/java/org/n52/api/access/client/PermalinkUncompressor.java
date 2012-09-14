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

package org.n52.api.access.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.n52.api.access.AccessLinkUncompressor;

public class PermalinkUncompressor implements AccessLinkUncompressor {

    @Override
    public URL uncompressAccessURL(URL accessURL) throws MalformedURLException {
        String query = accessURL.getQuery();
        if ( !isCompressed(query)) {
            return accessURL;
        }

        QueryBuilder queryBuilder = new QueryBuilder();

        URL serverURL = new URL(accessURL.getProtocol(), accessURL.getHost(), accessURL.getPort(), accessURL.getPath());
        queryBuilder.append(serverURL);
        queryBuilder.append("?");

        // get delimiter
        int i = query.indexOf(QueryBuilder.COMPRESSION_PARAMETER) + QueryBuilder.COMPRESSION_PARAMETER.length() + 1;
        String delimiter = query.substring(i);
        // TODO check if the delimiter does not contain other parameters!

        boolean first = true;
        String[] kvps = accessURL.getQuery().split("&");
        for (String compressedParameter : kvps) {
            if (compressedParameter.contains(QueryBuilder.COMPRESSION_PARAMETER))
                continue; // do not append compression paramter

            if (first)
                first = false;
            else
                queryBuilder.append("&");

            // uncompress only if neccessary
            if (compressedParameter.contains(delimiter)) {
                String[] parameterAndValue = compressedParameter.split("=");
                String key = parameterAndValue[0];
                List<String> values = Arrays.asList(parameterAndValue[1].split(","));

                queryBuilder.appendUncompressedParameters(key, values, delimiter);
            }
            else
                queryBuilder.append(compressedParameter);
        }
        return new URL(queryBuilder.toString());
    }

    // TODO implement the "isCompressed" check in one place only, e.g. static method in QueryBuilder
    private boolean isCompressed(String query) {
        return query != null && query.contains(QueryBuilder.COMPRESSION_PARAMETER);
    }

}
