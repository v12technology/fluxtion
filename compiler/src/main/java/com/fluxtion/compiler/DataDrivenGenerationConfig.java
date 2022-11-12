package com.fluxtion.compiler;

import com.fluxtion.runtime.audit.EventLogControlEvent.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

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
    private boolean enableAudit;
    private LogLevel auditMethodTraceLogLevel = LogLevel.DEBUG;
    private FluxtionCompilerConfig compilerConfig;// = new FluxtionCompilerConfig();

    @SneakyThrows
    public RootNodeConfig getRootNodeConfig() {
        return new RootNodeConfig(name, Class.forName(rootClass, true, compilerConfig.getClassLoader()), configMap);
    }

    public EventProcessorConfig getEventProcessorConfig() {
        EventProcessorConfig eventProcessorConfig = new EventProcessorConfig();
        eventProcessorConfig.setRootNodeConfig(getRootNodeConfig());
        if (enableAudit)
            eventProcessorConfig.addEventAudit(auditMethodTraceLogLevel);
        return eventProcessorConfig;
    }
}
