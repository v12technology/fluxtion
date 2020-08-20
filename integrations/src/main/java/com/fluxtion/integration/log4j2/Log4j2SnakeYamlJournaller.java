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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

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
    Logger appLog = LogManager.getLogger(Log4j2SnakeYamlJournaller.class);
    
    @Override
    public void processEvent(Object o) {
        record.setEvent(o);
        buffer.setLength(0);
        try {
            final String dump = yaml.dumpAs(record, Tag.MAP, null);
            log.info("---\n");
            log.info(dump);
        } catch (Exception e) {
            appLog.warn("cannot serialiase event:'{}'",o, e);
            yaml = new Yaml();
        }
//with top level tag        
//        yaml.dump(record, writer);
//        log.info(buffer);
        propagate(o);
    }

    @Override
    protected void initHandler() {
        writer = new StringWriter();
        buffer = writer.getBuffer();
        record = new JournalRecord();
        
//        DumperOptions opt = new DumperOptions().
        
//        writer
    }
    
    
    
}
