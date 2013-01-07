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

package org.n52.client.i18n;

import com.google.gwt.core.client.GWT;

/**
 * Shortcut class providing i18n interfaces which offer access to internationalized language strings.
 * 
 * TODO refactor so that SOS client and SES client UI access their own i18n class from separated packages.
 */
class I18NShortCut {

    public static org.n52.client.sos.i18n.I18N sosClient;
    public static org.n52.client.ses.i18n.I18N sesClient;

    public I18NShortCut() {
        sosClient = GWT.create(org.n52.client.sos.i18n.I18N.class);
        sesClient = GWT.create(org.n52.client.ses.i18n.I18N.class);
    }
}