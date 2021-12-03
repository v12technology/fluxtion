/*
 * Copyright (C) 2021 V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.triggeroverride;

import com.fluxtion.ext.declarative.builder.stream.StreamInProcessTest;
import static com.fluxtion.ext.streaming.api.stream.Argument.argDouble;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.FilterBuilder.filter;
import static com.fluxtion.ext.streaming.builder.factory.MappingBuilder.map;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsBuilder.multiply;
import lombok.Data;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class TriggerOverrideWrapperTest extends StreamInProcessTest {
    
    
    @Test
    public void testOverride(){
    
        sep(c ->{
            Receiver receiver = c.addNode(new Receiver(), "receiver");
            map(multiply(), argDouble(select(Integer.class)), argDouble(select(Integer.class)))
                    .triggerOverride(filter("publish"::equalsIgnoreCase))
                    .push(receiver::setResult);
        
        });
        Receiver receiver = getField("receiver");
        MatcherAssert.assertThat(receiver.getResult(), is(0.0));
        onEvent(10);
        MatcherAssert.assertThat(receiver.getResult(), is(0.0));
        onEvent("publish");
        MatcherAssert.assertThat(receiver.getResult(), is(100.0));
    }
    
    @Data
    public static class Receiver{
        double result;
    }
}
