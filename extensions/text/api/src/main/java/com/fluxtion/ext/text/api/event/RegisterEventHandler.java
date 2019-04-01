/*
 * Copyright (C) 2019 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.ext.text.api.event;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.EventHandler;
import static com.fluxtion.ext.text.api.event.EventId.REGISTER_EVENTHANDLER_ID;

/**
 * An event that registers an {@link EventHandler} as a sink in another SEP. The
 * receiving SEP must listen for Can be useful if a SEP wants to forward events
 * it produces to another SEP, this event can be used to register the target
 * SEP.
 *
 * @author V12 Technology Ltd.
 */
public class RegisterEventHandler extends Event {

    public static final int ID = REGISTER_EVENTHANDLER_ID;
    private final EventHandler handler;
    private boolean register;

    public RegisterEventHandler(EventHandler handler) {
        this(handler, true);
    }

    /**
     * Register/Unregister an EVentHandler
     *
     * @param handler the target
     * @param register register/unregister
     */
    public RegisterEventHandler(EventHandler handler, boolean register) {
        super(ID);
        this.handler = handler;
        this.register = register;
    }

    public EventHandler getHandler() {
        return handler;
    }

    public boolean isRegister() {
        return register;
    }

}
