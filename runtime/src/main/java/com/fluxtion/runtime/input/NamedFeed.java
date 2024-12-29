package com.fluxtion.runtime.input;

import com.fluxtion.runtime.event.NamedFeedEvent;
import com.fluxtion.runtime.node.EventSubscription;

public interface NamedFeed extends EventFeed<EventSubscription<?>> {

    default <T> NamedFeedEvent<T> lastUpdate() {
        return null;
    }
}
