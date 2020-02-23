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
package com.fluxtion.compiler;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepTestInstance;
import static com.fluxtion.generator.util.ClassUtils.getField;
import com.fluxtion.test.event.TimeEvent;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class InprocessSepCompilerTest {

    @Test
    public void inProcessTestSimple() throws InstantiationException, IllegalAccessException, Exception {
        StaticEventProcessor sep = sepTestInstance(this::buildSepSingle, "com.gh.test", "GenNode_" + System.currentTimeMillis());
        MyHandler handler = getField("handler", sep);
        assertThat(handler.count, is(0));
        sep.onEvent(new TimeEvent());
        assertThat(handler.count, is(1));
    }

    public void buildSepSingle(SEPConfig cfg) {
        cfg.addNode(new MyHandler(), "handler");
    }

    public static class MyHandler {

        int count;

        @com.fluxtion.api.annotations.EventHandler
        public void onAllTimeEvents(TimeEvent e) {
            count++;
        }
    }

}
