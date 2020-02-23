/*
 * Copyright (C) 2019 V12 Technology Ltd.
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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.text.api.util.StringDriver;
import com.fluxtion.ext.text.api.util.marshaller.DispatchingCsvMarshaller;
import static com.fluxtion.ext.text.builder.csv.CsvMarshallerBuilder.csvMarshaller;
import static com.fluxtion.ext.text.builder.csv.NumericValidatorBuilder.gt;
import static com.fluxtion.ext.text.builder.csv.NumericValidatorBuilder.lt;
import static com.fluxtion.ext.text.builder.csv.NumericValidatorBuilder.positive;
import static com.fluxtion.ext.text.builder.csv.NumericValidatorBuilder.withinRange;
import com.fluxtion.ext.text.builder.csv.RulesEvaluatorBuilder;
import com.fluxtion.generator.util.BaseSepTest;
import java.util.concurrent.atomic.LongAdder;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author V12 Technology Ltd.
 */
public class MultiTest extends BaseSepTest {

    @Test
    public void multiTest() {
        StaticEventProcessor customerParser = buildAndInitSep(CsvCustomerData.class);
        beforeTest();
        StaticEventProcessor trackParser = buildAndInitSep(CsvTrackPlay.class);
        //
        DispatchingCsvMarshaller dispatcher = new DispatchingCsvMarshaller();
        dispatcher.init();
        //customer
        dispatcher.addMarshaller(CustomerData.class, customerParser);
        //track
        dispatcher.addMarshaller(TrackPlay.class, trackParser);
        //output
        LongAdder adder = new LongAdder();
        dispatcher.addSink((e) -> {
            if (e instanceof CustomerData) {
                adder.increment();
            }
        });

        StringDriver.streamChars(
                "CustomerData,name\n"
                + "TrackPlay,trackId,playCount\n"
                + "TrackPlay,fgty-678,900\n"
                + "TrackPlay,ggg,100000000\n"
                + "TrackPlay,ddd,-10\n"
                + "CustomerData,fred\n"
                + "CustomerData,george\n",
                dispatcher, false);

        Assert.assertThat(adder.intValue(), is(2));
    }

    public static class CsvCustomerData extends SEPConfig {

        @Override
        public void buildConfig() {
            csvMarshaller(CustomerData.class).addEventPublisher().build();
        }

    }

    public static class CsvTrackPlay extends SEPConfig {

        @Override
        public void buildConfig() {
            RulesEvaluatorBuilder.validator(
                    csvMarshaller(TrackPlay.class).build())
                    .addRule(lt(200), TrackPlay::getPlayCount)
                    .addRule(gt(20), TrackPlay::getPlayCount)
                    .addRule(positive(), TrackPlay::getPlayCount)
                    .addRule(withinRange(200.0, 700.9), TrackPlay::getPlayCount)
                    .build();

        }

    }
}
