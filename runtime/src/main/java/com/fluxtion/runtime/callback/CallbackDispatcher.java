package com.fluxtion.runtime.callback;

import java.util.Iterator;

public interface CallbackDispatcher {

    void fireCallback(int id);
    <T> void fireCallback(int id, T item);

    <R> void fireIteratorCallback(int callbackId, Iterator<R> dataIterator);
}
