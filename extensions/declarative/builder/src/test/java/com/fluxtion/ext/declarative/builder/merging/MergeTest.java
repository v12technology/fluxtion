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
package com.fluxtion.ext.declarative.builder.merging;

import com.fluxtion.api.event.Event;
import static com.fluxtion.ext.declarative.api.MergingWrapper.merge;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.api.stream.StreamFunctions.count;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.builder.stream.BaseSepInprocessTest;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class MergeTest extends BaseSepInprocessTest {

    @Test
    public void mapRef2Ref() {
//        fixedPkg = true;
        sep((c) -> {
            select(EventC.class).map(count()).id("nonMerged");
            merge(Events.class, select(EventA.class), select(EventB.class)).map(count()).id("merged");
        });
        Wrapper<Number> nonMerged = getField("nonMerged");
        Wrapper<Number> merged = getField("merged");
        onEvent(new EventA());
        onEvent(new EventA());
        onEvent(new EventB());
        onEvent(new EventB());
        onEvent(new EventA());
        onEvent(new EventC());
        onEvent(new EventC());
        onEvent(new EventA());
        //
        assertThat(merged.event().intValue(), is(6));
        assertThat(nonMerged.event().intValue(), is(2));
    }
    
    public static class Events extends Event{}
    public static class EventA extends Events{}
    public static class EventB extends Events{}
    public static class EventC extends Events{}
    
    
}
