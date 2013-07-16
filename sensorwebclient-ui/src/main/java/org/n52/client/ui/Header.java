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

import org.n52.client.ctrl.TimeManager;
import org.n52.client.sos.ctrl.DataManagerSosImpl;
import org.n52.client.sos.data.DataStoreTimeSeriesImpl;
import org.n52.client.sos.legend.TimeSeries;
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

    private String url;
    
    public static com.google.gwt.user.client.ui.Label requestCounter;

    public Header (String id){
        this.elemID = id;
        generateHeader();
    }

    
    private void generateHeader(){
        setStyleName("header");

        setAutoHeight();
        
        Layout headerLayout = new HLayout();
        headerLayout.addMember(getHomeLabel());
        headerLayout.addMember(getHeaderMainLayout());

        addMember(headerLayout);
    }
    
    private Layout getHeaderMainLayout() {
    	Layout headerMainLayout = new VLayout();
    	headerMainLayout.addMember(getUpperLinkLine());
    	headerMainLayout.addMember(getLowerLinkLine());
    	headerMainLayout.setWidth100();
    	return headerMainLayout;
    }
    
    private Layout getUpperLinkLine() {
    	
    	Layout upperLinkLine = new HLayout();
    	
    	upperLinkLine.setBackgroundColor("#CCCCCC");
    	upperLinkLine.setHeight(31);
    	upperLinkLine.setWidth100();
    	upperLinkLine.addMember(getDirectLinks());
    	upperLinkLine.addMember(getMiscLinks());
    	
    	return upperLinkLine;
    }
    
    private Layout getDirectLinks() {
    	Layout directLinks = new HLayout();
    	directLinks.setAlign(Alignment.LEFT);
    	directLinks.setWidth("70%");
    	
    	Label directLinksLabel = new Label("Direkt zu den Portalen:");
    	
    	directLinksLabel.setWrap(false);
    	directLinksLabel.setStyleName("bold");
    	//directTopicLabel.
    	directLinks.addMember(directLinksLabel);
    	directLinks.addMember(getSeparatorSpace());
    	directLinks.addMember(createLink("Nordseeküste","http://www.portalnsk.de/"));
    	directLinks.addMember(getSeparator());
    	directLinks.addMember(createLink("Tideems","http://www.portaltideems.de/"));
    	directLinks.addMember(getSeparator());
    	directLinks.addMember(createLink("Nord-Ostsee-Kanal","http://www.portalnok.de/"));
    	directLinks.addMember(getSeparator());
    	directLinks.addMember(createLink("Ostseeküste","http://www.portalosk.de/"));
    	directLinks.addMember(getSeparator());
    	directLinks.addMember(createLink("Küstendaten","http://www.kuestendaten.de/"));
    	
    	return directLinks;    	
    }
    
    private Layout getMiscLinks() {
    	Layout miscLinks = new HLayout();
    	miscLinks.setAlign(Alignment.RIGHT);

    	miscLinks.addMember(getPermalinkLink());
    	miscLinks.addMember(getSeparator());
    	miscLinks.addMember(createLink("Impressum","/Impressum.html"));
    	miscLinks.addMember(getSeparator());
    	miscLinks.addMember(createLink("Open Source Software","/OSS.html"));
    	miscLinks.addMember(getSeparator());
    	miscLinks.addMember(getHelpLink());
    	
    	return miscLinks;    	
    }
    
    private Label createLink(String title, final String linkUrl) {
    	return createLink(title,linkUrl,"header_link",null);
    }
    
    private Label createLink(String title, final String linkUrl, String styleName) {
    	return createLink(title,linkUrl,styleName,null);
    }
    
    private Label createLink(String title, final String linkUrl, String styleName, String prompt) {
    	Label linkLabel = new Label(title);

    	linkLabel.setStyleName(styleName);
    	linkLabel.setAutoWidth();
    	linkLabel.setWrap(false);
    	linkLabel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url = linkUrl;
                Window.open(url, "_blank", "");
			}
		});
    	if (prompt!=null) {
    		linkLabel.setPrompt(prompt);
    	}
		return linkLabel;
    }

    private Layout getLowerLinkLine() {
    	
    	Layout lowerLinkLine = new HLayout();
    	lowerLinkLine.setStyleName("header breadcrumb");
    	lowerLinkLine.setWidth100();
    	lowerLinkLine.setHeight(31);
    	lowerLinkLine.setBackgroundColor("#666666");
 	

    	
    	lowerLinkLine.addMember(getBreadcrumbs());
    	lowerLinkLine.addMember(getIconLinks());
    	    	
    	return lowerLinkLine;
    }
    
    private Layout getBreadcrumbs() {
    	Layout breadcrumbsLayout = new HLayout();
    	breadcrumbsLayout.setWidth("70%");    	
    	breadcrumbsLayout.setAlign(Alignment.LEFT);   
    	breadcrumbsLayout.setStyleName("breadcrumb");
    	
    	Label breadcrumbLabel = new Label("Sie sind hier:");
    	breadcrumbLabel.setAlign(Alignment.LEFT);
    	breadcrumbLabel.setStyleName("breadcrumb bold");
    	breadcrumbsLayout.addMember(breadcrumbLabel);
    	breadcrumbsLayout.addMember(getSeparatorSpace());
    	breadcrumbsLayout.addMember(createLink("Portal Tideelbe","http://www.portal-tideelbe.de/","breadcrumb_link"));
    	breadcrumbsLayout.addMember(getSeparatorSpace());
    	Label hereLabel = new Label (" > SOS-Client");
    	hereLabel.setStyleName("breadcrumb");    	
    	breadcrumbsLayout.addMember(hereLabel);
    	return breadcrumbsLayout;
    }
    
    private Layout getIconLinks() {
    	Layout iconLinksLayout = new HLayout();
    	iconLinksLayout.setAlign(Alignment.RIGHT);   
    	
    	iconLinksLayout.addMember(createLink(" ","http://www.portal-tideelbe.de/","pics10","Zur Startseite des Portals Tideelbe"));
    	iconLinksLayout.addMember(createLink(" ","/Sitemap.html","pics20","zur Inhaltsübersicht (Sitemap)"));
    	iconLinksLayout.addMember(createLink(" ","/cgi-bin/search","pics30","Suche in allen Portalseiten"));
    	iconLinksLayout.addMember(createLink(" ","mailto:zdm.wsd-n@wsv.bund.de","pics40","Email an den Webmaster"));
    	iconLinksLayout.addMember(createLink(" ","/Feedback.html?oid=&ds=portaltideelbe&seite=SOS-Client&untertitel=SOS-Client&source_url=","pics60","Fragen, Hinweise, Anmerkungen oder Kritiken zu dieser Seite (Feedback)"));
    	iconLinksLayout.addMember(createLink(" ","javascript:window.print();","pics50","Inhalt dieser Seite drucken"));
    	
    	return iconLinksLayout;
    }
    
	private Layout getHomeLabel() {
		Layout layout = new VLayout();
		Img homeLabel = new Img("../img/taue.jpg", 95, 62);
		homeLabel.setCursor(Cursor.POINTER);
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
	
	private Label getImpressumLink() {

        Label imp = getHeaderLinkLabel("Impressum");
        imp.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url = "/Impressum.html";
                Window.open(url, "_blank", "");
			}
		});
		return imp;
	}
	
	private Label getOpenSourceLink() {

        Label osLink = getHeaderLinkLabel("Open Source Software");
        osLink.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url = "/OpenSourceSoftware.html";
                Window.open(url, "_blank", "");
			}
		});
		return osLink;
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
            	String localeParameter = Window.Location.getParameter("locale");
                String href = Window.Location.getHref();
				String permaLink = getPermaLink(href);
				if (isEnglishLocale(localeParameter)) {
                    url = href.substring(0, href.indexOf("?"));
                    url += "?locale=en" + permaLink;
                }
                else if (isGermanLocale(localeParameter)) {
                    url = href.substring(0, href.indexOf("?"));
                    url += "?locale=de" + permaLink;
                }
                else {
                	url = permaLink;
//                	if (GWT.isProdMode()) {
//                        url = href + "?" + permaLink.substring(1);
//					} else {
//						url = permaLink;
//					}
                }
                Window.Location.assign(url);
            }
        });
		return restart;
	}
	
	private LoginHeaderLayout createLoginInfo() {
		return new LoginHeaderLayout();
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
        label.setStyleName("header_link");
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
    
    private Label getSeparatorSpace(){
        Label pipe = new Label(" ");
        pipe.setStyleName("n52_sensorweb_client_pipe");
        pipe.setWidth(6);
        return pipe;
    }
    
    String getPermaLink(String baseUrl) {
        TimeSeries[] ts = DataStoreTimeSeriesImpl.getInst().getTimeSeriesSorted();
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
        SOSMetadata metadata = DataManagerSosImpl.getInst().getServiceMetadata(sos);
        String sosVersion = metadata.getSosVersion();
        return new TimeSeriesParameters(sos, sosVersion, offering, procedure, phenomenon, feature);
    }

    // Calls native javascript function addbookmark listed in Client.html
    private native void addToFavorites() /*-{
            $wnd.addBookmark();
    }-*/;
}
