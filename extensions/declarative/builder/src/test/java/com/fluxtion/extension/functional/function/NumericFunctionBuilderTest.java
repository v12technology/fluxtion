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

import com.fluxtion.extension.declarative.builder.function.NumericFunctionBuilder;
import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateless;
import com.fluxtion.extension.declarative.api.numeric.NumericFunctionStateful;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.functional.helpers.DataEvent;
import com.fluxtion.extension.declarative.api.numeric.MutableNumericValue;
import com.fluxtion.extension.declarative.api.numeric.NumericConstant;
import com.fluxtion.extension.declarative.api.numeric.NumericResultRelay;
import com.fluxtion.extension.declarative.api.numeric.NumericResultTarget;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import org.junit.Test;
import com.fluxtion.extension.declarative.api.EventWrapper;
import static com.fluxtion.extension.declarative.builder.function.NumericFunctionBuilder.function;
import com.fluxtion.extension.functional.helpers.MyData;
import com.fluxtion.extension.functional.helpers.Tests.Greater;
import com.fluxtion.extension.functional.helpers.Tests.Smaller;
import static com.fluxtion.extension.declarative.builder.log.LogBuilder.Log;
import static com.fluxtion.extension.declarative.builder.event.EventSelect.select;
import com.fluxtion.generator.util.BaseSepTest;
import static com.fluxtion.extension.declarative.builder.test.TestBuilder.buildTest;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;

/**
 *
 * @author greg
 */
public class NumericFunctionBuilderTest extends BaseSepTest{

    public NumericFunctionBuilderTest() {
    }

    public static class MyCalculation implements NumericFunctionStateless {

        public short calc(int one, double two, short three) {
            return (short) Math.max(one, Math.max(two, three));
        }
    }

    public static class CumSum implements NumericFunctionStateful {

        public double calc(double prevVal, double newVal) {
            return prevVal + newVal;
        }
    }

    /**
     * Test of function method, of class NumericFunctionBuilder.
     */
    @Test
    public void testFunction() throws Exception {
        System.out.println("testFunction");
//        JavaTestGeneratorHelper.setupDefaultTestContext(
//                "com.fluxtion.extension.fucntional.test.generated.functionWrapper_1", "FunctionWrapper_1");

        NumericValue source1 = new NumericConstant(2);
        NumericValue source2 = new NumericConstant(4);

        NumericValue mixedTypes = NumericFunctionBuilder
                .function(MyCalculation.class)
                .input(source1, NumericValue::intValue)
                .input(source2)
                .input(source2, NumericValue::shortValue)
                .build();

        NumericValue castTypes = NumericFunctionBuilder
                .function(MyCalculation.class)
                .input(source1, NumericValue::intValue)
                .input(source2, NumericValue::intValue, true)
                .input(source2, NumericValue::doubleValue, true)
                .build();

        NumericValue aggregateFunction = NumericFunctionBuilder
                .function(CumSum.class)
                .input(source2, NumericValue::doubleValue)
                .build();

        NumericValue primitive = NumericFunctionBuilder
                .function(MyCalculation.class)
                .input(source1, NumericValue::intValue)
                .input(5.3)
                .input(source2, NumericValue::shortValue)
                .build();

        NumericValue eventInput = NumericFunctionBuilder
                .function(MyCalculation.class)
                .input(DataEvent.class, DataEvent::getValue)
                .input(5.3)
                .input(source2, NumericValue::shortValue)
                .build();

        NumericValue eventInputCast = NumericFunctionBuilder
                .function(MyCalculation.class)
                .input(DataEvent.class, DataEvent::getValue, true)
                .input(5.3)
                .input(source2, NumericValue::shortValue)
                .build();

    }

    @Test
    public void testPushFunction() throws Exception {
        System.out.println("testPushFunction");
//        JavaTestGeneratorHelper.setupDefaultTestContext(
//                "com.fluxtion.extension.fucntional.test.generated.functionPush_1", "FunctionWrapper_1");

        NumericValue source1 = new NumericConstant(2);
        NumericValue source2 = new NumericConstant(4);
        MutableNumericValue targetInt = new MutableNumericValue();
        MutableNumericValue targetDouble = new MutableNumericValue();
        ResultReceiver targetChar = new ResultReceiver();

        NumericValue mixedTypes = NumericFunctionBuilder
                .function(MyCalculation.class)
                .input(source1)
                .input(source2)
                .input(source2)
                .push(targetChar, ResultReceiver::setMyByte)
                .pushChar(targetChar, ResultReceiver::setMyChar)
                .push(targetChar, ResultReceiver::setMyShort)
                .push(targetChar, ResultReceiver::setMyInt)
                .push(targetChar, ResultReceiver::setMyLong)
                .push(targetChar, ResultReceiver::setMyFloat)
                .push(targetChar, ResultReceiver::setMyDouble)
                .push(targetInt)
                .push(targetDouble)
                .build();
    }

