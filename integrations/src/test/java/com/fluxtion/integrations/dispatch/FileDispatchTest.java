/*
 * Copyright (c) 2020, V12 Technology Ltd.
 * All rights reserved.
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
package com.fluxtion.integrations.dispatch;

import com.fluxtion.ext.text.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import com.fluxtion.integration.dispatch.ConsoleFilter;
import com.fluxtion.integration.dispatch.EventFilter;
import com.fluxtion.integration.dispatch.Pipeline;
import com.fluxtion.integration.dispatch.RowProcessorFilter;
import com.fluxtion.integration.dispatch.SepFilter;
import com.fluxtion.integration.dispatch.SynchronizedFilter;
import lombok.Data;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FileDispatchTest extends BaseSepInprocessTest {

    @Test
    public void testFileRead() {

        Pipeline pipe = new Pipeline();
        EventFilter csvDecoder = RowProcessorFilter.of(DataEventCsvDecoder0.class);
        pipe.entry(csvDecoder).next(new ConsoleFilter());

        String input = DataEventCsvDecoder0.csvHeader() + "\n"
                + "1,tom\n"
                + "21,fred\n"
                + "346,dfgfgfgf\n";

        pipe.start();
        StringDriver.streamChars(input, csvDecoder::processEvent);
    }

    @Test
    public void testGatheringPipeline() {
        
        
        Pipeline pipe = new Pipeline();
        
        ForwardingSep forwardingSep = new ForwardingSep();
        EventFilter csvDecoder = RowProcessorFilter.of(DataEventCsvDecoder0.class);
        
        pipe.entry(csvDecoder)
                .merge(pipe.entry(SepFilter.of(forwardingSep)))
                .next(new ConsoleFilter());      
        
        pipe.start();        


        forwardingSep.onEvent("helloworld");
        String input = DataEventCsvDecoder0.csvHeader() + "\n"
                + "1,tom\n"
                + "21,fred\n"
                + "346,dfgfgfgf\n";
        StringDriver.streamChars(input, csvDecoder::processEvent);
    }

    @Data
    public static class DataEvent {

        private int id;
        private String name;
    }

}
