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


import static com.google.gwt.http.client.URL.encodeQueryString;
import static com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat.ISO_8601;
import static org.n52.client.sos.data.TimeseriesDataStore.getTimeSeriesDataStore;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.Date;

import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.TimeManager;
import org.n52.client.sos.ctrl.SosDataManager;
import org.n52.client.sos.event.data.UpdateSOSMetadataEvent;
import org.n52.client.sos.legend.Timeseries;
import org.n52.client.util.LabelFactory;
import org.n52.client.util.PortalInfo;
import org.n52.client.util.PortalInfos;
import org.n52.ext.ExternalToolsException;
import org.n52.ext.link.AccessLinkFactory;
import org.n52.ext.link.sos.TimeRange;
import org.n52.ext.link.sos.TimeSeriesParameters;
import org.n52.ext.link.sos.TimeSeriesPermalinkBuilder;
import org.n52.shared.serializable.pojos.TimeseriesRenderingOptions;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
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

    	Layout headerMain = new HLayout();
    	Layout headerLogo = new VLayout();
    	Layout headerContent = new VLayout();
    	Layout headerContentTop = new HLayout();
    	Layout headerContentBottom = new HLayout();
    	Layout headerPortalLinks = getPortalLayout();
    	Layout headerMetaLinks = getMetaLinkLayout();
    	Layout headerBreadcrumb = getBreadcrumbLayout();
    	Layout headerIcons = getIconLayout();
    	
    	headerMain.addMember(headerLogo);
    	headerMain.addMember(headerContent);
//    	headerMain.setHeight(62);
    	headerMain.addStyleName("main");
    	
    	headerContent.addMember(headerContentTop);
    	headerContent.addMember(headerContentBottom);
    	headerContentTop.addStyleName("content");
    	
    	headerContentTop.addMember(headerPortalLinks);
    	headerContentTop.addMember(headerMetaLinks);
    	headerContentTop.addStyleName("contentTop");
//    	headerContentTop.setHeight("50%");
    	
    	headerContentBottom.addMember(headerBreadcrumb);
    	headerContentBottom.addMember(headerIcons);
    	headerContentBottom.addStyleName("contentBottom");
//    	headerContentBottom.setHeight("50%");
    	
    	headerLogo.addMember(getHeaderLogo());
//    	headerLogo.setWidth(95);
//    	headerLogo.setHeight(62);
    	headerLogo.addStyleName("logo");
    	
//    	headerPortalLinks.setWidth("75%");
    	headerPortalLinks.addStyleName("portalLinks");
    	
//    	headerMetaLinks.setWidth("25%");
    	headerMetaLinks.addStyleName("metaLinks");
    	
//    	headerBreadcrumb.setWidth("50%");
    	headerBreadcrumb.addStyleName("breadcrumb");
    	
