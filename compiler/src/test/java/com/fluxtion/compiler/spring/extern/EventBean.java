package com.fluxtion.compiler.spring.extern;

import com.fluxtion.runtime.annotations.OnEventHandler;

public class EventBean {

    public String input;

    @OnEventHandler
    public boolean stringUpdate(String in) {
        input = in;
        return true;
    }
}
