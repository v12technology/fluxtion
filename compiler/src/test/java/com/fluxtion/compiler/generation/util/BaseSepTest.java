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
package com.fluxtion.compiler.generation.util;

import com.fluxtion.compiler.generation.targets.JavaTestGeneratorHelper;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.compiler.builder.factory.NodeFactoryRegistration;
import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.generation.compiler.SepCompilerConfig;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * A base class a test can extend to aid SEP generation testing. A test class
 * can call buildAndInitSep with a SEP class, and the eventhandler will be
 * generated and compiled.
 *
 * @author Greg Higgins
 */
public class BaseSepTest {

//    protected static long count = System.currentTimeMillis();
    protected SepCompilerConfig compileCfg;
    protected StaticEventProcessor sep;
    @Rule
    public TestName testName = new TestName();
    protected String pckg;
    protected String className;

    @Before
    public void beforeTest() {
        pckg = this.getClass().getCanonicalName() + "_" + testName.getMethodName() + "_" + testPackageID();
        pckg = pckg.toLowerCase();
        className = sepClassName();
        JavaTestGeneratorHelper.setupDefaultTestContext(
                pckg, className);
        compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(pckg, className);
        cleanOuputDirectory(compileCfg);
        compileCfg.setSupportDirtyFiltering(true);
        compileCfg.setGenerateDescription(false);
    }

    protected String sepClassName() {
        return "TestSep_" + testName.getMethodName();
    }
    
    protected String testPackageID(){
        return "" + System.currentTimeMillis();
    }

    protected StaticEventProcessor buildAndInitSep(Class<? extends SEPConfig> builderClass) {
        try {
            compileCfg.setConfigClass(builderClass.getName());
            sep = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
            ((Lifecycle) sep).init();
            //System.out.println("generated SEP:" + compileCfg.getFqn());
            return sep;
        } catch (Exception ex) {
            throw new RuntimeException("could not build SEP:" + ex.getMessage(), ex);
        }
    }

    @SafeVarargs
    public static NodeFactoryRegistration factorySet(Class<? extends NodeFactory<?>>... classes) {
        return new NodeFactoryRegistration(new HashSet<>(Arrays.asList(classes)), null);
    }

    protected <T> T getField(String name) {
        return (T) new Mirror().on(sep).get().field(name);
    }

    protected void onEvent(Event e) {
        sep.onEvent(e);
    }

    public static void cleanOuputDirectory(SepCompilerConfig compilerConfig) {
        File file = new File(compilerConfig.getOutputDirectory(), compilerConfig.getPackageName().replace(".", "/"));
        try {
            if (file.exists()) {
                FileUtils.cleanDirectory(file);
            }
        } catch (IOException ex) {
            System.out.println("WARNING: could not delete source output directory:" + file);
        }
    }

}
