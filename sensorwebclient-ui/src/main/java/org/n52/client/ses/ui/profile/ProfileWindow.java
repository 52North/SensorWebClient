/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.client.ses.ui.profile;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.util.ClientSessionManager.currentSession;
import static org.n52.client.util.ClientSessionManager.isLoggedIn;
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
        content = new HLayout();
        content.addMember(createEditProfileLayout());
        content.addMember(createSubcriptionsLayout());
        content.setStyleName("n52_sensorweb_client_form_content");
        addItem(content);
        setTitle(i18n.editUserData());
        markForRedraw();
    }
    
    @Override
    public void show() {
        if (isLoggedIn()) {
            requestUserData();
        }
        super.show();
    }

    private void requestUserData() {
        final SessionInfo sessionInfo = currentSession();
        getMainEventBus().fireEvent(new GetSingleUserEvent(sessionInfo));
        getMainEventBus().fireEvent(new GetUserSubscriptionsEvent(sessionInfo));
    }

    private Canvas createEditProfileLayout() {
        if (userDataLayout == null) {
            userDataLayout = new EditProfileLayout();
            userDataLayout.setWidth("40%");
        }
        return userDataLayout;
    }

    private Canvas createSubcriptionsLayout() {
        if (subscriptionsLayout == null) {
            subscriptionsLayout = new SubscriptionsLayout();
            subscriptionsLayout.setWidth("60%");
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
