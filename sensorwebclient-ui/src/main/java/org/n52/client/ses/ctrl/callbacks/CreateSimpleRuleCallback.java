package org.n52.client.ses.ctrl.callbacks;

import org.n52.client.ctrl.RequestManager;
import org.n52.client.ctrl.ServerCallback;
import org.n52.shared.responses.SesClientResponse;

public abstract class CreateSimpleRuleCallback extends ServerCallback<SesClientResponse> {

    public CreateSimpleRuleCallback(RequestManager requestMgr, String errorMsg) {
        super(requestMgr, errorMsg);
    }

}
