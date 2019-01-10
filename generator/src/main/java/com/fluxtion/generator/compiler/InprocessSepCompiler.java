/*
 * Copyright (C) 2019 V12 Technology Ltd.
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
package com.fluxtion.generator.compiler;

import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.builder.generation.GenerationContext;
import static com.fluxtion.builder.generation.GenerationContext.SINGLETON;
import com.fluxtion.builder.node.SEPConfig;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Generates and compiles a SEP for use by a caller in the same process.
 *
 * @author V12 Technology Ltd.
 */
public class InprocessSepCompiler {
    
    public static final String JAVA_TESTGEN_DIR = "target/generated-test-sources/java/";
    public static final String JAVA_GEN_DIR = "target/generated-sources/java/";
    public static final String JAVA_SRC_DIR = "src/main/java/";
    
    public static final String RESOURCE_TEST_DIR = "target/generated-test-sources/resources/";
    public static final String RESOURCE_DIR = "src/main/resources/";

    public static Class<EventHandler> buildSep(Consumer<SEPConfig> cfgBuilder, String pckg, String sepName) throws IOException, InstantiationException, IllegalAccessException, Exception {
        return buildSep(cfgBuilder, pckg, sepName, JAVA_GEN_DIR, RESOURCE_DIR);
    }
    
    public static Class<EventHandler> buildTestSep(Consumer<SEPConfig> cfgBuilder, String pckg, String sepName) throws IOException, InstantiationException, IllegalAccessException, Exception {
        return buildSep(cfgBuilder, pckg, sepName, JAVA_TESTGEN_DIR, RESOURCE_TEST_DIR);
    }
    
    public static Class<EventHandler> buildSep(Consumer<SEPConfig> cfgBuilder, String pckg, String sepName,  String srcGenDir, String resGenDir) throws IOException, InstantiationException, IllegalAccessException, Exception {
        SepCompiler compiler = new SepCompiler();
        final SepCompilerConfig compilerCfg = getSepCompileConfig(pckg, sepName, srcGenDir, resGenDir);
        compiler.compile(compilerCfg, new InProcessSepConfig(cfgBuilder));
        return (Class<EventHandler>) Class.forName(compilerCfg.getFqn());
    }

    private static class InProcessSepConfig extends SEPConfig {

        private final Consumer<SEPConfig> cfg;

        public InProcessSepConfig(Consumer<SEPConfig> cfg) {
            this.cfg = cfg;
        }

        @Override
        public void buildConfig() {
            System.out.println("sending cfg");
            cfg.accept(this);
            System.out.println("receiving cfg");
        }

    }

    public static SepCompilerConfig getSepCompileConfig(String packageName, String className, String srcGenDir, String resGenDir) throws IOException {
        File outputDir = new File(srcGenDir);
        File resourcesDir = new File(resGenDir);
        GenerationContext.setupStaticContext(packageName, className, outputDir, resourcesDir);
        SepCompilerConfig cfg = new SepCompilerConfig();
        cfg.setOutputDirectory(SINGLETON.getSourceRootDirectory().getCanonicalPath());
        cfg.setResourcesOutputDirectory(SINGLETON.getResourcesOutputDirectory().getCanonicalPath());
        cfg.setPackageName(packageName);
        cfg.setClassName(className);
        cfg.setCachedCompiler(SINGLETON.getJavaCompiler());
        cfg.setConfigClass(InProcessSepConfig.class.getCanonicalName());
        return cfg;
    }
}
