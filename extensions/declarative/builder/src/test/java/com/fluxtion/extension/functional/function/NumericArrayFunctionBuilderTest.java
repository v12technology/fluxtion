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
package com.fluxtion.extension.functional.function;

import com.fluxtion.extension.declarative.builder.function.NumericArrayFunctionBuilder;
import com.fluxtion.ext.declarative.api.numeric.NumericArrayFunctionStateless;
import com.fluxtion.ext.declarative.api.numeric.NumericArrayFunctionStateful;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.extension.declarative.builder.event.EventSelect;
import com.fluxtion.ext.declarative.api.EventWrapper;
import static com.fluxtion.extension.declarative.builder.function.NumericArrayFunctionBuilder.buildFunction;
import com.fluxtion.extension.functional.helpers.DataEvent;
import com.fluxtion.extension.functional.helpers.UpdatedDataEvent;
import com.fluxtion.ext.declarative.api.numeric.NumericResultRelay;
import com.fluxtion.ext.declarative.api.numeric.NumericResultTarget;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class NumericArrayFunctionBuilderTest extends BaseSepTest {

    public static class SumIncArray implements NumericArrayFunctionStateful {

        public double calcIncSum(double prevTotal, double newVal) {
            return prevTotal + newVal;
        }

        @Override
        public double reset() {
            return 0;
        }
    }

    public static class SumArray implements NumericArrayFunctionStateless {

        public double calcSum(double prevTotal, double newVal) {
            return prevTotal + newVal;
        }
    }

    /**
     * tests compile is working
     *
     * @throws Exception
     */
    @Test
    public void testSimpleStatefulArray() throws Exception {
        EventWrapper<DataEvent>[] dataHandler = EventSelect.select(DataEvent.class, "FORWARD", "FX");
        EventWrapper<UpdatedDataEvent>[] updateHandler = EventSelect.select(UpdatedDataEvent.class, "OPTION", "EQ", "FX");

        NumericArrayFunctionBuilder function = NumericArrayFunctionBuilder.function(SumIncArray.class);
        function.input(DataEvent::getValue, true, dataHandler);
        function.input(UpdatedDataEvent::getUPdatedValue, true, updateHandler);
        function.push(new ResultReceiver(), ResultReceiver::setMyDouble);

        function.push(new ResultReceiver(), ResultReceiver::setMyByte);
        function.pushChar(new ResultReceiver(), ResultReceiver::setMyChar);
        function.push(new ResultReceiver(), ResultReceiver::setMyShort);
        function.push(new ResultReceiver(), ResultReceiver::setMyInt);
        function.push(new ResultReceiver(), ResultReceiver::setMyLong);
        function.push(new ResultReceiver(), ResultReceiver::setMyFloat);
        function.push(new ResultReceiver(), ResultReceiver::setMyDouble);

        function.build();
    }

    /**
     * tests compile is working
     *
     * @throws Exception
     */
    @Test
    public void testSimpleStatelessArray() throws Exception {
//        System.out.println("testSimpleStatelessArray");
        EventWrapper<DataEvent>[] dataHandler = EventSelect.select(DataEvent.class, "FORWARD", "FX");
        EventWrapper<UpdatedDataEvent>[] updateHandler = EventSelect.select(UpdatedDataEvent.class, "OPTION", "EQ", "FX");

        NumericArrayFunctionBuilder function = NumericArrayFunctionBuilder.function(SumArray.class);
        function.input(DataEvent::getValue, true, dataHandler);
        function.input(UpdatedDataEvent::getUPdatedValue, true, updateHandler);
        function.build();
    }

    @Test
    public void generateArraySepProcessor() throws Exception {
        buildAndInitSep(Builder.class);
        //add results listeners
        NumericResultTarget targetFX = new NumericResultTarget( "result FX+Options");
        NumericResultTarget targetEq = new NumericResultTarget( "result EQ");
        sep.onEvent(targetFX);
        sep.onEvent(targetEq);
        //fire some events for FX - ignored ny EQ 
        DataEvent de1 = new DataEvent();
        de1.setDataKey("FX");
        de1.value = 200;
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1.value = 600;
        sep.onEvent(de1);
        //fire some events for EQ - ignored by FX|OPTIONS
        de1.setDataKey("EQ");
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        //fire some events for OPTIONS - ignored ny EQ
        de1.setDataKey("OPTIONS");
        de1.value = 2000;
        sep.onEvent(de1);

        assertThat(targetFX.getTarget().intValue(), is(3000));
        assertThat(targetEq.getTarget().intValue(), is(1800));
        System.out.println("ResultFX:" + targetFX.getTarget().intValue());
        System.out.println("ResultEq:" + targetEq.getTarget().intValue());
    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            //pseudo sql:
            //select sum(value) from dataTable where id ="FX" or id = "OPTIONS" sumFx
            NumericValue sumFx = buildFunction(SumIncArray.class,
                    DataEvent.class, DataEvent::getValue, "FX", "OPTIONS");
            NumericValue sumEq = buildFunction(SumIncArray.class,
                    DataEvent.class, DataEvent::getValue, "EQ");
            //results listeners
            addNode(new NumericResultRelay("result FX+Options", sumFx));
            addNode(new NumericResultRelay("result EQ", sumEq));
        }

    }
}
