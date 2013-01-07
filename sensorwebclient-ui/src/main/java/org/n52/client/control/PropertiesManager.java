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

package org.n52.client.control;

import static org.n52.client.sos.i18n.I18NStringsAccessor.i18n;

import java.util.ArrayList;

import org.n52.client.view.gui.widgets.Toaster;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class PropertiesManager {

    private static PropertiesManager instance;

    public static String language = "en";

    protected Document properties;
    
    private boolean initiated = false;

    public static PropertiesManager getInstance() {
        if (instance == null) {
            instance = new PropertiesManager();
            if ( !instance.isInitiated()) {
                instance.init();
                instance.setInitiated(true);
            }
        }
        return instance;
    }

    private void init() {
        try {
        	setCurrentLanguage();
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "properties/client-properties.xml");
            RequestCallback callback = new RequestCallback() {

                public void onError(Request request, Throwable exception) {
                    Toaster.getInstance().addMessage(i18n.errorRequest());
                }

                public void onResponseReceived(Request request, Response response) {
                    PropertiesManager.this.properties = XMLParser.parse(response.getText());
                    Application.continueStartup();
                }
            };
            requestBuilder.sendRequest(null, callback);
        }
        catch (RequestException ex) {
            Toaster.getInstance().addMessage(i18n.errorRequest());
        }
    }

	private void setCurrentLanguage() {
		LocaleInfo currentLocale = LocaleInfo.getCurrentLocale();
		String localeName = currentLocale.getLocaleName();
		PropertiesManager.language = localeName.substring(0, 2);
	}

    public String getParameterAsString(String name) {
        ArrayList<String> attributes = getParameters(name);
		return attributes.isEmpty() ? null : attributes.get(0);
    }
    
    /**
     * @param name the parameter name
     * @return the parameter's boolean value. Note: if attribute is not available <code>false</code> is returned!
     */
    public boolean getParameterAsBoolean(String name) {
        return Boolean.parseBoolean(getParameterAsString(name));
    }
    
    public int getParamaterAsInt(String name, int defaultValue) {
        try {
            return Integer.parseInt(getParameters(name).get(0));
        } catch (Exception e) {
            GWT.log("Error while parsing value for key " + name, e);
            return defaultValue;
        }
    }

    public ArrayList<String> getParameters(String name) {
        ArrayList<String> array = new ArrayList<String>();
        if (hasProperty(name)) {
            NodeList nodes = this.properties.getElementsByTagName(name);
            for (int i = 0; i < nodes.getLength(); i++) {
                array.add(nodes.item(i).getFirstChild().toString());
            }
        }
        return array;
    }

    private boolean hasProperty(String name) {
        return properties != null && properties.getElementsByTagName(name) != null;
    }
    
    public boolean isSetProperty(String name) {
    	return hasProperty(name) && properties.getElementsByTagName(name).getLength() > 0;
    }

    public ArrayList<String> getTabsFromPropertiesFile() {
        return instance.getParameters("tab");
    }

    private boolean isInitiated() {
        return this.initiated;
    }

    private void setInitiated(boolean initiated) {
        this.initiated = initiated;
    }

}