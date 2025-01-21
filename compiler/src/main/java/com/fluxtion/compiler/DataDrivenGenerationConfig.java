/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.compiler;

import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

/**
 * Combines {@link RootNodeConfig} and {@link FluxtionCompilerConfig} into a single instance
 * so a complete configuration for a generation run can be recorded.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDrivenGenerationConfig {

    private String name;
    private String rootClass;
    private Map<String, Object> configMap;
    private List<Object> nodes;
    private boolean enableAudit;
    private boolean printEventToString = false;
    private LogLevel traceLogeLevel = LogLevel.NONE;
    private FluxtionCompilerConfig compilerConfig;// = new FluxtionCompilerConfig();

    @SneakyThrows
    public RootNodeConfig getRootNodeConfig() {
        Class<?> rootClass1 = rootClass == null ? null : Class.forName(rootClass, true, compilerConfig.getClassLoader());
        return new RootNodeConfig(name, rootClass1, configMap, nodes);
    }

    public EventProcessorConfig getEventProcessorConfig() {
        EventProcessorConfig eventProcessorConfig = new EventProcessorConfig();
        eventProcessorConfig.setRootNodeConfig(getRootNodeConfig());
        if (enableAudit) {
            eventProcessorConfig.addEventAudit();
        }
        if (traceLogeLevel != LogLevel.NONE) {
            eventProcessorConfig.addEventAudit(traceLogeLevel, printEventToString);
        }
        return eventProcessorConfig;
    }
}
