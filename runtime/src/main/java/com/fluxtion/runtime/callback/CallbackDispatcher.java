package com.fluxtion.runtime.callback;

import java.util.Iterator;

public interface CallbackDispatcher extends EventDispatcher {
    String DEFAULT_NODE_NAME = "callbackDispatcher";

    void fireCallback(int id);

    <T> void fireCallback(int id, T item);

    <R> void fireIteratorCallback(int callbackId, Iterator<R> dataIterator);
}
