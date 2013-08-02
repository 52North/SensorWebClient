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
package org.n52.client.sos.data;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class SOSDataSource extends DataSource {

	private static final String SOS_INSTANCES_DATA_SOURCE = "ds/sos-instances.data.xml";
	
	private static SOSDataSource instance = null;
	
	SOSDataSource() {
		// use as singleton
	}
	
	public static SOSDataSource getInstance() {
		if (instance == null) {
			instance = new SOSDataSource("sosDataSource");
		}
		return instance;
	}

	public SOSDataSource(String id) {
		setID(id);
		setRecordXPath("/instances/instance");
		DataSourceIntegerField pkField = new DataSourceIntegerField("itemID");
        pkField.setPrimaryKey(true);  
		pkField.setHidden(true);

		DataSourceTextField sosURLField = new DataSourceTextField("url");
		DataSourceTextField sosVersionField = new DataSourceTextField("version");
		DataSourceBooleanField cachedField = new DataSourceBooleanField("cached");
		DataSourceTextField sosItemNameField = new DataSourceTextField("itemName");
		DataSourceBooleanField waterMLField = new DataSourceBooleanField("waterML");
		DataSourceFloatField llEastingField = new DataSourceFloatField("llEasting");
		DataSourceFloatField urEastingField = new DataSourceFloatField("urEasting");
		DataSourceFloatField llNorthingField = new DataSourceFloatField("llNorthing");
		DataSourceFloatField urNorthingField = new DataSourceFloatField("urNorthing");
		DataSourceIntegerField defaultZoomField = new DataSourceIntegerField("defaultZoom");
		DataSourceIntegerField requestChunkField = new DataSourceIntegerField("requestChunk");
		
		setFields(pkField, sosItemNameField, sosVersionField, sosURLField, waterMLField, cachedField, llEastingField, llNorthingField, urEastingField, urNorthingField, defaultZoomField, requestChunkField);
		setDataURL(SOS_INSTANCES_DATA_SOURCE);
	}

}
