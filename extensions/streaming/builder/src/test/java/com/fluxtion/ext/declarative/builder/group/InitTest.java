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
package com.fluxtion.ext.declarative.builder.group;

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import org.junit.Test;

import static com.fluxtion.ext.streaming.builder.group.Group.groupBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class InitTest extends StreamInprocessTest {
    
    @Test
    public void testInit(){
        sep((c) ->{
            groupBy(Order::getCcyPair, OrderSummary.class)
                .init(Order::getSize, OrderSummary::setOrderSize)
                .initPrimitive(Order::getSize, OrderSummary::setVolumeDealt)
                .build()
                .id("group");
        });
    
        onEvent(new Order(1, "EURUSD", 200));
        onEvent(new Order(1, "EURJPY", 80));
        onEvent(new Order(1, "EURUSD", 4378));
        onEvent(new Order(1, "EURJPY", 6564));
        GroupBy<OrderSummary> group = getField("group");
        
        assertThat(group.value("EURUSD").getOrderSize(), is(200));
        assertThat(group.value("EURUSD").getVolumeDealt(), is(200.0));
        assertThat(group.value("EURJPY").getOrderSize(), is(80));
        assertThat(group.value("EURJPY").getVolumeDealt(), is(80.0));
        
    }
    
}
