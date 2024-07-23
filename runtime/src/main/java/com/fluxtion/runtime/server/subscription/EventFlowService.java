package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.annotations.feature.Experimental;

@Experimental
public interface EventFlowService {

    void setEventFlowManager(EventFlowManager eventFlowManager, String serviceName);
}
