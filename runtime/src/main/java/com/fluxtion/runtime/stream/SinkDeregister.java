package com.fluxtion.runtime.stream;

import com.fluxtion.runtime.event.DefaultEvent;

public class SinkDeregister extends DefaultEvent {

    private SinkDeregister(String sinkId) {
        super(sinkId);
    }

    public static SinkDeregister sink(String sinkId) {
        return new SinkDeregister(sinkId);
    }
}
