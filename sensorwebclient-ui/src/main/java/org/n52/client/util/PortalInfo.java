package org.n52.client.util;

import java.io.Serializable;
import java.util.Vector;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class PortalInfo implements Serializable {

	private static final long serialVersionUID = 8294129560856319595L;

	private String title;
	private String baseUrl;
	private Vector<String> urlVariants = new Vector<String>();
	
	public PortalInfo( String title, String baseUrl, Vector<String> urlVariants){
		this.title = title;
		this.baseUrl = baseUrl;
		if(urlVariants != null){
			this.urlVariants = urlVariants;
		}
	}
	
	public PortalInfo( String title, String baseUrl ){
		this(title, baseUrl, null);
	}
	
	public void addUrlVariant( String urlVariant){
		if(urlVariant != null){
			this.urlVariants.add(urlVariant);
		}
	}
	
	public boolean usesUrl(String testUrl){
		for( String urlVariant : this.urlVariants){
			if(urlVariant.contains(testUrl)){
				return true;
			}
		}
		return false;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getBaseUrl(){
		return this.baseUrl;
	}

	public Label getLinkLabel(){
		return getLinkLabel(null);
	}

	public Label getLinkLabel(Label label){
		if( label == null){
			label = LabelFactory.getFormattedLinkLabel();			
		}
		label.setContents(this.title);
        label.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open(PortalInfo.this.baseUrl, "_blank", "");
           }
        });
		return label;
	}
}
