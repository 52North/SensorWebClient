package org.n52.client.ui;

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.shared.serializable.pojos.UserRole.LOGOUT;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.ctrl.SesRequestManager;
import org.n52.client.ses.event.LogoutEvent;
import org.n52.client.ses.event.SetRoleEvent;
import org.n52.client.ses.event.handler.SetRoleEventHandler;
import org.n52.shared.serializable.pojos.UserRole;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class LoginHeaderLayout extends HLayout {
	
	private Label user;
	private HLayout admin;

	public LoginHeaderLayout() {
		initializeLayout();
		new LoginHeaderEventBroker(this);
		updateLoginInfo();
	}

	private void initializeLayout() {
		setStyleName("n52_sensorweb_client_loginBlock");
		setAlign(Alignment.RIGHT);
		
		user = new Label();
		user.setStyleName("n52_sensorweb_client_headerLoggedInAs");
		user.setAutoWidth();
		user.setWrap(false);
		addMember(user);
		
		// logout link
		Label logout = getHeaderLinkLabel("(" + i18n.logout() + ")");
		logout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EventBus.getMainEventBus().fireEvent(new LogoutEvent());
				SC.say(i18n.logoutSuccessful());
			}
		});
		addMember(logout);
		
		// admin link
		admin = new HLayout();
		admin.setStyleName("n52_sensorweb_client_headerLoggedInAs");
		admin.setAutoWidth();
		Label link = getHeaderLinkLabel(i18n.admin());		
		admin.addMember(getSeparator());
		admin.addMember(link);
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DataPanel dataPanel = View.getView().getDataPanel();
				DataPanelTab newTab;
				if (dataPanel.getCurrentTab().equals(View.getView().getSesTab())) {
					newTab = View.getView().getDiagramTab();
				} else {
					newTab = View.getView().getSesTab();
				}
				dataPanel.getPanel().selectTab(newTab);
				dataPanel.setCurrentTab(newTab);
				dataPanel.update();
			}
		});
		addMember(admin);
//		setAutoWidth();
	}
	
	public void updateLoginInfo() {
		String userName = Cookies.getCookie(SesRequestManager.COOKIE_USER_NAME);
		if(userName != null) {
			show();
			user.setContents(i18n.loggedInAs() + " " + userName);
			String roleType = Cookies.getCookie(SesRequestManager.COOKIE_USER_ROLE);
			if (roleType.equals(UserRole.ADMIN.name())) {
				admin.show();
			} else {
				admin.hide();
			}
		} else {
			hide();
		}
	}
	
	public void logout() {
	    hide();
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
    
    private static class LoginHeaderEventBroker implements SetRoleEventHandler {

    	private final LoginHeaderLayout loginHeaderLayout;
    	
		public LoginHeaderEventBroker(LoginHeaderLayout loginHeaderLayout) {
			EventBus.getMainEventBus().addHandler(SetRoleEvent.TYPE, this);
			this.loginHeaderLayout = loginHeaderLayout;
		}

		@Override
		public void onChangeRole(SetRoleEvent evt) {
		    UserRole role = evt.getRole();
		    if (LOGOUT == role) {
                loginHeaderLayout.logout();
            } else {
                loginHeaderLayout.updateLoginInfo();
            }
		}
    }

}
