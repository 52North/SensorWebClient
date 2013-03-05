
package org.n52.client.ses.ui.profile;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.shared.responses.SesClientResponseType.USER_SUBSCRIPTIONS;

import org.n52.client.ses.event.GetSingleUserEvent;
import org.n52.client.ses.event.GetUserSubscriptionsEvent;
import org.n52.client.ses.event.InformUserEvent;
import org.n52.client.ses.event.UpdateProfileEvent;
import org.n52.client.ses.event.handler.InformUserEventHandler;
import org.n52.client.ses.event.handler.UpdateProfileEventHandler;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.session.SessionInfo;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

public class ProfileWindow extends LoginWindow {

    private static final String COMPONENT_ID = "UserProfileWindow";

    private EditProfileLayout userDataLayout;

    private SubscriptionsLayout subscriptionsLayout;

    public ProfileWindow() {
        super(COMPONENT_ID);
        new ProfileWindowEventBroker(this);
        initializeContent();
    }

    @Override
    protected void loadWindowContent() {
        if (userDataLayout != null) {
            userDataLayout.clearValues();
        }
        clearContent();
        content = new HLayout();
        content.addMember(createEditProfileLayout());
        content.addMember(createSubcriptionsLayout());
        addItem(content);
        setTitle(i18n.editUserData());
        
        subscriptionsLayout.clearGrid();
        final SessionInfo sessionInfo = currentSession();
        getMainEventBus().fireEvent(new GetSingleUserEvent(sessionInfo));
        getMainEventBus().fireEvent(new GetUserSubscriptionsEvent(sessionInfo));
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