    @Test
    public void testSepFunction() throws Exception {
        System.out.println("testSepFunction");
        EventHandler sep = buildAndInitSep(Builder.class);
        //
        MutableNumericValue resultFX = new MutableNumericValue();
        MutableNumericValue resultAll = new MutableNumericValue();
        NumericResultTarget targetFX = new NumericResultTarget(resultFX, "cum sum FX");
        NumericResultTarget targetEq = new NumericResultTarget(resultAll, "cum sum All");
        sep.onEvent(targetFX);
        sep.onEvent(targetEq);

        DataEvent de1 = new DataEvent();
        //fire some events for FX - should be procecessed
        de1.setDataKey("FX");
        de1.value = 200;
        sep.onEvent(de1);
        Assert.assertThat(200, is(resultFX.intValue));
        sep.onEvent(de1);
        Assert.assertThat(400, is(resultFX.intValue));
        de1.value = 600;
        sep.onEvent(de1);
        Assert.assertThat(1000, is(resultFX.intValue));
        //fire some events for EQ - should be ignored
        de1.setDataKey("EQ");
        sep.onEvent(de1);
        Assert.assertThat(1600, is(resultAll.intValue));
        sep.onEvent(de1);
        Assert.assertThat(2200, is(resultAll.intValue));
        sep.onEvent(de1);
        Assert.assertThat(2800, is(resultAll.intValue));

        System.out.println("result FX : " + resultFX);
        System.out.println("result All: " + resultAll);
    }
    
    @Test
    public void testFilterAndFunction() throws Exception {
        System.out.println("testFilterAndFunction");
        EventHandler sep = buildAndInitSep(BuilderFilter1.class);
        MyData de1 = new MyData(600, 600, "EU");
        //fire some events for FX - should be procecessed
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1 = new MyData(100, 100, "EU");
        sep.onEvent(de1);
        //fire some events for EQ - should be ignored
        de1 = new MyData(600, 600, "EC");
        sep.onEvent(de1);
        de1 = new MyData(100, 100, "EC");
        sep.onEvent(de1);
        sep.onEvent(de1);
    }
    
    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            NumericValue cumSumRsult = NumericFunctionBuilder.function(CumSum.class)
                    .input(DataEvent.class, DataEvent::getValue)
                    .build();
            NumericValue cumSumRsultFiltered = NumericFunctionBuilder.function(CumSum.class)
                    .input(select(DataEvent.class, "FX"), DataEvent::getValue)
                    .build();
            addNode(new NumericResultRelay("cum sum All", cumSumRsult));
            addNode(new NumericResultRelay("cum sum FX", cumSumRsultFiltered));
        }
    }

    public static class BuilderFilter1 extends SEPConfig {

        public BuilderFilter1() {
            //data handlers
            final EventWrapper<MyData>[] eu_ec_handler = select(MyData.class, "EU", "EC");
            //filter on size
            Wrapper<MyData> euec_gt_200 = buildTest(Greater.class, eu_ec_handler, MyData::getIntVal).arg(200).buildFilter();
            Wrapper<MyData> euec_lt_200 = buildTest(Smaller.class, eu_ec_handler, MyData::getIntVal).arg(200).buildFilter();
            //cumulative sum per filter
            NumericValue cumSumGt200 = function(CumSum.class).input(euec_gt_200, MyData::getIntVal).build();
            NumericValue cumSumLt200 = function(CumSum.class).input(euec_lt_200, MyData::getIntVal).build();
            //log 
            Log("-> rcvd MyData trade='{}' volume='{}'", select(MyData.class), MyData::getFilterString, MyData::getIntVal);
            Log("cumSum where volume > 200 = '{}'", cumSumGt200, NumericValue::intValue);
            Log("cumSum where volume < 200 = '{}'", cumSumLt200, NumericValue::intValue);
        }

    }
}
