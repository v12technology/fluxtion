package com.fluxtion.runtime.server.subscription;

import lombok.Value;

public interface CallBackType {

    String name();

    static CallBackType forClass(Class<?> clazz) {
        return new CallBackTypeByClass(clazz);
    }

    @Value
    class CallBackTypeByClass implements CallBackType {

        Class<?> callBackClass;

        @Override
        public String name() {
            return callBackClass.getCanonicalName();
        }
    }

    enum StandardCallbacks implements CallBackType {
        ON_EVENT;
    }

}
