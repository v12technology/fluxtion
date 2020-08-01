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

import com.fluxtion.ext.text.builder.csv.CsvToBeanBuilder;
import com.fluxtion.ext.text.builder.util.StringDriver;
import com.fluxtion.generator.compiler.InprocessSepCompiler;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import com.fluxtion.integration.eventflow.filters.CharReader;
import com.fluxtion.integration.eventflow.filters.ConsoleFilter;
import com.fluxtion.integration.eventflow.PipelineFilter;
import com.fluxtion.integration.eventflow.Pipeline;
import com.fluxtion.integration.eventflow.filters.RowProcessorFilter;
import com.fluxtion.integration.eventflow.filters.SepEventPublisher;
import java.io.FileNotFoundException;
import java.io.FileReader;
import lombok.Data;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FileDispatchTest extends BaseSepInprocessTest {

    @Test
    public void testFileRead() throws FileNotFoundException {
        Pipeline.build(CharReader.of(new FileReader("src/test/data/data1.csv")))
                .next(RowProcessorFilter.of(DataEventCsvDecoder0.class))
                .next(new ConsoleFilter())
                .start()
                .stop();

    }

    @Test
    public void testGatheringPipeline() {
        Pipeline pipe = new Pipeline();
        ForwardingSep sampleSep = new ForwardingSep();
        PipelineFilter csvDecoder = RowProcessorFilter.of(DataEventCsvDecoder0.class);
        pipe.entry(csvDecoder)
                .merge(pipe.entry(SepEventPublisher.of(sampleSep)))
                .next(new ConsoleFilter());      
        pipe.start();        

        sampleSep.onEvent("helloworld");
        String input = DataEventCsvDecoder0.csvHeader() + "\n"
                + "1,tom\n"
                + "21,fred\n"
                + "346,dfgfgfgf\n";
        StringDriver.streamChars(input, csvDecoder::processEvent);
    }
    
    @Test
    @Ignore
    public void geneerateMarshaller(){
        CsvToBeanBuilder.nameSpace("com.fluxtion.integrations.dispatch")
                .dirOption(InprocessSepCompiler.DirOptions.TEST_DIR_OUTPUT)
                .builder(DataEvent.class, 1)
                .build();
    }

    @Data
    public static class DataEvent {

        private int id;
        private String name;
    }

}