//    	headerIcons.setWidth("50%");
    	headerIcons.addStyleName("icons");
    	headerIcons.setAlign(VerticalAlignment.CENTER);
    	headerIcons.setAlign(Alignment.RIGHT);
    	
        this.setHeight(62);
        this.addStyleName("header");
        this.addMember(headerMain);
    }
    
    private Img getHeaderLogo(){
    	Img image = new Img("../img/taue.jpg", 95, 62);
    	return image;
    }
    
    private Canvas getHeaderIconHome(){
    	String name = "iconHome";
    	String title = PortalInfos.getCurrent().getTitle();
    	String url = PortalInfos.getCurrent().getBaseUrl();
    	int width = 20;
    	int height = 18;
    	return getHeaderIconTemplate(name, url, title, width, height);
    }
    
    private Canvas getHeaderIconSitemap(){
    	String name = "iconSitemap";
    	String title = i18n.headerIconSitemapTitle();
    	String url = PortalInfos.getCurrent().getBaseUrl() + "Sitemap.html";
    	int width = 20;
    	int height = 18;
    	return getHeaderIconTemplate(name, url, title, width, height);
    }
    
    private Canvas getHeaderIconSearch(){
    	String name = "iconSearch";
    	String title = i18n.headerIconSearchTitle();
    	String url = PortalInfos.getCurrent().getBaseUrl() + "cgi-bin/search";
    	int width = 20;
    	int height = 18;
    	return getHeaderIconTemplate(name, url, title, width, height);
    }
    
    private Canvas getHeaderIconMail(){
    	String name = "iconMail";
    	String title = i18n.headerIconMailTitle();
    	String url = "mailto:zdm.wsd-n@wsv.bund.de";
    	int width = 20;
    	int height = 18;
    	return getHeaderIconTemplate(name, url, title, width, height);
    }
    
    private Canvas getHeaderIconFeedback(){
    	String name = "iconFeedback";
    	String title = i18n.headerIconFeedbackTitle();
    	String url = PortalInfos.getCurrent().getBaseUrl() + "Feedback.html?seite=SOS-Client";
    	int width = 20;
    	int height = 18;
    	return getHeaderIconTemplate(name, url, title, width, height);
    }
    
    private static Label getHeaderIconTemplate(String name, final String url, String title, int width, int height){
    	Label label = LabelFactory.getBaseLabel();
    	label.setWidth(width);
    	label.setHeight(height);
    	label.setPadding(0);
    	label.setMargin(0);
    	label.addStyleName("icon");
    	label.addStyleName(name);
        label.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
                Window.open(url, "_blank", "");
			}
        });

    	return label;
    }

    
    private Layout getPortalLayout(){
    	Layout portalLinks = new HLayout();
    	
    	Label text = LabelFactory.getFormattedLabel(i18n.headerGotoPortals());
    	text.addStyleName("bold");
    	portalLinks.addMember(text);
    	
    	PortalInfo currentPortal = PortalInfos.getCurrent();
    	boolean first = true;
    	for( PortalInfo portal : PortalInfos.getAll() ){
    		if( portal != currentPortal ){
	    		if(!first){
	    			portalLinks.addMember(getSeparator());
	    		}
	    		portalLinks.addMember(portal.getLinkLabel(LabelFactory.getFormattedLinkLabel()));
				first = false;
    		}
    	}
    	
    	return portalLinks;
    }

    private Layout getMetaLinkLayout(){
    	Layout metaLinks = new HLayout();
    	metaLinks.setAlign(Alignment.RIGHT);
    	
        metaLinks.addMember(getPermalinkLink());
        metaLinks.addMember(getSeparator());
//        metaLinks.addMember(getAddBookmarkLink());
//        metaLinks.addMember(getSeparator());
        metaLinks.addMember(getImprintLink());
        metaLinks.addMember(getSeparator());
//        metaLinks.addMember(getCopyrightLink());
//        metaLinks.addMember(getSeparator());
        metaLinks.addMember(getHelpLink());
        
//        if (ClientUtils.isSesEnabled()) {
//        	metaLinks.addMember(createLoginInfo());
//        }

        return metaLinks;
    }
    
    private Layout getBreadcrumbLayout(){
    	Layout breadcrumb = new HLayout();
    	
    	Canvas youAreHere = LabelFactory.getFormattedLabel(i18n.headerYouAreHere());
    	youAreHere.addStyleName("bold");
    	Canvas portal = PortalInfos.getCurrent().getLinkLabel(LabelFactory.getFormattedLinkLabel());
    	Canvas sos = LabelFactory.getFormattedLabel(i18n.headerLabelSosClient());
    	
    	breadcrumb.addMembers(youAreHere, portal, getBreadcrumbSeparator(), sos);
    	return breadcrumb;
    }
    
    private Layout getIconLayout(){
    	Layout icons = new HLayout();
    	icons.addMembers(getHeaderIconHome(), getHeaderIconSitemap(), getHeaderIconSearch(), getHeaderIconMail(), getHeaderIconFeedback());
    	icons.setAlign(Alignment.RIGHT);
    	return icons;
    }

    private Label getSeparator() {
		Label pipe = LabelFactory.getFormattedLabel("|");
		pipe.addStyleName("separator");
		return pipe;
	}

    private Label getBreadcrumbSeparator() {
		Label pipe = LabelFactory.getFormattedLabel("&gt;");
		pipe.addStyleName("separator");
		return pipe;
	}


    //////////////////////////////////////////////////////////////////////////////////////////////
    
    
	private Label getMetadatareset() {
        Label label = LabelFactory.getFormattedLinkLabel("reset Metadata");
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
		layout.addStyleName("n52_sensorweb_client_logoBlock");
		Img homeLabel = new Img("../img/client-logo.png", 289, 55);
		homeLabel.addStyleName("n52_sensorweb_client_logo");
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
        Label versionLabel = LabelFactory.getFormattedLinkLabel("Version: " +  version);
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

        Label copyright = LabelFactory.getFormattedLinkLabel("&copy; 52&#176;North, GmbH " + year);
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
		Label addToFavorites = LabelFactory.getFormattedLinkLabel(i18n.addToBookmarks());
        addToFavorites.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent evt) {
                addToFavorites();
            }
        });
		return addToFavorites;
	}

	private Label getPermalinkLink() {
		Label restart = LabelFactory.getFormattedLinkLabel(i18n.permalink());
        restart.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent evt) {
                String currentUrl = Window.Location.getHref();
                String permalink = createPermaLink(currentUrl);
                Window.Location.assign(addDesignOptions(permalink));
            }

            private String addDesignOptions(String permalink) {
                Timeseries[] ts = getTimeSeriesDataStore().getTimeSeriesSorted();
                if (ts == null || ts.length == 0) {
                    return permalink;
                }
                StringBuilder options = new StringBuilder();
                for (Timeseries timeSeries : ts) {
                    TimeseriesRenderingOptions renderingOptions = new TimeseriesRenderingOptions();
                    renderingOptions.setColor(timeSeries.getColor());
                    renderingOptions.setLineWidth(timeSeries.getLineWidth());
                    options.append(renderingOptions.asJson()).append(",");
                }
                // delete last commas
                options.deleteCharAt(options.length() - 1);
                StringBuilder sb = new StringBuilder(permalink);
                String urlEncodedValue = encodeQueryString(options.toString());
                sb.append("&").append("options=").append(urlEncodedValue);
                return sb.toString();
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
		Label help = LabelFactory.getFormattedLinkLabel(i18n.help());
        help.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
                String helpUrl = GWT.getHostPageBaseURL() + i18n.helpPath();
                Window.open(helpUrl, "", "");
            }
        });
		return help;
	}

	private Label getImprintLink() {
		Label imprint = LabelFactory.getFormattedLinkLabel(i18n.Impressum());
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
                layout.addStyleName("n52_sensorweb_client_imprint_content");
                layout.addMember(pane);
                w.addItem(layout);
                w.show();
            }
        });
		return imprint;
	}

    private String createPermaLink(String baseUrl) {
        Timeseries[] ts = getTimeSeriesDataStore().getTimeSeriesSorted();
        if (ts == null || ts.length == 0) {
            return baseUrl;
        }
        
        TimeSeriesPermalinkBuilder builder = new TimeSeriesPermalinkBuilder();
        for (Timeseries timeSeries : ts) {
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

    private TimeSeriesParameters createTimeSeriesParameters(Timeseries timeSeries) {
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

