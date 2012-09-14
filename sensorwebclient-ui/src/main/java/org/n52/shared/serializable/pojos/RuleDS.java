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

import org.n52.client.control.I18N;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 *
 */
public class RuleDS extends DataSource {

    /**
     * 
     */
    public RuleDS() {
        
        DataSourceIntegerField pkField = new DataSourceIntegerField("itemID");  
        pkField.setHidden(true);  
        pkField.setPrimaryKey(true);

        DataSourceTextField name = new DataSourceTextField("name", I18N.sesClient.name());

        DataSourceTextField type = new DataSourceTextField("type", I18N.sesClient.type());
        DataSourceTextField owner = new DataSourceTextField("ownerName", I18N.sesClient.owner());
        DataSourceTextField description = new DataSourceTextField("description", I18N.sesClient.description());
        DataSourceTextField medium = new DataSourceTextField("medium", I18N.sesClient.description());
        DataSourceTextField format = new DataSourceTextField("format", I18N.sesClient.description());

        setFields(pkField, type, owner, name, description, medium, format);

        setClientOnly(true);
    }
}