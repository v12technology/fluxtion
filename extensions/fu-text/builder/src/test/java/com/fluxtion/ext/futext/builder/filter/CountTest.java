/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.ext.futext.builder.filter;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.generator.util.BaseSepTest;
import static com.fluxtion.extension.declarative.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.api.window.UpdateCountTest;
import com.fluxtion.ext.futext.builder.test.helpers.DataEvent;
import com.fluxtion.ext.futext.builder.test.helpers.UpdateCount;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Greg Higgins
 */
public class CountTest extends BaseSepTest {

    @Test
    public void countTest1() {
        System.out.println("testSelectAndAlwaysNotify");
        buildAndInitSep(Builder1.class);
        UpdateCount counter = getField("counter");
        //send data
        onEvent(new DataEvent());
        assertEquals(0, counter.count);
        onEvent(new DataEvent());
        assertEquals(0, counter.count);
        onEvent(new DataEvent());
        assertEquals(1, counter.count);

        onEvent(new DataEvent());
        assertEquals(1, counter.count);
        onEvent(new DataEvent());
        assertEquals(1, counter.count);
        onEvent(new DataEvent());
        assertEquals(2, counter.count);

    }

    public static class Builder1 extends SEPConfig {
        {
            addPublicNode(new UpdateCount(UpdateCountTest.updateCount(select(DataEvent.class), 3)), "counter");
        }
    }
}
