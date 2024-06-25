package com.fluxtion.runtime.server.subscription;

public interface CallBackType {

    String name();

    enum StandardCallbacks implements CallBackType {
        ON_EVENT;
    }

}
