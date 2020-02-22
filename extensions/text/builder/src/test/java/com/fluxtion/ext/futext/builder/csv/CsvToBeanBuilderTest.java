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

import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
import com.fluxtion.api.event.Event;
import com.fluxtion.builder.generation.GenerationContext;
import com.fluxtion.ext.text.api.util.marshaller.DispatchingCsvMarshaller;
import com.fluxtion.ext.text.builder.util.StringDriver;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.DirOptions.TEST_DIR_OUTPUT;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.fluxtion.api.lifecycle.StaticEventProcessor;

/**
 *
 * @author gregp
 */
public class CsvToBeanBuilderTest {

    @Test
    public void defaultBeanMap() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        long currentTime = System.currentTimeMillis();
        WorldCityOptionalEvent[] worldCity = new WorldCityOptionalEvent[1];
        DispatchingCsvMarshaller dispatcher = CsvToBeanBuilder.nameSpace("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest2")
                .dirOption(TEST_DIR_OUTPUT)
                .mapBean("DefaultMappedBean", WorldCityOptionalEvent.class)
                .build((e) -> {
                    if (e instanceof WorldCityOptionalEvent) {
                        worldCity[0] = (WorldCityOptionalEvent) e;
                    }
                });
        String dataCsh = "WorldCityOptionalEvent,country,city,accent City,region,population,longitude,latitude\n"
                + "WorldCityOptionalEvent,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n";
        StringDriver.streamChars(dataCsh, dispatcher, false);
        assertTrue(worldCity[0].getEventTime() >= currentTime);

        DispatchingCsvMarshaller dispatcher2 = new DispatchingCsvMarshaller();
        dispatcher2.addMarshaller(WorldCity.class, (StaticEventProcessor) GenerationContext.SINGLETON.forName(
                "com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest2.fluxCsvDefaultMappedBean.Csv2DefaultMappedBean").newInstance());
        dispatcher2.addSink((e) -> {
            if (e instanceof WorldCityOptionalEvent) {
                worldCity[0] = (WorldCityOptionalEvent) e;
            }
        });

        dataCsh = "WorldCity,eventTime,country,city,accent City,region,population,longitude,latitude\n"
                + "WorldCity,200,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n";
        StringDriver.streamChars(dataCsh, dispatcher2, false);
        assertThat(worldCity[0].getEventTime(), is(200L));
    }

    @Test
    public void inlineCustomiseTest() throws Exception {
        boolean build = true;
        LongAdder count = new LongAdder();
        WorldCity[] worldCity = new WorldCity[1];

        DispatchingCsvMarshaller dispatcher = null;
        if (build) {
            //build
            MyFunctions functions = new MyFunctions();
            dispatcher = CsvToBeanBuilder.nameSpace("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest")
                    .dirOption(TEST_DIR_OUTPUT)
                    .mapCustomBean("WorldCity", WorldCity.class, (c) -> {
                        c
                                .map(0, WorldCity::setCountry)
                                .map(1, WorldCity::setCity)
                                .map(5, WorldCity::setLongitude)
                                .converter(5, CsvMarshallerBuilderTest::always_1)
                                .map(6, WorldCity::setLatitude)
                                .map(6, WorldCity::setLatitudeCharSequence)
                                .converter(6, functions::always_Zero)
                                .map("population", WorldCity::setPopulation)
                                .converter("population", functions::always_100)
                                .headerLines(1);
                    }).build((e) -> {
                if (e instanceof WorldCity) {
                    worldCity[0] = (WorldCity) e;
                    count.increment();
                }
            });
        } else {
            dispatcher = new DispatchingCsvMarshaller();
            dispatcher.addMarshaller(WorldCity.class, (StaticEventProcessor) Class.forName("com.fluxtion.ext.futext.builder.csv.csvToBeanBuilderTest.WorldCityCsvBean").newInstance());
            dispatcher.addSink((e) -> {
                if (e instanceof WorldCity) {
                    worldCity[0] = (WorldCity) e;
                    count.increment();
                }
            });
        }

        //dispatch
        String dataCsh = "WorldCity,country,city,accent city,region,population,longitude,latitude\n"
                + "WorldCity,mexico,aixirivali,Aixirivali,06,,25.19,1.5\n"
                + "WorldCity,brazil,santiago,Aixirivall,06,,130,1.5\n";
        StringDriver.streamChars(dataCsh, dispatcher, false);

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
