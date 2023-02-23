/*
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.test.event;

import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.TearDown;
import com.fluxtion.runtime.node.AbstractEventHandlerNode;

/**
 * @author Greg Higgins
 */
public class EventHandlerCbNode extends AbstractEventHandlerNode<TestEvent> {

    public String id;

    public EventHandlerCbNode(String id, int filterId) {
        super(filterId);
        this.id = id;
    }

    public EventHandlerCbNode() {
    }

    @Override
    public Class<TestEvent> eventClass() {
        return TestEvent.class;
    }

    @TearDown
    public void tearDown() {

    }

    @OnTrigger
    public void onParentChange() {
    }

    @Override
    public boolean onEvent(TestEvent e) {
        return true;
    }

    @Override
    public String toString() {
        return "EventHandlerCb{" + "id=" + id + ", filterId=" + filterId + '}';
    }


}
