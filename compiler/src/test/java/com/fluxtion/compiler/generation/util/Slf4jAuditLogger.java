package com.fluxtion.compiler.generation.util;

import com.fluxtion.runtime.audit.LogRecord;
import com.fluxtion.runtime.audit.LogRecordListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Slf4jAuditLogger implements LogRecordListener {
    @Override
    public void processLogRecord(LogRecord logRecord) {
        log.info("\n" + logRecord.toString() + "\n---");
    }
}
