package com.fluxtion.runtime.server.subscription;

import com.fluxtion.runtime.lifecycle.Lifecycle;

public interface LifeCycleEventSource<T> extends EventSource<T>, Lifecycle {
}
