package com.fluxtion.compiler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * Combines {@link RootNodeConfig} and {@link FluxtionCompilerConfig} into a single instance
 * so a complete configuration for a generation run can be recorded.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDrivenGenerationConfig {

    private String name;
    private String rootClass;
    private Map<String, Object> configMap;
    private FluxtionCompilerConfig compilerConfig;// = new FluxtionCompilerConfig();

    @SneakyThrows
    public RootNodeConfig getRootNodeConfig() {
        return new RootNodeConfig(name, Class.forName(rootClass,  true, compilerConfig.getClassLoader()), configMap);
    }

    public EventProcessorConfig getEventProcessorConfig(){
        EventProcessorConfig eventProcessorConfig = new EventProcessorConfig();
        eventProcessorConfig.setRootNodeConfig(getRootNodeConfig());
        return eventProcessorConfig;
    }

//    public static void main(String[] args) {
//        Yaml yaml = new Yaml();
//        Map<String, Object> configMap = new HashMap<>();
//        configMap.put("firstKey", 12);
//        configMap.put("anotherKey", "my value");
//        FluxtionCompilerConfig compilerConfig = new FluxtionCompilerConfig();
//        compilerConfig.setPackageName("mypackage.whatever");
//        DataDrivenGenerationConfig myRootConfig = new DataDrivenGenerationConfig("myRoot", MyRootClass.class.getCanonicalName(), configMap, compilerConfig);
//        System.out.println("dumpAsMap:\n" + yaml.dumpAsMap(myRootConfig));
//    }
//
//    public static final class MyRootClass{}

}
