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

import com.fluxtion.api.event.Event;
import com.fluxtion.ext.futext.api.util.marshaller.DispatchingCsvMarshaller;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.DirOptions.TEST_DIR_OUTPUT;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class CsvToBeanBuilderTest {
    
    @Test
    public void inlineCustomiseTest() {
        LongAdder count = new LongAdder();
        WorldCity[] worldCity = new WorldCity[1];
        //build
        MyFunctions functions = new MyFunctions();
        DispatchingCsvMarshaller dispatcher = CsvToBeanBuilder.nameSpace("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest")
                .dirOption(TEST_DIR_OUTPUT)
                .mapCustomBean("WorldCity", WorldCity.class, (c) -> {
                    c.map(0, WorldCity::setCountry)
                            .map(1, WorldCity::setCity)
                            .map(5, WorldCity::setLongitude)
                            .converter(5, CsvMarshallerBuilderTest::always_1)
                            .map(6, WorldCity::setLatitude)
                            .map(6, WorldCity::setLatitudeCharSequence)
                            .converter(6, functions::always_Zero)
                            .map("population", WorldCity::setPopulation)
                            .converter("population", functions::always_100)
                            .headerLines(1);
                }).build((Event e) -> {
            if (e instanceof WorldCity) {
                worldCity[0] = (WorldCity) e;
                count.increment();
            }
        });
        //dispatch
        String dataCsh = "WorldCity,country,city,accent city,region,population,longitude,latitude\n"
                + "WorldCity,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "WorldCity,brazil,santiago,Aixirivall,06,,130,1.5\n";
        StringDriver.streamChars(dataCsh, dispatcher, false);
        
        WorldCity city = worldCity[0];

        Assert.assertEquals(2, count.intValue());
        Assert.assertThat("brazil", is(city.getCountry()));
        Assert.assertThat("santiago", is(city.getCity().toString()));
        Assert.assertEquals(1.0, city.getLongitude(), 0.1);
        Assert.assertEquals(0.0, city.getLatitude(), 0.1);
        Assert.assertThat("1.5", is(city.getLatitudeCharSequence().toString()));
    }
    
    public static class MyFunctions {
        
        public double always_Zero(CharSequence cs) {
            return 0.0;
        }
        
        public int always_100(CharSequence cs) {
            return 100;
        }
        
    }
    
}
