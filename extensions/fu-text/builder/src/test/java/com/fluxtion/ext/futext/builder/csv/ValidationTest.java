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
import static com.fluxtion.ext.futext.builder.csv.RulesEvaluatorBuilder.validator;
import static com.fluxtion.ext.futext.builder.math.CountFunction.count;
import org.junit.Assert;
import org.junit.Ignore;
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
    public void testCsvWithHeaderAndRowFailedValidation() {
        compileCfg.setGenerateDescription(true);
//        final EventHandler sep = new TestSep_testCsvWithHeaderAndRowCBFailedValidation();
        final EventHandler sep = buildAndInitSep(WorldCitiesCsv_Header_OnEventCB_Validator.class);
        NumericValue countPassed = getField("countPassed");
        NumericValue countFailed = getField("countFailed");
        String dataCsh = "Country,City,AccentCity,Region,Population,Latitude,Longitude\n"
                + "mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "mexico,aixirivali,Aixirivali,06,,1.2,1.5\n"
                + "mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "mexico,aixirivali,Aixirivali,XXX06,,25ifg19,1.5\n"
                + "brazil,santiago,Aixirivall,06,,330,1.5";
        StringDriver.streamChars(dataCsh, sep, false);
        Assert.assertEquals(4, countPassed.intValue());
        Assert.assertEquals(1, countFailed.intValue());
    }

    public static class WorldCitiesCsv_Header_OnEventCB_Validator extends SEPConfig {

        {
            RulesEvaluator<WorldCityBeanPrimitive> validator = validator(
                    csvMarshaller(WorldCityBeanPrimitive.class).build()
            ).build();
            addPublicNode(count(validator.passedNotifier()), "countPassed");
            addPublicNode(count(validator.failedNotifier()), "countFailed");
            maxFiltersInline = 25;

        }
    }

}
