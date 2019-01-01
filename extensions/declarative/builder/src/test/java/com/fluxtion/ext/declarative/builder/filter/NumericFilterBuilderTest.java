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
package com.fluxtion.ext.declarative.builder.filter;

import com.fluxtion.api.node.SEPConfig;
import static com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilder.function;
import com.fluxtion.ext.declarative.builder.function.NumericFunctionBuilderTest;
import com.fluxtion.ext.declarative.builder.helpers.DataEvent;
import com.fluxtion.ext.declarative.builder.log.LogBuilder;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import org.junit.Test;
import static com.fluxtion.ext.declarative.builder.test.TestBuilder.buildTest;
import com.fluxtion.generator.util.BaseSepTest;

/**
 *
 * @author Greg Higgins
 */
public class NumericFilterBuilderTest extends BaseSepTest {

    @Test
    public void testSimpleFilter() throws Exception {
        buildAndInitSep(Builder.class);        
        //fire some events
        DataEvent de1 = new DataEvent();
        de1.value = 2;
        sep.onEvent(de1);
        sep.onEvent(de1);
        System.out.println("sum should be 4");
        de1.value = 10;
        sep.onEvent(de1);
        System.out.println("sum should be 14 - a warning message should be above this");
    }

    public static class GreaterThan implements com.fluxtion.ext.declarative.api.Test {

        public boolean greaterThan(double op1, double op2) {
            return op1 > op2;
        }
    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            NumericValue cumSumRsult = function(NumericFunctionBuilderTest.CumSum.class)
                    .input(DataEvent.class, DataEvent::getValue)
                    .build();
            com.fluxtion.ext.declarative.api.Test notifyGt10;
            notifyGt10 = buildTest(GreaterThan.class,  cumSumRsult).arg(10).build();

            LogBuilder.LogOnNotify("WARNING sum {} > 10", notifyGt10, cumSumRsult, cumSumRsult::intValue);
            supportDirtyFiltering = true;
        }
    }

}
