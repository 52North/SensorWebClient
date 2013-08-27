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
package org.n52.io.v1.data;

public class ServiceOutput extends OutputValue {

	private String serviceUrl;

	private String version;
	
	private String type;
	
	private int amountOfferings;
	
	private int amountFeatures;
	
	private int amountProcedures;
	
	private int amountPhenomena;
	
	private int amountStations;
	
	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    public int getAmountOfferings() {
		return amountOfferings;
	}

	public void setAmountOfferings(int amountOfferings) {
		this.amountOfferings = amountOfferings;
	}

	public int getAmountFeatures() {
		return amountFeatures;
	}

	public void setAmountFeatures(int amountFeatures) {
		this.amountFeatures = amountFeatures;
	}

	public int getAmountProcedures() {
		return amountProcedures;
	}

	public void setAmountProcedures(int amountProcedures) {
		this.amountProcedures = amountProcedures;
	}

	public int getAmountPhenomena() {
		return amountPhenomena;
	}

	public void setAmountPhenomena(int amountPhenomena) {
		this.amountPhenomena = amountPhenomena;
	}

	public int getAmountStations() {
		return amountStations;
	}

	public void setAmountStations(int amountStations) {
		this.amountStations = amountStations;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( (serviceUrl == null) ? 0 : serviceUrl.hashCode());
        result = prime * result + ( (type == null) ? 0 : type.hashCode());
        result = prime * result + ( (version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ( !super.equals(obj)) {
            return false;
        }
        if ( ! (obj instanceof ServiceOutput)) {
            return false;
        }
        ServiceOutput other = (ServiceOutput) obj;
        if (serviceUrl == null) {
            if (other.serviceUrl != null) {
                return false;
            }
        }
        else if ( !serviceUrl.equals(other.serviceUrl)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        }
        else if ( !type.equals(other.type)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        }
        else if ( !version.equals(other.version)) {
            return false;
        }
        return true;
    }
	
}
