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
package com.fluxtion.extension.functional.filter;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.extension.declarative.builder.event.EventSelect;
import com.fluxtion.extension.declarative.api.EventWrapper;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import fluxtion.extension.functional.test.helpers.DataEvent;
import org.junit.Test;
import static com.fluxtion.extension.declarative.builder.log.LogBuilder.Log;
import static com.fluxtion.extension.declarative.builder.log.LogBuilder.LogOnNotify;
import static com.fluxtion.extension.declarative.builder.log.LogBuilder.buildLog;
import static com.fluxtion.ext.futext.builder.math.CumSumFunctions.cumSum;
import static com.fluxtion.ext.futext.builder.math.SubtractFunctions.subtract;
import static com.fluxtion.ext.futext.builder.test.GreaterThanHelper.greaterThanOnce;
import static com.fluxtion.ext.futext.builder.test.LessThanHelper.lessThanOnce;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection.SerializableSupplier;

/**
 *
 * @author Greg Higgins
 */
public class FilterTest {

    @Test
    public void simpleFilterHelperTest() throws Exception {
        SepCompilerConfig compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(
                "com.fluxtion.extension.functional.filter.simpleFilterHelperTest",
                "GreaterThan1");
        compileCfg.setConfigClass(CompoundBuilder.class.getName());
        compileCfg.setSupportDirtyFiltering(true);
        EventHandler sep = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
        ((Lifecycle) sep).init();
        //fire some events for FX - ignored any non-Euro trades 
        DataEvent de1 = new DataEvent();
        de1.setFilterString("EU");
        de1.value = 2;
        sep.onEvent(de1);
        de1.setFilterString("EC");
        sep.onEvent(de1);
        de1.setFilterString("UE");
        sep.onEvent(de1);
        de1.value = 600;
        sep.onEvent(de1);
        de1.setFilterString("EY");
        sep.onEvent(de1);
        de1.setFilterString("CE");
        sep.onEvent(de1);
        de1.value = -1000;
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1.value = 500;
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        sep.onEvent(de1);
        de1.value = -1500;
        sep.onEvent(de1);
    }

    public static class CompoundBuilder extends SEPConfig {

        /**
         * WARN message when EUR position > 10 No repeated warnings for
         * subsequent trades Remove WARN when EUR pos < 10
         *
         * @throws Exception
         */
        public CompoundBuilder() throws Exception {
            //calculations
            EventWrapper<DataEvent> tradeEventHandler = EventSelect.select(DataEvent.class);
            NumericValue eurDealtPos = cumSum(DataEvent.class, DataEvent::getValue, "EU", "EC", "EG", "EY");
            NumericValue eurContraPos = cumSum(DataEvent.class, DataEvent::getValue, "UE", "CE", "GE", "YE");
            NumericValue eurNetPos = subtract(eurDealtPos, eurContraPos);
            //logging and reporting
            Log("-> Trade recived:'{}'@'{}' ", tradeEventHandler,
                    DataEvent::getStringValue, DataEvent::getValue);

            buildLog("<- Position update: EUR net:{} dealt:{} contra:{}", tradeEventHandler)
                    .input(eurNetPos, eurNetPos::intValue)
                    .input(eurDealtPos, eurDealtPos::intValue)
                    .input(eurContraPos, eurContraPos::intValue)
                    .build();

            //condtional alerts
            SerializableSupplier eurPosInt = eurNetPos::intValue;
            LogOnNotify("NEW 1 <- *  POS WARNING  * create    : -> EUR position:'{}' breached warn limit of 10", greaterThanOnce(eurNetPos, 10), eurNetPos, eurPosInt);
            LogOnNotify("NEW 2 <- *  POS WARNING  * delete : X  EUR position:'{}' dropped below warn limit of 10", lessThanOnce(eurNetPos, 10), eurNetPos, eurPosInt);
            LogOnNotify("NEW 3 <- ** POS CRITICAL ** create   : -> EUR position:'{}' breached critical limit of 1000", greaterThanOnce(eurNetPos, 1000), eurNetPos, eurPosInt);
            LogOnNotify("NEW 4 <- ** POS CRITICAL ** delete: X  EUR position:'{}' dropped below critical limit of 1000", lessThanOnce(eurNetPos, 1000), eurNetPos, eurPosInt);

        }
    }
}
