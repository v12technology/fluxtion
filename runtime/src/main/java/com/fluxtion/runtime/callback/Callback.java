package com.fluxtion.runtime.callback;

import java.util.Iterator;

public interface Callback<R> {
    void fireCallback();

    void fireCallback(R data);

    void fireCallback(Iterator<R> dataIterator);

    R get();
}
