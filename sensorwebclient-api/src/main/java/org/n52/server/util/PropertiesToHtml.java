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
package org.n52.server.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

import com.google.common.io.Files;

public class PropertiesToHtml extends MetadataToHtml {

    private Map<String, String> properties;

    public static PropertiesToHtml createFromProperties(Map<String, String> properties) {
        return new PropertiesToHtml(properties);
    }

    private PropertiesToHtml(Map<String, String> properties) {
        if (properties == null) {
            this.properties = new HashMap<String, String>();
        }
        this.properties = properties;
    }

    public String create(SosTimeseries timeseries) throws IOException {
        InputStream template = getClass().getResourceAsStream("/templates/metadata.html");
        Scanner scanner = new Scanner(template);
        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine());
        }

        String body = "<table(.*)>(.*)</table>";
        Pattern pattern = Pattern.compile(body);
        Matcher matcher = pattern.matcher(content.toString());
        if (matcher.find()) {
            int from = matcher.start(2);
            int until = matcher.end(2);
            content.replace(from, until, createTableRowsFrom(properties));
        }

        String fileName = createFileNameWithoutExtension(timeseries).concat(".html");
        saveToFile(content, prepareSavingFile(ConfigurationContext.GEN_DIR, fileName));

        return getExternalURLAsString(fileName);
    }

    private String prepareSavingFile(String directory, String fileName) throws IOException {
        File folder = new File(directory);
        if ( !folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IOException("Could not create folder '" + directory + "' to store '" + fileName + "'.");
            }
            if ( !directory.endsWith(File.separator)) {
                directory.concat(File.separator);
            }
        }
        
        return directory.concat(fileName);
    }

    private void saveToFile(StringBuilder sb, String fileName) throws IOException {
        BufferedWriter writer = Files.newWriter(new File(fileName), Charset.forName("UTF-8"));
        try {
            writer.append(sb.toString());
        }
        finally {
            writer.flush();
            writer.close();
        }
    }

    private String createTableRowsFrom(Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        for (String key : properties.keySet()) {
            sb.append("<tr>\n");
            sb.append("<th>").append(key).append("</th>\n");
            sb.append("<td>").append(properties.get(key)).append("</td>\n");
            sb.append("</tr>\n");
        }
        return sb.toString();
    }

}
