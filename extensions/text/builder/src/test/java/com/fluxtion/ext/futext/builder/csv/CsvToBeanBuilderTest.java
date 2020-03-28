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

import com.fluxtion.ext.text.api.util.CsvRecordStream;
import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.DirOptions.TEST_DIR_OUTPUT;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author gregp
 */
public class CsvToBeanBuilderTest {

    @Test
    public void mapBeanAndStream() {
        long currentTime = System.currentTimeMillis();
        List cities = new ArrayList<>();
        CsvRecordStream dispatcher = CsvToBeanBuilder.nameSpace("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest3")
                .dirOption(TEST_DIR_OUTPUT)
                .mapBean("DefaultMappedBean", WorldCityOptionalEvent.class)
                .build();
        dispatcher.sinks(cities::add)
                .stream(
                        "WorldCityOptionalEvent,country,city,accent City,region,population,longitude,latitude\n"
                        + "WorldCityOptionalEvent,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                );
        assertTrue(((WorldCityOptionalEvent) cities.get(0)).getEventTime() >= currentTime);
        dispatcher.stream("WorldCityOptionalEvent,UK,LONDON,Aixirivali,06,,25.19,1.5\n");
        assertThat(((WorldCityOptionalEvent) cities.get(1)).getCity().toString(), is("LONDON"));
    }

    @Test
    public void mapBeanAndStreamEventTIme() {
        long currentTime = System.currentTimeMillis();
        List cities = new ArrayList<>();
        CsvRecordStream dispatcher = CsvToBeanBuilder.nameSpace("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest4")
                .dirOption(TEST_DIR_OUTPUT)
                .mapBean("DefaultMappedBean", WorldCityOptionalEvent.class)
                .build();
        dispatcher.sinks(cities::add)
                .stream(
                        "WorldCityOptionalEvent,eventTime,country,city,accent City,region,population,longitude,latitude\n"
                        + "WorldCityOptionalEvent,-1,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                );

        assertTrue(((WorldCityOptionalEvent) cities.get(0)).getEventTime() >= currentTime);
        dispatcher.stream("WorldCityOptionalEvent,400,UK,LONDON,Aixirivali,06,,25.19,1.5\n");
        assertTrue(((WorldCityOptionalEvent) cities.get(1)).getEventTime() == 400);
    }

    @Test
    public void inlineCustomiseTest() throws Exception {
        LongAdder count = new LongAdder();
        WorldCity[] worldCity = new WorldCity[1];
        MyFunctions functions = new MyFunctions();
        CsvToBeanBuilder.nameSpace("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest")
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
                }).build().sinks((e) -> {
            if (e instanceof WorldCity) {
                worldCity[0] = (WorldCity) e;
                count.increment();
            }
        }).stream("WorldCity,country,city,accent city,region,population,longitude,latitude\n"
                + "WorldCity,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "WorldCity,brazil,santiago,Aixirivall,06,,130,1.5\n");

        WorldCity city = worldCity[0];
        assertEquals(2, count.intValue());
        assertThat(city.getCountry(), is("brazil"));
        assertThat(city.getCity().toString(), is("santiago"));
        assertEquals(1.0, city.getLongitude(), 0.1);
        assertEquals(0.0, city.getLatitude(), 0.1);
        assertThat("1.5", is(city.getLatitudeCharSequence().toString()));
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
