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
package org.n52.server.oxf.util.generator;

import java.io.Serializable;

import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.n52.server.oxf.render.sos.DesignDescriptionList;
import org.n52.server.oxf.render.sos.DesignDescriptionList.DesignDescription;

/**
 * The Class MetaDataInURLGenerator.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class MetaDataInURLGenerator implements XYURLGenerator, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3191226455244301588L;

    /** The dd list. */
    private DesignDescriptionList ddList;

    /**
     * Instantiates a new meta data in url generator.
     * 
     * @param ddList
     *            the dd list
     */
    public MetaDataInURLGenerator(DesignDescriptionList ddList) {
        this.ddList = ddList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jfree.chart.urls.XYURLGenerator#generateURL(org.jfree.data.xy.XYDataset
     * , int, int)
     */
    public String generateURL(XYDataset dataset, int series, int item) {
        String seriesID = (String) dataset.getSeriesKey(series);

        String foiID = seriesID.split("___")[0]; //$NON-NLS-1$
        String obsPropID = seriesID.split("___")[1]; //$NON-NLS-1$
        String procID = seriesID.split("___")[2]; //$NON-NLS-1$

        DesignDescription dd = this.ddList.get(obsPropID, procID, foiID);
        return dd.getUomLabel()
//                + ";" + Integer.toHexString(dd.getColor().getRGB() & 0x00ffffff); 
        + ";" + Integer.toHexString(dd.getColor().getRGB()).substring(2); 
    }

}
