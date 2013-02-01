package org.n52.client.ses.ui.usermanagement;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.event.GetSingleUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.event.handler.UpdateProfileEventHandler;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.client.ses.ui.layout.EditProfileLayout;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

public class AccountManagementWindow extends LoginWindow {
	
	private static final String COMPONENT_ID = "AccountManagementWindow";

	private static AccountManagementWindow instance;
	
	private EditProfileLayout editProfileLayout;
	
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
        addItem(content);
        setTitle(i18n.editUserData());
	}
	
	private Canvas createEditProfileLayout() {
		if (editProfileLayout == null) {
		    editProfileLayout =  new EditProfileLayout();
        }
		return editProfileLayout;
	}

	@Override
	public void show() {
		super.show();
		String id = getUserCookie();
		if (id != null) {
			EventBus.getMainEventBus().fireEvent(new GetSingleUserEvent(id));
		}
	}

	private static class AccountManagementWindowEventBroker implements UpdateProfileEventHandler {
		
		private final AccountManagementWindow window;

		public AccountManagementWindowEventBroker(AccountManagementWindow window) {
			getMainEventBus().addHandler(UpdateProfileEvent.TYPE, this);
			this.window = window;
		}

		@Override
		public void onUpdate(UpdateProfileEvent evt) {
            window.editProfileLayout.update(evt.getUser());
		}
	}
}
