/*
 * Copyright (c) 2019, 2024 gregory higgins.
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
package com.fluxtion.compiler.generation.util;

import com.fluxtion.runtime.audit.LogRecord;
import com.fluxtion.runtime.audit.LogRecordListener;
import com.fluxtion.runtime.audit.StructuredLogRecord;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility for reading {@link LogRecord} yaml and converting to a
 * {@link StructuredLogRecord}
 *
 * @author 2024 gregory higgins.
 */
public class YamlLogRecordListener implements LogRecordListener {

    private final List<StructuredLogRecord> eventList = new ArrayList<>();
    private final Yaml yaml;

    public List<StructuredLogRecord> getEventList() {
        return eventList;
    }

    public YamlLogRecordListener() {
        yaml = YamlFactory.newYaml();;
    }

    public void loadFromFile(Reader reader) {
        yaml.loadAll(reader).forEach((Object m) -> {
            if (m != null) {
                Map e = (Map) ((Map) m).get("eventLogRecord");
                if (e != null) {
                    eventList.add(new StructuredLogRecord(e));
                }
            }
        });
    }

    @Override
    public void processLogRecord(LogRecord logRecord) {
        yaml.loadAll(logRecord.asCharSequence().toString()).forEach(m -> {
            Map e = (Map) ((Map) m).get("eventLogRecord");
            eventList.add(new StructuredLogRecord(e));
        });
    }

    @Override
    public String toString() {
        return "MarshallingLogRecordListener{" + "eventList=" + eventList + '}';
    }

}
