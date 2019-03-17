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
package com.fluxtion.ext.futext.builder.math1;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.builder.event.EventSelect;
import com.fluxtion.ext.declarative.api.EventWrapper;
import static com.fluxtion.ext.futext.builder.math.MultiplyFunctions.multiply;
import static com.fluxtion.ext.futext.builder.math.AddFunctions.add;
import com.fluxtion.ext.futext.builder.test.helpers.DataEvent;
import com.fluxtion.ext.futext.builder.test.helpers.DataEvent_2;
import com.fluxtion.generator.util.BaseSepTest;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class MathFunctionTest extends BaseSepTest{

    @Test
    public void generateProcessor() throws Exception {
        buildAndInitSep(Builder.class);

    }

    @Test
    public void generateArrayProcessor() throws Exception {
        buildAndInitSep(BuilderArray.class);
    }



    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            EventWrapper<DataEvent> temp = EventSelect.select(DataEvent.class, "temp");
            EventWrapper<DataEvent> offset = EventSelect.select(DataEvent.class, "offset");
            add(DataEvent.class, DataEvent::getValue, DataEvent_2.class, DataEvent_2::getValue);
            multiply(temp, DataEvent::getValue, offset, DataEvent::getValue);
        }

    }

    public static class BuilderArray extends SEPConfig {

        public BuilderArray() throws Exception {
            EventWrapper<DataEvent>[] temp = EventSelect.select(DataEvent.class, "temp", "outsideTemp");
            EventWrapper<DataEvent> offset = EventSelect.select(DataEvent.class, "offset");
            add(DataEvent.class, DataEvent::getValue, DataEvent_2.class, DataEvent_2::getValue);
            multiply(temp[0], DataEvent::getValue, temp[1], DataEvent::getValue);
        }

    }


}
