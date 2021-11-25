/*
 * Copyright (c) 2020, V12 Technology Ltd.
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
package com.fluxtion.ext.declarative.builder.lookup;

import com.fluxtion.api.event.Signal;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.enrich.EventDrivenLookup;
import com.fluxtion.ext.streaming.api.util.Tuple;
import lombok.Data;
import org.junit.Test;

import static com.fluxtion.ext.streaming.api.enrich.EventDrivenLookup.lookup;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class LookupTest extends StreamInprocessTest {
    
    @Test
    public void testLookup(){
        sep((c) ->{
            select(MyNode.class)
                .forEach(new EventDrivenLookup("mylookup", MyNode::getKey, MyNode::setValue)::lookupValue);
        });
        
        MyNode nodeEvent = new MyNode();
        nodeEvent.setKey("hello");
        nodeEvent.setValue("nobody");
        
        //seed a lookupValue value
        onEvent(new Signal<Tuple>("mylookup", new Tuple<>("hello", "world")));
        assertThat(nodeEvent.getValue(), is("nobody"));
        onEvent(nodeEvent);
        assertThat(nodeEvent.getValue(), is("world"));
    }
    
    @Test
    public void testLookupFactory(){
        sep((c) ->{
            select(MyNode.class)
                .forEach(lookup("mylookup", MyNode::getKey, MyNode::setValue));
        });
        
        MyNode nodeEvent = new MyNode();
        nodeEvent.setKey("hello");
        nodeEvent.setValue("nobody");
        
        //seed a lookupValue value
        onEvent(new Signal<Tuple>("mylookup", new Tuple<>("hello", "world")));
        assertThat(nodeEvent.getValue(), is("nobody"));
        onEvent(nodeEvent);
        assertThat(nodeEvent.getValue(), is("world"));
    }
    
    @Data
    public static class MyNode{
        
        String key;
        String value;
        
    }
}
