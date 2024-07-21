package com.fluxtion.runtime.server.service.admin;

import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.annotations.feature.Experimental;
import com.fluxtion.runtime.server.subscription.AbstractEventToInvocationStrategy;

@Experimental
public class AdminCommandInvoker extends AbstractEventToInvocationStrategy {

    @Override
    protected void dispatchEvent(Object event, StaticEventProcessor eventProcessor) {
        AdminCommand adminCommand = (AdminCommand) event;
        adminCommand.executeCommand();
    }

    @Override
    protected boolean isValidTarget(StaticEventProcessor eventProcessor) {
        return true;
    }
}
