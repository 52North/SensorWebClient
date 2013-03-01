
package org.n52.client.ses.ui.profile;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserId;
import static org.n52.client.util.ClientSessionManager.isPresentSessionInfo;
import static org.n52.shared.responses.SesClientResponseType.USER_SUBSCRIPTIONS;

import org.n52.client.ses.event.GetSingleUserEvent;
import org.n52.client.ses.event.GetUserSubscriptionsEvent;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.event.handler.InformUserEventHandler;
import org.n52.client.ses.event.handler.UpdateProfileEventHandler;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.shared.responses.SesClientResponse;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

public class ProfileWindow extends LoginWindow {

    private static final String COMPONENT_ID = "UserProfileWindow";

    private static ProfileWindow instance;

    private EditProfileLayout userDataLayout;

    private SubscriptionsLayout subscriptionsLayout;

    public static ProfileWindow getProfileWindow() {
        if (instance == null) {
            instance = new ProfileWindow();
        }
        return instance;
    }

    private ProfileWindow() {
        super(COMPONENT_ID);
        new ProfileWindowEventBroker(this);
    }

    @Override
    protected void loadSubsciptionListContent() {
        clearContent();
        content = new HLayout();
        content.addMember(createEditProfileLayout());
        content.addMember(createSubcriptionsLayout());
        addItem(content);
        setTitle(i18n.editUserData());
        
        subscriptionsLayout.clearGrid();
        getMainEventBus().fireEvent(new GetSingleUserEvent());
        getMainEventBus().fireEvent(new GetUserSubscriptionsEvent());
        markForRedraw();
    }

    private Canvas createEditProfileLayout() {
        if (userDataLayout == null) {
            userDataLayout = new EditProfileLayout();
        }
        return userDataLayout;
    }

    private Canvas createSubcriptionsLayout() {
        if (subscriptionsLayout == null) {
            subscriptionsLayout = new SubscriptionsLayout();
        }
        return subscriptionsLayout;
    }

    private static class ProfileWindowEventBroker implements UpdateProfileEventHandler, InformUserEventHandler {

        private final ProfileWindow window;

        public ProfileWindowEventBroker(ProfileWindow window) {
            getMainEventBus().addHandler(UpdateProfileEvent.TYPE, this);
            getMainEventBus().addHandler(InformUserEvent.TYPE, this);
            this.window = window;
        }

        @Override
        public void onUpdate(UpdateProfileEvent evt) {
            window.userDataLayout.update(evt.getUser());
        }

        @Override
        public void onInform(InformUserEvent evt) {
            SesClientResponse response = evt.getResponse();
            if (response.getType() == USER_SUBSCRIPTIONS) {
                window.subscriptionsLayout.setData(response.getBasicRules(), response.getComplexRules());
            }
        }
    }
}
