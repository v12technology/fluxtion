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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.event.Event;
import com.fluxtion.ext.streaming.api.Wrapper;
import static com.fluxtion.ext.streaming.api.stream.NumericPredicates.gt;
import static com.fluxtion.ext.streaming.builder.event.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.stream.StreamBuilder.stream;
import static com.fluxtion.ext.streaming.builder.stream.StreamFunctionsBuilder.count;
import static com.fluxtion.ext.streaming.builder.test.BooleanBuilder.and;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class StreamingBooleanTest extends StreamInprocessTest{
    
    @Test
    public void statefulAnd(){
        sep((c) ->{
            Wrapper<StreamData> gt10 = select(StreamData.class)
                    .filter(StreamData::getIntValue, gt(10))
                    .resetNotifier(select(ResetEvent.class));
            
            stream(and(select(CalcEvent.class), gt10)).map(count()).id("count");
            
        });
        
        Number count = getWrappedField("count");
        onEvent(new StreamData(4));
        assertThat(count.intValue(), is(0));
        onEvent(new StreamData(9));
        assertThat(count.intValue(), is(0));
        onEvent(new CalcEvent());
        assertThat(count.intValue(), is(0));
        onEvent(new StreamData(100));
        assertThat(count.intValue(), is(0));
        assertThat(count.intValue(), is(0));
        //calc
        onEvent(new CalcEvent());
        assertThat(count.intValue(), is(1));
        //reset
        onEvent(new ResetEvent());
        onEvent(new CalcEvent());
        assertThat(count.intValue(), is(1));
        //
        onEvent(new StreamData(12));
        assertThat(count.intValue(), is(1));
        onEvent(new CalcEvent());
        assertThat(count.intValue(), is(2));
        
        
    }
    
    public static class ResetEvent extends Event{}
    public static class CalcEvent extends Event{}
    
}
