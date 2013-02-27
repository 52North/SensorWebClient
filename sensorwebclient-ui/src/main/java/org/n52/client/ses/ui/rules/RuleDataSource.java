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
package org.n52.client.ses.ui.rules;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.DESCRIPTION;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.FORMAT;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.MEDIUM;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.NAME;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.OWNERNAME;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.TYPE;
import static org.n52.client.ses.ui.rules.RuleDataSourceRecord.UUID;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class RuleDataSource extends DataSource {

    public RuleDataSource() {
        DataSourceTextField pkField = new DataSourceTextField(UUID);
        pkField.setPrimaryKey(true);
        pkField.setHidden(true);

        DataSourceTextField ruleName = new DataSourceTextField(NAME, i18n.name());
        DataSourceTextField type = new DataSourceTextField(TYPE, i18n.type());
        DataSourceTextField owner = new DataSourceTextField(OWNERNAME, i18n.owner());
        DataSourceTextField medium = new DataSourceTextField(MEDIUM, i18n.description());
        DataSourceTextField format = new DataSourceTextField(FORMAT, i18n.description());
        DataSourceTextField description = new DataSourceTextField(DESCRIPTION, i18n.description());

        setFields(pkField, type, owner, ruleName, description, medium, format);
        setClientOnly(true);
    }
}