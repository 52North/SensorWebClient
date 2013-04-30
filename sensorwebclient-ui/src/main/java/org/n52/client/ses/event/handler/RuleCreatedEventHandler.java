package org.n52.client.ses.event.handler;

import org.n52.client.ses.event.RuleCreatedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface RuleCreatedEventHandler extends EventHandler {

    public void onRuleCreated(RuleCreatedEvent evt);
}
