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

import com.fluxtion.compiler.builder.generation.GenerationContext;
import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.generation.Generator;
import com.fluxtion.compiler.generation.compiler.OutputRegistry;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.JULLogRecordListener;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.runtime.stream.EventStream;
import lombok.SneakyThrows;
import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import static com.fluxtion.compiler.generation.compiler.InProcessSepCompiler.sepTestInstance;
import static com.fluxtion.runtime.time.ClockStrategy.registerClockEvent;

/**
 * Test class utility for building a SEP in process
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@RunWith(Parameterized.class)
@Ignore
public class MultipleSepTargetInProcessTest {

    protected StaticEventProcessor sep;
    protected boolean fixedPkg = true;
    protected boolean reuseSep = false;
    protected boolean generateLogging = false;
    private boolean addAuditor = false;
    protected TestMutableNumber time;
    protected boolean timeAdded = false;
    //parametrized test config
    protected final boolean compiledSep;
    private InMemoryEventProcessor inMemorySep;
    protected SimpleEventProcessorModel simpleEventProcessorModel;

    public MultipleSepTargetInProcessTest(boolean compiledSep) {
        this.compiledSep = compiledSep;
    }
    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(false, true);
    }

    @Rule
    public TestName testName = new TestName();

    @Before
    public void beforeTest() {
        fixedPkg = true;
        addAuditor = false;
    }

    @After
    public void afterTest(){
        tearDown();
    }

    @SuppressWarnings("unchecked")
    protected <T extends StaticEventProcessor> T sep(Class<T> handlerClass) {
        GenerationContext.setupStaticContext(pckName(), sepClassName(),
            new File(OutputRegistry.JAVA_TESTGEN_DIR),
            new File(OutputRegistry.RESOURCE_TEST_DIR));
        try {
            sep = handlerClass.getDeclaredConstructor().newInstance();
            if (sep instanceof Lifecycle) {
                ((Lifecycle) sep).init();
            }
            return (T) sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected StaticEventProcessor sep(Consumer<SEPConfig> cfgBuilder) {
        Consumer<SEPConfig> wrappedBuilder = cfgBuilder;
        if(addAuditor){
            wrappedBuilder = cfg ->{
                cfgBuilder.accept(cfg);
                cfg.addEventAudit(EventLogControlEvent.LogLevel.INFO);
            };
        }

        try {
            if(!compiledSep){
                GenerationContext.setupStaticContext(pckName(), sepClassName(),
                        new File(OutputRegistry.JAVA_TESTGEN_DIR),
                        new File(OutputRegistry.RESOURCE_TEST_DIR));
                SEPConfig cfg = new SEPConfig();
                cfg.setSupportDirtyFiltering(true);
                wrappedBuilder.accept(cfg);
                Generator generator = new Generator();
                inMemorySep = generator.inMemoryProcessor(cfg);
                inMemorySep.init();
                sep = inMemorySep;
                simpleEventProcessorModel = generator.getSimpleEventProcessorModel();
            }
            else {
                if (reuseSep) {
                    sep(wrappedBuilder, fqn());
                } else {
                    sep = sepTestInstance(wrappedBuilder, pckName(), sepClassName());
                }
            }
            return sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Lazily generates a SEP using the supplied String as the generated fully qualified class name. If a SEP cannot be
     * loaded then a new SEP is generated and initialised, using the supplied builder.
     *
     * @param <T> The subclass of the generated StaticEventProcessor
     * @param cfgBuilder The user supplied builder that adds nodes to the generation context
     * @param handlerClass The fqn of the SEP that will be generated if it cannot be loaded
     * @return The SEP that the user can interact with in the test
     */
    @SuppressWarnings("unchecked")
    protected <T extends StaticEventProcessor> T sep(Consumer<SEPConfig> cfgBuilder, String handlerClass) {
        try {
            try {
                GenerationContext.setupStaticContext("", "",
                    new File(OutputRegistry.JAVA_TESTGEN_DIR),
                    new File(OutputRegistry.RESOURCE_TEST_DIR));
                sep = (StaticEventProcessor) Class.forName(handlerClass).getDeclaredConstructor().newInstance();
                if (sep instanceof Lifecycle) {
                    ((Lifecycle) sep).init();
                }
                return (T) sep;
            } catch (Exception e) {
                String pckName = org.apache.commons.lang3.ClassUtils.getPackageName(handlerClass);
                String className = org.apache.commons.lang3.ClassUtils.getShortCanonicalName(handlerClass);
                GenerationContext.setupStaticContext(pckName, className,
                    new File(OutputRegistry.JAVA_TESTGEN_DIR),
                    new File(OutputRegistry.RESOURCE_TEST_DIR));
                sep = sepTestInstance(cfgBuilder, pckName, className);
                return (T) sep;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected StaticEventProcessor init() {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
        return sep;
    }

    protected String pckName() {
        String pckg = this.getClass().getCanonicalName() + "_" + testName.getMethodName().replaceAll("\\[([0-9]*?)]", "");
        pckg = pckg.toLowerCase();
        if (!fixedPkg) {
            pckg += "_" + System.currentTimeMillis();
        }
        return pckg;
    }

    protected String sepClassName() {
        return "TestSep_" + testName.getMethodName().replaceAll("\\[([0-9]*?)]", "");
    }

    protected String fqn(){
        return pckName() +"." + sepClassName();
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String name) {
        if(compiledSep){
            return (T) new Mirror().on(sep).get().field(name);
        }
        return (T) inMemorySep.getFieldByName(name).instance;
    }

    protected <T> T getStreamed(String name){
        EventStream<T> stream = getField(name);
        return stream.get();
    }

    protected void onEvent(Object e) {
        sep.onEvent(e);
    }

    protected void onGenericEvent(Object e) {
        onEvent(e);
    }

    protected StaticEventProcessor batchPause() {
        if (sep instanceof BatchHandler) {
            BatchHandler batchHandler = (BatchHandler) sep;
            batchHandler.batchPause();
        }
        return sep;
    }

    protected StaticEventProcessor batchEnd() {
        if (sep instanceof BatchHandler) {
            BatchHandler batchHandler = (BatchHandler) sep;
            batchHandler.batchEnd();
        }
        return sep;
    }

    protected StaticEventProcessor tearDown() {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).tearDown();
        }
        return sep;
    }
    
    /**
     * Sets the time in the clock. Does not fire an event into the SEP under test
     * @param newTime time in UTC
     * @return current {@link StaticEventProcessor}
     */
    protected StaticEventProcessor setTime(long newTime) {
        addClock();
        time.set(newTime);
        return sep;
    }

    /**
     * Advances the time in the clock by delta. Does not fire an event into the SEP under test
     * @param delta advance time by delta milliseconds
     * @return current {@link StaticEventProcessor}
     */
    protected StaticEventProcessor advanceTime(long delta) {
        addClock();
        time.set(time.longValue + delta);
        return sep;
    }

    protected void tick() {
        onEvent(new Object());
    }

    /**
     * Sets the time and pushes an event to the SEP to force any nodes that depend on time to be updated
     * @param newTime time in UTC
     */
    protected void tick(long newTime) {
        setTime(newTime);
        tick();
    }

    /**
     * Advances the time by delta and pushes an event to the SEP to force any nodes that depend on time to update
     * @param deltaTime advance time by delta milliseconds
     */
    protected void tickDelta(long deltaTime) {
        advanceTime(deltaTime);
        tick();
    }

    public void addClock() {
        if (!timeAdded) {
            time = new TestMutableNumber();
            time.set(0);
            onEvent(registerClockEvent(time::longValue));
        }
        timeAdded = true;
    }

    public void addAuditor(){
        addAuditor = true;
    }

    @SneakyThrows
    protected void auditToFile(String fileNamePrefix) {
        fileNamePrefix = fileNamePrefix + (compiledSep?"-compiled.yaml":"-interpreted.yaml");
        File file = new File("target" + File.separator + "generated-test-sources" + File.separator + "fluxtion-log" + File.separator + fileNamePrefix);
        FileUtils.forceMkdirParent(file);
        onEvent( new EventLogControlEvent(new JULLogRecordListener(file)) );
    }

}
