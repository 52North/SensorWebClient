/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.server.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.n52.oxf.util.JavaHelper;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.responses.FileResponse;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produces a zip File of a folder or a file given by its path.
 * 
 * @author <a href="mailto:p.verhoeven@52north.de">Philipp Verhoeven</a>
 */
public class ZipGenerator extends Generator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipGenerator.class);

    /** The buffer. */
    static byte[] buffer = new byte[8192];

    /** The len. */
    static int len = 0;

    private String folder;

    /**
     * Instantiates a new zip generator.
     *
     * @param folder the folder
     */
    public ZipGenerator(String folder) {
        super();
        this.folder = folder;
    }

    /**
     * Does the actual zipping.
     * 
     * @param file
     *            The file or folder to zip
     * @param relateTo
     *            The path to folder or file
     * @param zipOutputStream
     *            The path of the zip file
     * @throws Exception
     *             the exception
     */
    private static void treeWalkAndCompressTo(File file, URI relateTo,
            ZipOutputStream zipOutputStream) throws Exception {

        /*
         * if the path leads not to a folder
         */
        if (!file.isDirectory()) {
            URI relativePath = relateTo.relativize(file.toURI());
            ZipEntry entry = new ZipEntry(relativePath.toString());
            zipOutputStream.putNextEntry(entry);

            FileInputStream fis = new FileInputStream(file);
            while ((len = fis.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
            fis.close();
            zipOutputStream.closeEntry();
        }
        /*
         * if the path leads to a folder the method is called again with the
         * folders children
         */
        else {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                File child = children[i];
                treeWalkAndCompressTo(child, relateTo, zipOutputStream);
            }
        }
    }

    @Override
    public RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException {
        ZipOutputStream zipOutputStream = null;
        File file = new File(ConfigurationContext.GEN_DIR+"/"+folder);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        String filename = format.format(new Date()) + ConfigurationContext.ZIP_POSTFIX + ".zip";
        File f = new File(ConfigurationContext.GEN_DIR+"/"+filename);
        try {
            zipOutputStream =
                    new ZipOutputStream(new FileOutputStream(f.getAbsolutePath()));

            treeWalkAndCompressTo(file, file.getParentFile().toURI(), zipOutputStream);

        } catch (FileNotFoundException e2) {
            LOGGER.error("Could not write to file.", e2);
            return null;
        } catch (Exception e) {
            LOGGER.error("Could not produce zip file.", e);
            return null;
        } finally {
            try {
                zipOutputStream.flush();
                zipOutputStream.close();
            } catch (IOException e) {
                LOGGER.error("Could not close zip stream accordingly.", e);
            }
           
        }

        JavaHelper.cleanUpDir(ConfigurationContext.GEN_DIR, ConfigurationContext.FILE_KEEPING_TIME);

        LOGGER.debug("Produced zip file: " + filename);
        return new FileResponse(ConfigurationContext.GEN_URL + "/" + f.getName());
    }
}