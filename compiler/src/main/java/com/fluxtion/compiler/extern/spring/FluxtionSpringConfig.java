package com.fluxtion.compiler.extern.spring;

import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FluxtionSpringConfig {
    private List<Auditor> auditors = new ArrayList<>();
    private EventLogControlEvent.LogLevel logLevel;
}