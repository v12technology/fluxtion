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
package com.fluxtion.example.quickstart.lesson3;

import com.fluxtion.example.quickstart.lesson3.TradeMonitor.Trade;
import com.fluxtion.example.quickstart.lesson3.generated.TradeEventProcessor;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.util.Tuple;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import com.fluxtion.junit.SystemOutResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class TradeMonitorTest extends BaseSepInprocessTest {

    @Rule
    public SystemOutResource sysOut = new SystemOutResource();

    String window1_log = "Most active ccy pairs in past 5 seconds:\n"
        + "	 1. EURUSD - 5150 trades\n"
        + "	 2. USDCHF - 500 trades\n"
        + "	 3. EURJPY - 100 trades";
    String window2_sysout = "Most active ccy pairs in past 5 seconds:\n"
        + "	 1. USDCHF - 500 trades\n"
        + "	 2. EURUSD - 150 trades\n"
        + "	 3. EURJPY - 100 trades";
    
    @Test
    public void testTradeMonitor() {
        sep(TradeEventProcessor.class);
        sysOut.clear();
        tick(1);
        onEvent(new Trade("EURUSD", 5_000));
        tick(1200);
        onEvent(new Trade("EURUSD", 150));
        onEvent(new Trade("EURJPY", 100));
        tick(2100);
        onEvent(new Trade("USDCHF", 500));
        tick(4000);
        onEvent(new Trade("GBPUSD", 25));
        WrappedList<Tuple<String, Number>> top3 = getField("top3");
        assertThat(top3.size(), is(0));

        //advance to 5 seconds
        tick(5500);
        top3 = getField("top3");
        assertThat(top3.size(), is(3));
        assertThat(top3.get(0).getKey(), is("EURUSD"));
        assertThat(top3.get(0).getValue(), is(5_150d));
        assertThat(top3.get(1).getKey(), is("USDCHF"));
        assertThat(top3.get(1).getValue(), is(500d));
        assertThat(top3.get(2).getKey(), is("EURJPY"));
        assertThat(top3.get(2).getValue(), is(100d));
        assertThat(sysOut.asString().trim(), is(window1_log));

        //advance time but within a bucket, nothing will happen inbetween buckets
        sysOut.clear();
        tick(5999);
        top3 = getField("top3");
        assertThat(top3.size(), is(3));
        assertThat(sysOut.asString().trim(), is(""));

        //advance to new bucket will removes first EURUSD trade and triggers resort
        sysOut.clear();
        tick(6000);
        top3 = getField("top3");
        assertThat(top3.size(), is(3));
        assertThat(top3.get(0).getKey(), is("USDCHF"));
        assertThat(top3.get(0).getValue(), is(500d));
        assertThat(top3.get(1).getKey(), is("EURUSD"));
        assertThat(top3.get(1).getValue(), is(150d));
        assertThat(top3.get(2).getKey(), is("EURJPY"));
        assertThat(top3.get(2).getValue(), is(100d));
        assertThat(sysOut.asString().trim(), is(window2_sysout));
    }
}
