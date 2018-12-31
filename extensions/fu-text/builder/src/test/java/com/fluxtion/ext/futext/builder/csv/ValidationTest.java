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
package com.fluxtion.ext.futext.builder.csv;

import com.fluxtion.api.annotations.Config;
import com.fluxtion.api.annotations.Inject;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.extension.declarative.api.Wrapper;
import com.fluxtion.extension.declarative.api.numeric.NumericValue;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.and;
import static com.fluxtion.extension.declarative.builder.test.BooleanBuilder.nand;
import static com.fluxtion.extension.declarative.builder.test.TestBuilder.buildTest;
import com.fluxtion.extension.declarative.builder.util.LambdaReflection;
import com.fluxtion.ext.futext.api.csv.RowProcessor;
import static com.fluxtion.ext.futext.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.ext.futext.builder.math.CountFunction.count;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import static com.fluxtion.ext.futext.builder.csv.ValidationTest.NumberCompareValidators.gt;
import static com.fluxtion.ext.futext.builder.csv.ValidationTest.NumberCompareValidators.lt;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.runtime.lifecycle.EventHandler;
import org.junit.Assert;
import org.junit.Test;

public class ValidationTest extends BaseSepTest {

//    protected String testPackageID() {
//        return "";
//    }

    @Test
    public void testCsvWithHeaderAndRowCBFailedValidation() {
        final EventHandler sep = buildAndInitSep(WorldCitiesCsv_Header_OnEventCB_Validator.class);
        NumericValue count = getField("count");
        ValidationEvaluator validator = getField("validationEvaluator");
        String dataCsh = "Country,City,AccentCity,Region,Population,Latitude,Longitude\n"
                + "mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "mexico,aixirivali,Aixirivali,06,,1.2,1.5\n"
                + "mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "brazil,santiago,Aixirivall,06,,330,1.5";
        WorldCityBeanPrimitive city = ((Wrapper<WorldCityBeanPrimitive>) getField("city")).event();
        StringDriver.streamChars(dataCsh, sep, false);
        Assert.assertEquals(4, count.intValue());
        Assert.assertEquals(2, validator.failureCount);
    }

    public static class WorldCitiesCsv_Header_OnEventCB_Validator extends SEPConfig {

        {
            RowProcessor<WorldCityBeanPrimitive> city = csvMarshaller(WorldCityBeanPrimitive.class)
                    .build();
            addPublicNode(city, "city");
            addPublicNode(count(city), "count");
            maxFiltersInline = 25;

            addNode(new ValidationEvaluator(
                    and(
                            city,
                            nand(
                                    //buildTest(eq(20), city, WorldCityBeanPrimitive::getLatitude).build(),
                                    buildTest(lt(200), city, WorldCityBeanPrimitive::getLatitude).build(),
                                    buildTest(gt(2), city, WorldCityBeanPrimitive::getLatitude).build()
                            )
                    )
            ), "validationEvaluator");
        }

    }

    public static class Logger extends Named {

        private StringBuilder sb;

        public Logger(String name) {
            super(name);
        }
        
        public void log(String log){
            System.out.println("log:" + log);
        }
    }

    public static class ValidationEvaluator {

        private final Object failureNotifier;
        public int failureCount;

        public ValidationEvaluator(Object failureNotifier) {
            this.failureNotifier = failureNotifier;
        }

        @OnEvent
        public void failed() {
            failureCount++;
        }

    }

    public static class NumberCompareValidators {

        public final double limit;

        @Inject
        @Config(key = "name", value = "validationLog")
        public Logger logger;

        public static NumberCompareValidators limit(double limit) {
            return new NumberCompareValidators(limit);
        }

        public static LambdaReflection.SerializableConsumer<Double> gt(double limit) {
            return limit(limit)::greaterThan;
        }

        public static LambdaReflection.SerializableConsumer<Double> lt(double limit) {
            return limit(limit)::lessThan;
        }

        public static LambdaReflection.SerializableConsumer<Integer> eq(int limit) {
            return limit(limit)::equal;
        }

        public NumberCompareValidators(double limit) {
            this.limit = limit;
        }

        public boolean greaterThan(double x) {
            final boolean test = x > limit;
            if(!test){
                logger.log("failed " + x + ">" + limit);
            }
            return test;
        }

        public boolean lessThan(double x) {
            final boolean test = x < limit;
            if(!test){
                logger.log("failed " + x + "<" + limit);
            }
            return test;
        }

        public boolean equal(double x) {
            final boolean test = x == limit;
            if(!test){
                logger.log("failed " + x + "==" + limit);
            }
            return test;
        }

    }

}
