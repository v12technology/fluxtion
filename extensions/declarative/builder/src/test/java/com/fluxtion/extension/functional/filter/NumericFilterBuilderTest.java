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
package com.fluxtion.extension.functional.filter;

import com.fluxtion.api.node.SEPConfig;
import static com.fluxtion.extension.declarative.builder.function.NumericFunctionBuilder.function;
import com.fluxtion.extension.functional.function.NumericFunctionBuilderTest;
import com.fluxtion.extension.functional.helpers.DataEvent;
import com.fluxtion.extension.declarative.builder.log.LogBuilder;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import org.junit.Test;
//import static com.fluxtion.extension.functional.filter.NumericFilterBuilder.buildFilter;
import static com.fluxtion.extension.declarative.builder.test.TestBuilder.buildTest;

/**
 *
 * @author Greg Higgins
 */
public class NumericFilterBuilderTest {

    @Test
    public void testSimpleFilter() throws Exception {
        System.out.println("function");
        SepCompilerConfig compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(
                "com.fluxtion.extension.fucntional.test.generated.fiterWrapper_1", "GreaterThan1");
        compileCfg.setConfigClass(Builder.class.getName());
        compileCfg.setSupportDirtyFiltering(true);
        EventHandler sep = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
        ((Lifecycle) sep).init();

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

    public static class GreaterThan implements com.fluxtion.extension.declarative.api.Test {

        public boolean greaterThan(double op1, double op2) {
            return op1 > op2;
        }
    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            NumericValue cumSumRsult = function(NumericFunctionBuilderTest.CumSum.class)
                    .input(DataEvent.class, DataEvent::getValue)
                    .build();
            com.fluxtion.extension.declarative.api.Test notifyGt10;
            notifyGt10 = buildTest(GreaterThan.class,  cumSumRsult).arg(10).build();

            LogBuilder.LogOnNotify("WARNING sum {} > 10", notifyGt10, cumSumRsult, cumSumRsult::intValue);
            supportDirtyFiltering = true;
        }
    }

}
