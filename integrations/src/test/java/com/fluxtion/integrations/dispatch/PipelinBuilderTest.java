package com.fluxtion.integrations.dispatch;

import com.fluxtion.integration.eventflow.filters.ConsoleFilter;
import com.fluxtion.integration.eventflow.PipelineFilter;
import com.fluxtion.integration.eventflow.Pipeline;
import com.fluxtion.integration.eventflow.filters.RowProcessorFilter;
import com.fluxtion.integration.eventflow.filters.SepEventPublisher;
import org.junit.jupiter.api.Test;

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

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class PipelinBuilderTest {
    
    @Test
    public void testBuild(){
        Pipeline pipeline = new Pipeline();
        pipeline
                .entry(RowProcessorFilter.of(DataEventCsvDecoder0.class))
                .next(SepEventPublisher.of(new ForwardingSep()))
                .next(new ConsoleFilter())
                ;
        pipeline.start();
        pipeline.stop();
                
    }
}
