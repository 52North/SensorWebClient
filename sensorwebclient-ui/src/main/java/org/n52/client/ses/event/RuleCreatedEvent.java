package org.n52.client.ses.event;

import org.n52.client.ses.event.handler.RuleCreatedEventHandler;
import org.n52.shared.serializable.pojos.Rule;

import com.google.gwt.event.shared.GwtEvent;

public class RuleCreatedEvent extends GwtEvent<RuleCreatedEventHandler> {

    public static Type<RuleCreatedEventHandler> TYPE = new Type<RuleCreatedEventHandler>();
    
    private Rule createdRule;
    
    public RuleCreatedEvent(Rule createdRule) {
        this.createdRule = createdRule;
        // TODO add parameters of interest
    }
    
    public Rule getCreatedRule() {
        return createdRule;
    }

    @Override
    protected void dispatch(RuleCreatedEventHandler handler) {
        handler.onRuleCreated(this);
    }
    
    @Override
    public Type<RuleCreatedEventHandler> getAssociatedType() {
        return TYPE;
    }    
}
