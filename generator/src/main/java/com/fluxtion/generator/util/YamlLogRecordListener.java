/* 
 * Copyright (c) 2019, V12 Technology Ltd.
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
package com.fluxtion.generator.util;

import com.fluxtion.api.audit.LogRecord;
import com.fluxtion.api.audit.LogRecordListener;
import com.fluxtion.api.audit.StructuredLogRecord;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Utility for reading {@link LogRecord} yaml and converting to a
 * {@link StructuredLogRecord}
 *
 * @author V12 Technology Ltd.
 */
public class YamlLogRecordListener implements LogRecordListener {

    private List<StructuredLogRecord> eventList = new ArrayList<>();
    private final Yaml yaml;

    public List<StructuredLogRecord> getEventList() {
        return eventList;
    }

    public YamlLogRecordListener() {
        yaml = new Yaml();
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
