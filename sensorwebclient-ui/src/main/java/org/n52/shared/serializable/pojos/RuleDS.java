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

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

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

        DataSourceTextField name = new DataSourceTextField("name", i18n.name());

        DataSourceTextField type = new DataSourceTextField("type", i18n.type());
        DataSourceTextField owner = new DataSourceTextField("ownerName", i18n.owner());
        DataSourceTextField description = new DataSourceTextField("description", i18n.description());
        DataSourceTextField medium = new DataSourceTextField("medium", i18n.description());
        DataSourceTextField format = new DataSourceTextField("format", i18n.description());

        setFields(pkField, type, owner, name, description, medium, format);

        setClientOnly(true);
    }
}