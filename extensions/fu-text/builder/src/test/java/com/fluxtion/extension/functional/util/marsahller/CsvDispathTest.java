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
package com.fluxtion.extension.functional.util.marsahller;

import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.util.marshaller.DispatchingCsvMarshaller;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Limited
 */
public class CsvDispathTest {

    @Test
    public void testDispatch() {
        //state recorders
        LongAdder stringCount = new LongAdder();
        LongAdder intCount = new LongAdder();
        StringBuilder input = new StringBuilder();
        //set up idispatch
        DispatchingCsvMarshaller dispatcher = new DispatchingCsvMarshaller();
        dispatcher.init();
        dispatcher.addMarshaller(new StringWrapper(), (EventHandler) (Event e) -> {
            stringCount.increment();
            if (e instanceof CharEvent) {
                input.append(((CharEvent)e).getCharacter());
            }
        });
        dispatcher.addMarshaller(new IntegerWrapper(), (EventHandler) (Event e) -> {
            intCount.increment();
            if (e instanceof CharEvent) {
                input.append(((CharEvent)e).getCharacter());
            }
        });
        //
        StringDriver.streamChars("String,123\n", dispatcher, false);
        assertThat(input.toString(), is("123\n"));
        assertThat(stringCount.intValue(), is(4));
        assertThat(intCount.intValue(), is(0));
        input.setLength(0);
        intCount.reset();
        stringCount.reset();
        //tests
        StringDriver.streamChars("Date,11-23-2012\n", dispatcher, false);
        assertThat(stringCount.intValue(), is(0));
        assertThat(intCount.intValue(), is(0));
        intCount.reset();
        stringCount.reset();
        //
        StringDriver.streamChars("Integer,40\n", dispatcher, false);
        assertThat(stringCount.intValue(), is(0));
        assertThat(intCount.intValue(), is(3));
        assertThat(input.toString(), is("40\n"));
        intCount.reset();
        stringCount.reset();
    }

    public static class StringWrapper implements Wrapper<String> {

        @Override
        public String event() {
            return "null";
        }

        @Override
        public Class<String> eventClass() {
            return String.class;
        }

    }
    
    public static class IntegerWrapper implements Wrapper<Integer> {

        @Override
        public Integer event() {
            return 0;
        }

        @Override
        public Class<Integer> eventClass() {
            return Integer.class;
        }

    }
}
