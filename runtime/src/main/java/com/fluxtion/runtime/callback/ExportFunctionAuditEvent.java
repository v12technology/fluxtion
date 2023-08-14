package com.fluxtion.runtime.callback;

import com.fluxtion.runtime.event.Event;

public class ExportFunctionAuditEvent implements Event {
    private String functionDescription;


    public ExportFunctionAuditEvent setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
        return this;
    }

    @Override
    public String toString() {
        return functionDescription;
    }
}
