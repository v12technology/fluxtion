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
package com.fluxtion.ext.declarative.builder.filter;

import static com.fluxtion.ext.declarative.api.stream.NumericPredicates.gt;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.count;
import com.fluxtion.ext.declarative.builder.stream.StreamInprocessTest;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.cumSum;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class FilterTest extends StreamInprocessTest {

    @Test
    public void testOnNotifyControl() {
        sep((c) -> {
            cumSum(DataEvent::getValue).id("sum")
                    .filter(gt(10))
                    .map(count()).id("count");
        });

        Number count = getWrappedField("count");
        Number sum = getWrappedField("sum");
        DataEvent de1 = new DataEvent();
        de1.value = 2;
        sep.onEvent(de1);
        sep.onEvent(de1);
        Assert.assertThat(sum.intValue(), is (4));
        Assert.assertThat(count.intValue(), is (0));
        
        de1.value = 10;
        sep.onEvent(de1);
        Assert.assertThat(sum.intValue(), is (14));
        Assert.assertThat(count.intValue(), is (1));
    }
}
