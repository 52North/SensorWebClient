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
package org.n52.client.ui;


import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.ISO_8601;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.Date;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.TimeManager;
import org.n52.client.sos.ctrl.SosDataManager;
import org.n52.client.sos.data.DataStoreTimeSeriesImpl;
import org.n52.client.sos.event.data.UpdateSOSMetadataEvent;
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.util.ClientUtils;
import org.n52.ext.ExternalToolsException;
import org.n52.ext.link.AccessLinkFactory;
import org.n52.ext.link.sos.TimeRange;
import org.n52.ext.link.sos.TimeSeriesParameters;
import org.n52.ext.link.sos.TimeSeriesPermalinkBuilder;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class Header extends HLayout {

    private String elemID;

    public static com.google.gwt.user.client.ui.Label requestCounter;

    public Header (String id){
        this.elemID = id;
        generateHeader();
    }

    private void generateHeader(){

        setStyleName("n52_sensorweb_client_headerContainer");
        setBackgroundImage("../img/52n_bg.png");
        setAutoHeight();

        addMember(getHomeLabel());

        Layout rightLayout = new VLayout();
        Layout linkLayout = new HLayout();
        linkLayout.setStyleName("n52_sensorweb_client_linkBlock");
        linkLayout.setAlign(Alignment.RIGHT);

//        linkLayout.addMember(getVersionInfo());
//        linkLayout.addMember(getSeparator());
        
        // temporary button for metadata reset
        //linkLayout.addMember(getMetadatareset());
        //linkLayout.addMember(getSeparator());
        
        linkLayout.addMember(getPermalinkLink());
        linkLayout.addMember(getSeparator());
        linkLayout.addMember(getHelpLink());
        linkLayout.addMember(getSeparator());
        linkLayout.addMember(getAddBookmarkLink());
        linkLayout.addMember(getSeparator());
        linkLayout.addMember(getImprintLink());
        linkLayout.addMember(getSeparator());
        linkLayout.addMember(getCopyrightLink());
        rightLayout.addMember(linkLayout);
        
        if (ClientUtils.isSesEnabled()) {
        	rightLayout.addMember(createLoginInfo());
        }
        
        addMember(rightLayout);
    }

	private Label getMetadatareset() {
        Label label = getHeaderLinkLabel("reset Metadata");
        label.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	Toaster.getToasterInstance().addMessage("Update protected services");
            	EventBus.getMainEventBus().fireEvent(new UpdateSOSMetadataEvent());
            }
        });
        return label;
	}

	private Layout getHomeLabel() {
		Layout layout = new VLayout();
		layout.setStyleName("n52_sensorweb_client_logoBlock");
		Img homeLabel = new Img("../img/client-logo.png", 289, 55);
		homeLabel.setStyleName("n52_sensorweb_client_logo");
		homeLabel.setCursor(Cursor.POINTER);
        homeLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String url = "http://52north.org/communities/sensorweb/";
                Window.open(url, "_blank", "");
			}
        });
        layout.addMember(homeLabel);
		return layout;
	}

    private Label getVersionInfo() {
        String version = "foobar";
        Label versionLabel = getHeaderLinkLabel("Version: " +  version);
        versionLabel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open("version-info.txt", "_blank", "");
            }
        });
        return versionLabel;
        
    }

	private Label getCopyrightLink() {

        DateTimeFormat dateFormatter = DateTimeFormat.getFormat("yyyy");
		String year = dateFormatter.format(new Date());

        Label copyright = getHeaderLinkLabel("&copy; 52&#176;North, GmbH " + year);
        copyright.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url = "http://www.52north.org";
                Window.open(url, "_blank", "");
			}
		});
		return copyright;
	}

	private Label getAddBookmarkLink() {
		Label addToFavorites = getHeaderLinkLabel(i18n.addToBookmarks());
        addToFavorites.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent evt) {
                addToFavorites();
            }
        });
		return addToFavorites;
	}

	private Label getPermalinkLink() {
		Label restart = getHeaderLinkLabel(i18n.permalink());
        restart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent evt) {
                String currentUrl = Window.Location.getHref();
                Window.Location.assign(createPermaLink(currentUrl));
            }
        });
		return restart;
	}
	
	private LoginHeaderLayout createLoginInfo() {
		return new LoginHeaderLayout();
	}
	
    private boolean hasLocaleParameter(String value) {
        return value != null && !value.isEmpty();
    }
	
	private boolean isEnglishLocale(String value) {
		return value != null && value.equals("en");
	}
	
	private boolean isGermanLocale(String value) {
		return value != null && value.equals("de");
	}

	private Label getHelpLink() {
		Label help = getHeaderLinkLabel(i18n.help());
        help.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                String helpUrl = GWT.getHostPageBaseURL() + i18n.helpPath();
                Window.open(helpUrl, "", "");
            }
        });
		return help;
	}

	private Label getImprintLink() {
		Label imprint = getHeaderLinkLabel(i18n.Impressum());
        imprint.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                com.smartgwt.client.widgets.Window w = new com.smartgwt.client.widgets.Window();
                w.setTitle(i18n.Impressum());
                w.setWidth(450);
                w.setHeight(460);
                w.centerInPage();
                w.setIsModal(true);

                VLayout layout = new VLayout();
                HTMLPane pane = new HTMLPane();
                pane.setContentsURL(i18n.imprintPath());
                layout.setStyleName("n52_sensorweb_client_imprint_content");
                layout.addMember(pane);
                w.addItem(layout);
                w.show();
            }
        });
		return imprint;
	}

    private Label getHeaderLinkLabel(String labelText) {
    	Label label = new Label(labelText);
        label.setStyleName("n52_sensorweb_client_headerlink");
        label.setAutoWidth();
        label.setWrap(false);
		return label;
	}

    
    private Label getSeparator(){
        Label pipe = new Label("|");
        pipe.setStyleName("n52_sensorweb_client_pipe");
        pipe.setAutoWidth();
        return pipe;
    }
    
    private String createPermaLink(String baseUrl) {
        TimeSeries[] ts = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
        if (ts == null || ts.length == 0) {
            return baseUrl;
        }
        
        TimeSeriesPermalinkBuilder builder = new TimeSeriesPermalinkBuilder();
        for (TimeSeries timeSeries : ts) {
            TimeSeriesParameters parameters = createTimeSeriesParameters(timeSeries);
            parameters.setTimeRange(createTimeRange());
            builder.addParameters(parameters);
        }
        try {
            AccessLinkFactory factory = builder.build();
            return factory.createAccessURL(baseUrl).toString();
        } catch (ExternalToolsException e) {
            Toaster.getToasterInstance().addErrorMessage("Malformed base URL: " + baseUrl);
            return baseUrl;
        }
    }

    private TimeRange createTimeRange() {
        Date start = new Date(TimeManager.getInst().getBegin());
        Date end = new Date(TimeManager.getInst().getEnd());
        DateTimeFormat format = DateTimeFormat.getFormat(ISO_8601);
        return TimeRange.createTimeRange(format.format(start), format.format(end));
    }

    private TimeSeriesParameters createTimeSeriesParameters(TimeSeries timeSeries) {
        String sos = timeSeries.getSosUrl();
        String offering = timeSeries.getOfferingId();
        String procedure = timeSeries.getProcedureId();
        String phenomenon = timeSeries.getPhenomenonId();
        String feature = timeSeries.getFeatureId();
        SOSMetadata metadata = SosDataManager.getDataManager().getServiceMetadata(sos);
        String sosVersion = metadata.getSosVersion();
        return new TimeSeriesParameters(sos, sosVersion, offering, procedure, phenomenon, feature);
    }

    // Calls native javascript function addbookmark listed in Client.html
    private native void addToFavorites() /*-{
            $wnd.addBookmark();
    }-*/;
}
