package org.n52.client.ui;

import org.n52.client.ui.btn.SmallButton;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class ApplyCancelButtonLayout extends HLayout {
    
    private Canvas loadingSpinner;
    
    private String applyImg = "../img/icons/acc.png";
    
    private String cancelImg = "../img/icons/del.png";
    
    private String loaderImg = "../img/loader_wide.gif";
    
    private Canvas applyButton;
    
    public ApplyCancelButtonLayout() {
        loadingSpinner = createLoadingSpinner();
        addMember(loadingSpinner);
    }

    private Canvas createLoadingSpinner() {
        LoadingSpinner loader = new LoadingSpinner(loaderImg, 43, 11);
        loader.setPadding(7);
        return loader;
    }

    public void createApplyButton(String tooltip, String longTooltip, ClickHandler handler) {
        Canvas applyButton = createSmallButton(applyImg, tooltip, longTooltip);
        applyButton.addClickHandler(handler);
        addMember(applyButton);
        this.applyButton = applyButton;
    }
    
    public void createCancelButton(String tooltip, String longTooltip, ClickHandler handler) {
        Canvas applyButton = createSmallButton(cancelImg, tooltip, longTooltip);
        applyButton.addClickHandler(handler);
        addMember(applyButton);
    }

    private Canvas createSmallButton(String img, String tooltip, String longTooltip) {
        return new SmallButton(new Img(img), tooltip, longTooltip);
    }
    
    public void setLoading() {
        loadingSpinner.show();
    }
    
    public void finishLoading() {
        loadingSpinner.hide();
    }
    
    public void enableApplyButton(){
    	if(this.applyButton != null){
    		this.applyButton.enable();
    	}
    }
    
    public void disableApplyButton(){
    	if(this.applyButton != null){
    		this.applyButton.disable();
    	}
    }
    
    public void switchApplyButton(boolean enable){
    	if(enable){
    		enableApplyButton();
    	} else {
    		disableApplyButton();
    	}
    }
}
