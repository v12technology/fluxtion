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

import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.streaming.api.MergingWrapper.merge;
import static com.fluxtion.ext.streaming.builder.factory.EventSelect.select;
import static com.fluxtion.ext.streaming.builder.factory.StreamFunctionsLibrary.count;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class MergeTest extends StreamInprocessTest {

    @Test
    public void mapRef2Ref() {
//        fixedPkg = true;
        sep((c) -> {
            select(EventC.class).map(count()).id("nonMergedCount");
            merge(Events.class, select(EventA.class), select(EventB.class)).id("mergedStreams")
                    .map(count()).id("mergedCount");
        });
        Number nonMerged = getWrappedField("nonMergedCount");
        Number merged = getWrappedField("mergedCount");
        onEvent(new EventA());
        onEvent(new EventA());
        onEvent(new EventB());
        onEvent(new EventB());
        onEvent(new EventA());
        onEvent(new EventC());
        onEvent(new EventC());
        onEvent(new EventA());
        //
        assertThat(merged.intValue(), is(6));
        assertThat(nonMerged.intValue(), is(2));
    }
    
    public static class Events  {
    }

    public static class EventA extends Events {
    }

    public static class EventB extends Events {
    }

    public static class EventC extends Events {
    }
    
}
