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
package com.fluxtion.runtime.event;

import com.fluxtion.runtime.StaticEventProcessor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An event that registers an {@link StaticEventProcessor} as a sink in another SEP. The
 * receiving SEP must listen for this event. Can be useful if a SEP wants to
 * forward events it produces to another SEP, this event is used to register the
 * target SEP.<br>
 *
 * {@link EventPublisher} is a node that forwards events to registered
 * EventHandlers.
 *
 * @author V12 Technology Ltd.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RegisterEventHandler implements Event {

    private final StaticEventProcessor handler;
    private boolean register;

    public RegisterEventHandler(StaticEventProcessor handler) {
        this(handler, true);
    }

    /**
     * Register/Unregister an EVentHandler
     *
     * @param handler the target
     * @param register register/unregister
     */
    public RegisterEventHandler(StaticEventProcessor handler, boolean register) {
        this.handler = handler;
        this.register = register;
    }

    public StaticEventProcessor getHandler() {
        return handler;
    }

    public boolean isRegister() {
        return register;
    }

}
