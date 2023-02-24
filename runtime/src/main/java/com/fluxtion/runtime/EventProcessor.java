package com.fluxtion.runtime;

import com.fluxtion.runtime.lifecycle.Lifecycle;

import java.util.Map;

public interface EventProcessor<T extends EventProcessor<?>> extends StaticEventProcessor, Lifecycle {

    default T newInstance() {
        throw new UnsupportedOperationException();
    }

    default T newInstance(Map<Object, Object> contextMap) {
        throw new UnsupportedOperationException();
    }

}
