/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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