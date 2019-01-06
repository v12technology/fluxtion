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

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.declarative.api.numeric.NumericValue;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.futext.api.csv.RulesEvaluator;
import static com.fluxtion.ext.futext.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.ext.futext.builder.csv.NumericValidatorBuilder.withinRange;
import static com.fluxtion.ext.futext.builder.csv.RulesEvaluatorBuilder.validator;
import static com.fluxtion.ext.futext.builder.math.CountFunction.count;
import org.junit.Assert;
import org.junit.Test;

public class ValidationTest extends BaseSepTest {

//    protected String testPackageID() {
//        return "";
//    }
    @Test
//    @Ignore
    public void testCsvWithHeaderAndRowCB() {
        compileCfg.setGenerateDescription(true);
        buildAndInitSep(CsvMarshallerBuilderTest.WorldCitiesCsv_Header_OnEventCB.class);
        String dataCsh = "country,city,accent city,region,population,longitude,latitude\n"
                + "mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "brazil,santiago,Aixirivall,06,,130,1.5\n";
        WorldCityOnEvent city = ((Wrapper<WorldCityOnEvent>) getField("city")).event();
        StringDriver.streamChars(dataCsh, sep, false);
        Assert.assertEquals(2, city.parse);
        Assert.assertEquals(2, city.postProcess);
    }

    @Test
//    @Ignore
    public void testCsvWithHeaderAndError() {
        final EventHandler sep = buildAndInitSep(CsvMarshallerBuilderTest.WorldCitiesCsv_Header_1_Cfg.class);
        String dataCsh = "country,city,accent city,region,population,longitude,latitude\n"
                + "mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "brazil,santiago,Aixirivall,06,,16*90,1.5\n";
        StringDriver.streamChars(dataCsh, sep, false);
    }

    @Test
//    @Ignore
    //A RulesEvaluator is attached to the RowProcessor. Any read exceptions
    //are caught and the failed notifer path executed for the Evaluator
    public void testCsvWithHeaderAndRowFailedRead() {
//        compileCfg.setGenerateDescription(false);
//        final EventHandler sep = new TestSep_testCsvWithHeaderAndRowCBFailedValidation();
        final EventHandler sep = buildAndInitSep(WorldCitiesCsvWithFailNotifier.class);
        NumericValue countPassed = getField("countPassed");
        NumericValue countFailed = getField("countFailed");
        String dataCsh = "Country,City,AccentCity,Region,Population,Latitude,Longitude\n"
                + "mexico,aixirivali,Aixirivali,06,12,25.19,1.5\n"
                + "mexico,aixirivali,Aixirivali,06,500,1.2,1.5\n"
                + "mexico,aixirivali,Aixirivali,06,200,25.19,1.5\n"
                + "mexico,aixirivali,Aixirivali,XXX06,656,25ifg19,1.5\n"
                + "brazil,santiago,Aixirivall,06,655,330,1.5";
        StringDriver.streamChars(dataCsh, sep, false);
        Assert.assertEquals(4, countPassed.intValue());
        Assert.assertEquals(1, countFailed.intValue());
    }

    @Test
    public void testValueValidation() {
//        compileCfg.setGenerateDescription(true);
//        compileCfg.setFormatSource(true);
        final EventHandler sep = buildAndInitSep(WorldCityBeanValidating.class);
        NumericValue countPassed = getField("countPassed");
        NumericValue countFailed = getField("countFailed");
        String dataCsh = "Country,City,AccentCity,Region,Population,Latitude,Longitude\n"
                + "mexico,aixirivali,Aixirivali,06,12,25.19,1.5\n"//pass
                + "mexico,aixirivali,Aixirivali,06,500,1.2,181\n"//fail
                + "mexico,aixirivali,Aixirivali,06,200,25.19,1.5\n"//pass
                + "mexico,aixirivali,Aixirivali,06,65600,23,1.5\n"//fail
                + "mexico,aixirivali,Aixirivali,06,500,-25,1.5\n"//pass
                + "mexico,aixirivali,Aixirivali,06,500,-250,1.5\n"//fail
                + "mexico,aixirivali,Aixirivali,06,-500,-250,2500\n"//fail
                + "brazil,santiago,Aixirivall,06,655,330,1.5";//fail
        StringDriver.streamChars(dataCsh, sep, false);
        Assert.assertEquals(3, countPassed.intValue());
        Assert.assertEquals(5, countFailed.intValue());
    }

    public static class WorldCitiesCsvWithFailNotifier extends SEPConfig {

        {
            RulesEvaluator<WorldCityBeanPrimitive> validator = validator(
                    csvMarshaller(WorldCityBeanPrimitive.class).build()
            ).build();
            addPublicNode(count(validator.passedNotifier()), "countPassed");
            addPublicNode(count(validator.failedNotifier()), "countFailed");
            maxFiltersInline = 25;

        }
    }

    public static class WorldCityBeanValidating extends SEPConfig {

        {
            RulesEvaluator<WorldCityBeanPrimitive> validator = validator(
                    csvMarshaller(WorldCityBeanPrimitive.class).build()
            )
                    .addRule(withinRange(0, 2500), WorldCityBeanPrimitive::getPopulation)
                    .addRule(withinRange(-90, 90), WorldCityBeanPrimitive::getLatitude)
                    .addRule(withinRange(-180, 180), WorldCityBeanPrimitive::getLongitude)
                    .build();
            addPublicNode(count(validator.passedNotifier()), "countPassed");
            addPublicNode(count(validator.failedNotifier()), "countFailed");
        }
    }

}
