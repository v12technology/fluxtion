/* 
 *  Copyright (C) [2016]-[2017] V12 Technology Limited
 *  
 *  This software is subject to the terms and conditions of its EULA, defined in the
 *  file "LICENCE.txt" and distributed with this software. All information contained
 *  herein is, and remains the property of V12 Technology Limited and its licensors, 
 *  if any. This source code may be protected by patents and patents pending and is 
 *  also protected by trade secret and copyright law. Dissemination or reproduction 
 *  of this material is strictly forbidden unless prior written permission is 
 *  obtained from V12 Technology Limited.  
 */
package com.fluxtion.ext.declarative.builder.window;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.builder.event.EventSelect;
import static com.fluxtion.ext.declarative.builder.event.EventSelect.select;
import com.fluxtion.ext.declarative.api.EventWrapper;
import com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilder;
import static com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilder.function;
import com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilderTest.CumSum;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.log.LogBuilder;
import static com.fluxtion.ext.declarative.builder.log.LogBuilder.Log;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.generator.util.BaseSepTest;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Greg Higgins
 */
public class WindowTest extends BaseSepTest {

    @Test
    public void test1() {
        buildAndInitSep(WinBuilder1.class);
        NumericValue cumSum = getField("cumSumTest");
        DataEvent de = new DataEvent();
        de.value = 10;
        assertEquals(0, cumSum.intValue());
        onEvent(de);
        assertEquals(0, cumSum.intValue());
        onEvent(de);
        assertEquals(0, cumSum.intValue());
        onEvent(de);
        assertEquals(30, cumSum.intValue());
        //new tumble window - should reset but keep the old value
        de.value = 20;
        onEvent(de);
        assertEquals(30, cumSum.intValue());
        onEvent(de);
        de.value = 60;
        assertEquals(30, cumSum.intValue());
        onEvent(de);
        assertEquals(100, cumSum.intValue());
        //new tumble window - should reset but keep the old value
        onEvent(de);
        assertEquals(100, cumSum.intValue());
    }

    @Test
    public void test2() {
        buildAndInitSep(WinBuilder2.class);
        DataEvent de = new DataEvent();
        NumericValue cumSum = getField("cumSumTest");
        de.value = 10;
        for (int i = 0; i < 2; i++) {
            onEvent(de);
        }
        assertEquals(0, cumSum.intValue());
        onEvent(de);
        assertEquals(30, cumSum.intValue());
        onEvent(de);
        assertEquals(30, cumSum.intValue());
        onEvent(de);
        onEvent(de);
        assertEquals(60, cumSum.intValue());
        de.value = 100;
        onEvent(de);
        assertEquals(60, cumSum.intValue());
        onEvent(de);
        assertEquals(60, cumSum.intValue());
        onEvent(de);
        assertEquals(340, cumSum.intValue());

    }

    public static class WinBuilder1 extends SEPConfig {

        {
            NumericValue cumSum = function(CumSum.class)
                    .input(DataEvent.class, DataEvent::getValue)
                    .countWin(3)
                    .build();
            addPublicNode(cumSum, "cumSumTest");
            Log("<-Tumbling window update CumSum::{}", cumSum, cumSum::intValue);
            Log("->DataEvent val::{}", select(DataEvent.class), DataEvent::getValue);
        }
    }

    public static class WinBuilder2 extends SEPConfig {

        {
            NumericValue cumSum = function(CumSum.class)
                    .input(DataEvent.class, DataEvent::getValue)
                    .countSlideWin(7, 3)
                    .build();
            addPublicNode(cumSum, "cumSumTest");
            Log("<-Sliding window update CumSum::{}", cumSum, cumSum::intValue);
            Log("->DataEvent val::{}", select(DataEvent.class), DataEvent::getValue);
        }
    }
    
}
