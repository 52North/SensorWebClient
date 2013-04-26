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

package org.n52.shared.serializable.pojos;

import java.io.Serializable;

public class BoundingBox extends Geometry implements Serializable {
	
    private static final long serialVersionUID = -674668726920006020L;

    private EastingNorthing ll;

    private EastingNorthing ur;

	@SuppressWarnings("unused")
	private BoundingBox() {
        // client requires to be default instantiable
    }

    /**
     * @param ll the lower left corner
     * @param ur the upper right corner
     * @param srs the srs code (e.g. EPSG:4326)
     */
    public BoundingBox(EastingNorthing ll, EastingNorthing ur) {
    	setSrs(ll.getSrs());
    	this.ll = ll;
        this.ur = ur;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("BBOX [ (");
        sb.append(ll.getEasting()).append(",").append(ll.getNorthing()).append(");(");
        sb.append(ur.getEasting()).append(",").append(ur.getNorthing()).append(") ");
        sb.append("srs: ").append(getSrs()).append(" ]");
        return sb.toString();
    }
    
    /**
     * Indicates if the given coordinate pair is contained by this bounding box instance. The coordinates are
     * assumed to be in the same coordinate reference system as the bounding box instance.
     * 
     * @param easting
     *        the 'right' value
     * @param northing
     *        the 'up' value
     * @return if this instance contains the given coordiantes.
     */
    public boolean contains(double easting, double northing) {
        return isWithinHorizontalRange(easting) && isWithinVerticalRange(northing);
    }

	private boolean isWithinHorizontalRange(double easting) {
		return ll.getEasting() <= easting && easting <= ur.getEasting();
	}
	
	private boolean isWithinVerticalRange(double northing) {
		return ll.getNorthing() <= northing && northing <= ur.getNorthing();
	}

    /**
     * @return the lower left corner coordinate.
     */
    public EastingNorthing getLowerLeftCorner() {
        return ll;
    }

    /**
     * @return the upper right corner coordinate.
     */
    public EastingNorthing getUpperRightCorner() {
        return ur;
    }

}
