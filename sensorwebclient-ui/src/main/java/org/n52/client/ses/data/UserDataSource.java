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
package org.n52.client.ses.data;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;

public class UserDataSource extends DataSource {

    public UserDataSource() {

        DataSourceIntegerField id = new DataSourceIntegerField("parameterId", "ID");
        id.setPrimaryKey(true);
        id.setHidden(true);

        DataSourceTextField userName = new DataSourceTextField("userName", i18n.userName());
        DataSourceTextField name = new DataSourceTextField("name", i18n.name());
        DataSourceTextField password = new DataSourceTextField("password", i18n.password());
        DataSourceTextField eMail = new DataSourceTextField("eMail", i18n.emailAddress());
        DataSourceTextField handy = new DataSourceTextField("handy", i18n.handyNumber());
        DataSourceTextField role = new DataSourceTextField("role", i18n.role());

        setFields(id, userName, name, password, eMail, handy, role);

        setClientOnly(true);
    }
}