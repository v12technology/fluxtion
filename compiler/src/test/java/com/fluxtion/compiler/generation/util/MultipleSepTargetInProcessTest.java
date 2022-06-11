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

import com.fluxtion.compiler.SEPConfig;
import com.fluxtion.compiler.builder.factory.RootInjectedNode;
import com.fluxtion.compiler.builder.generation.GenerationContext;
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
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

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
    protected boolean generateMetaInformation = false;
    protected boolean writeSourceFile = false;
    protected boolean fixedPkg = true;
    protected boolean reuseSep = false;
    protected boolean generateLogging = false;
    private boolean addAuditor = false;
    protected TestMutableNumber time;
    protected boolean timeAdded = false;
    //parametrized test config
    protected final boolean compiledSep;

    protected boolean inlineCompiled = false;
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
    public void afterTest() {
        tearDown();
    }

    protected StaticEventProcessor sep(RootInjectedNode rootNode) {
        return sep((SEPConfig cfg) -> cfg.setRootInjectedNode(rootNode));
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
        if (addAuditor || inlineCompiled) {
            wrappedBuilder = cfg -> {
                cfgBuilder.accept(cfg);
                if (addAuditor)
                    cfg.addEventAudit(EventLogControlEvent.LogLevel.INFO);
                cfg.setInlineEventHandling(inlineCompiled);
            };
        }

        try {
            if (!compiledSep) {
                GenerationContext.setupStaticContext(pckName(), sepClassName(),
                        new File(OutputRegistry.JAVA_TESTGEN_DIR),
                        new File(OutputRegistry.RESOURCE_TEST_DIR));
                SEPConfig cfg = new SEPConfig();
                cfg.setSupportDirtyFiltering(true);
                wrappedBuilder.accept(cfg);
                Generator generator = new Generator();
                inMemorySep = generator.inMemoryProcessor(cfg, generateMetaInformation);
                inMemorySep.init();
                sep = inMemorySep;
                simpleEventProcessorModel = generator.getSimpleEventProcessorModel();
            } else {
                if (reuseSep) {
//                    sep(wrappedBuilder, fqn());
                    try {
                        GenerationContext.setupStaticContext("", "",
                                new File(OutputRegistry.JAVA_TESTGEN_DIR),
                                new File(OutputRegistry.RESOURCE_TEST_DIR));
                        sep = (StaticEventProcessor) Class.forName(fqn()).getDeclaredConstructor().newInstance();
                        if (sep instanceof Lifecycle) {
                            ((Lifecycle) sep).init();
                        }
                    } catch (Exception e) {  }
                }
                if(sep == null) {
                    sep = sepTestInstance(wrappedBuilder, pckName(), sepClassName(), writeSourceFile, generateMetaInformation);
                }
            }
            return sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void writeOutputsToFile(boolean write){
        generateMetaInformation = write;
        writeSourceFile = write;
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
        return "TestSep_" + testName.getMethodName().replaceAll("\\[([0-9]*?)]", "") + (inlineCompiled?"Inline":"");
    }

    protected String fqn() {
        return pckName() + "." + sepClassName();
    }

    @SuppressWarnings("unchecked")
    protected <T> T getField(String name) {
        if (compiledSep) {
            return (T) new Mirror().on(sep).get().field(name);
        }
        return (T) inMemorySep.getFieldByName(name).instance;
    }

    protected <T> T getStreamed(String name) {
        EventStream<T> stream = getField(name);
        return stream.get();
    }

    protected void onEvent(Object e) {
        sep.onEvent(e);
    }

    protected void onEvent(byte value){
        onEvent((Byte)value);
    }

    protected void onEvent(char value){
        onEvent((Character)value);
    }

    protected void onEvent(short value){
        onEvent((Short)value);
    }

    protected void onEvent(int value){
        onEvent((Integer)value);
    }

    protected void onEvent(float value){
        onEvent((Float)value);
    }

    protected void onEvent(double value){
        onEvent((Double)value);
    }

    protected void onEvent(long value){
        onEvent((Long)value);
    }

    protected void onGenericEvent(Object e) {
        onEvent(e);
    }

    protected <T> void addSink(String id, Consumer<T> sink) {
        sep.addSink(id, sink);
    }

    protected void addIntSink(String id, IntConsumer sink){
        sep.addSink(id, sink);
    }

    protected void addDoubleSink(String id, DoubleConsumer sink) {
        sep.addSink(id, sink);
    }

    protected void addLongSink(String id, LongConsumer sink) {
        sep.addSink(id, sink);
    }

    protected void removeSink(String id) {
        sep.removeSink(id);
    }

    protected <T> void publishSignal(String filter) {
        sep.publishSignal(filter);
    }

    protected <T> void publishSignal(String filter, T value) {
        sep.publishSignal(filter, value);
    }

    protected void publishSignal(String filter, int value) {
        sep.publishSignal(filter, value);
    }

    protected void publishSignal(String filter, double value) {
        sep.publishSignal(filter, value);
    }

    protected void publishSignal(String filter, long value) {
        sep.publishSignal(filter, value);
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
     *
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
     *
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
     *
     * @param newTime time in UTC
     */
    protected void tick(long newTime) {
        setTime(newTime);
        tick();
    }

    /**
     * Advances the time by delta and pushes an event to the SEP to force any nodes that depend on time to update
     *
     * @param deltaTime advance time by delta milliseconds
     */
    protected void tickDelta(long deltaTime) {
        advanceTime(deltaTime);
        tick();
    }

    protected void tickDelta(long deltaTime, int repeats) {
        for (int i = 0; i < repeats; i++) {
            tickDelta(deltaTime);
        }
    }
    public void addClock() {
        if (!timeAdded) {
            time = new TestMutableNumber();
            time.set(0);
            onEvent(registerClockEvent(time::longValue));
        }
        timeAdded = true;
    }

    public void addAuditor() {
        addAuditor = true;
    }

    @SneakyThrows
    protected void auditToFile(String fileNamePrefix) {
        fileNamePrefix = fileNamePrefix + (compiledSep ? "-compiled.yaml" : "-interpreted.yaml");
        File file = new File("target" + File.separator + "generated-test-sources" + File.separator + "fluxtion-log" + File.separator + fileNamePrefix);
        FileUtils.forceMkdirParent(file);
        onEvent(new EventLogControlEvent(new JULLogRecordListener(file)));
    }

}
