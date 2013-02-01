package org.n52.client.ses.ui.usermanagement;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.GetSingleUserEvent;
import org.n52.client.ses.event.GetUserSubscriptionsEvent;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.event.handler.InformUserEventHandler;
import org.n52.client.ses.event.handler.UpdateProfileEventHandler;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.client.ses.ui.layout.EditProfileLayout;
import org.n52.client.ses.ui.layout.UserSubscriptionsLayout;
import org.n52.shared.responses.SesClientResponse;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

public class AccountManagementWindow extends LoginWindow {
	
	private static final String COMPONENT_ID = "AccountManagementWindow";

	private static AccountManagementWindow instance;
	
	private EditProfileLayout editProfileLayout;

	private UserSubscriptionsLayout subscriptionsLayout;
	
	public static AccountManagementWindow getProfileWindow() {
		if (instance == null) {
			instance = new AccountManagementWindow();
			new AccountManagementWindowEventBroker(instance);
		}
		return instance;
	}
	
	private AccountManagementWindow() {
		super(COMPONENT_ID);
	}
	
	@Override
	protected void initializeContent() {
		content = new HLayout();
        content.addMember(createEditProfileLayout());
        content.addMember(createSubcriptionsLayout());
        addItem(content);
        setTitle(i18n.editUserData());
	}
	
	private Canvas createEditProfileLayout() {
		if (editProfileLayout == null) {
		    editProfileLayout = new EditProfileLayout();
        }
		return editProfileLayout;
	}
	
	private Canvas createSubcriptionsLayout() {
		if (subscriptionsLayout == null) {
			subscriptionsLayout = new UserSubscriptionsLayout();
		}
		return subscriptionsLayout;
	}

	@Override
	public void show() {
		super.show();
		String id = getUserCookie();
		if (id != null) {
			EventBus.getMainEventBus().fireEvent(new GetSingleUserEvent(id));
			EventBus.getMainEventBus().fireEvent(new GetUserSubscriptionsEvent(id));
		}
	}

	private static class AccountManagementWindowEventBroker implements UpdateProfileEventHandler, InformUserEventHandler {
		
		private final AccountManagementWindow window;

		public AccountManagementWindowEventBroker(AccountManagementWindow window) {
			getMainEventBus().addHandler(UpdateProfileEvent.TYPE, this);
			getMainEventBus().addHandler(InformUserEvent.TYPE, this);
			this.window = window;
		}

		@Override
		public void onUpdate(UpdateProfileEvent evt) {
            window.editProfileLayout.update(evt.getUser());
		}

		@Override
		public void onInform(InformUserEvent evt) {
			SesClientResponse response = evt.getResponse();
			switch(response.getType()) {
			case USER_SUBSCRIPTIONS:
				window.subscriptionsLayout.setData(response.getBasicRules(), response.getComplexRules());
			}
		}
	}
}
