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
package com.fluxtion.ext.declarative.builder.stream;

import com.fluxtion.api.event.Event;
import static com.fluxtion.ext.declarative.api.MergingWrapper.merge;
import com.fluxtion.ext.declarative.api.Wrapper;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import static com.fluxtion.ext.declarative.builder.stream.StreamFunctionsBuilder.cumSum;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class MathFunctionTest extends BaseSepInprocessTest {

    @Test
    public void testIncSumArray() throws Exception {
        sep((c) -> {
            merge(select(DataEvent.class, "RED", "GREEN"))
                    .map(cumSum(), DataEvent::getValue)
                    .id("redGreen");

            merge(select(DataEvent.class, 1, 2, 3))
                    .map(cumSum(), DataEvent::getValue)
                    .id("num_1_2_3");
        });

        //fire some events for FX - ignored ny EQ 
        DataEvent de1 = new DataEvent();
        de1.setFilterString("RED");
        de1.value = 200;
        sep.onEvent(de1);
        de1.setFilterString("BLUE");
        sep.onEvent(de1);
        de1.setFilterString("GREEN");
        sep.onEvent(de1);
        de1.value = 600;
        sep.onEvent(de1);
        de1.setFilterInt(2);
        sep.onEvent(de1);
        //test
        Wrapper<Number> colours = getField("redGreen");
        Wrapper<Number> nums = getField("num_1_2_3");
        assertThat(colours.event().intValue(), is(1000));
        assertThat(nums.event().intValue(), is(600));
    }

    public static class DataEvent extends Event {

        public static final int ID = 1;

        public DataEvent() {
            super(ID);
        }

        public int value;

        public int getValue() {
            return value;
        }

        public void setFilterString(String key) {
            this.filterString = key;
            this.filterId = Integer.MAX_VALUE;
        }

        public void setFilterInt(int id) {
            this.filterString = "";
            this.filterId = id;

        }

        public String getStringValue() {
            return filterString.toString();
        }

    }
}
