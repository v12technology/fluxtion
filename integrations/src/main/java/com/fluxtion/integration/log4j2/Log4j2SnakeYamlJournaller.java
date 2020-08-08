/*
 * Copyright (C) Copyright (C) 2020 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.integration.log4j2;

import com.fluxtion.integration.eventflow.PipelineFilter;
import java.io.StringWriter;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author V12 Technology Ltd.
 */
@Log4j2(topic = "fluxtion.journaller.yaml")
public class Log4j2SnakeYamlJournaller extends PipelineFilter {

    Yaml yaml = new Yaml();
    StringBuffer buffer;
    StringWriter writer;
    JournalRecord record;
    
    @Override
    public void processEvent(Object o) {
        record.setEvent(o);
        buffer.setLength(0);
        yaml.dump(record, writer);
        yaml.dumpAsMap(record);
        log.info(buffer);
        log.info("---");
    }

    @Override
    protected void initHandler() {
        writer = new StringWriter();
        buffer = writer.getBuffer();
        record = new JournalRecord();
//        writer
    }
    
    
    
}
