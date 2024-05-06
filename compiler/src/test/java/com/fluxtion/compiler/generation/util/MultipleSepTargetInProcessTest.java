/*
 * Copyright (c) 2019, 2024 gregory higgins.
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

import com.fluxtion.compiler.EventProcessorConfig;
import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.generation.GenerationContext;
import com.fluxtion.compiler.generation.OutputRegistry;
import com.fluxtion.compiler.generation.compiler.EventProcessorGenerator;
import com.fluxtion.compiler.generation.model.SimpleEventProcessorModel;
import com.fluxtion.compiler.generation.targets.InMemoryEventProcessor;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.StaticEventProcessor;
import com.fluxtion.runtime.audit.EventLogControlEvent;
import com.fluxtion.runtime.audit.JULLogRecordListener;
import com.fluxtion.runtime.callback.InstanceCallbackEvent;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import lombok.SneakyThrows;
import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import static com.fluxtion.compiler.generation.EventProcessorFactory.compileTestInstance;
import static com.fluxtion.runtime.time.ClockStrategy.registerClockEvent;

/**
 * Test class utility for building a SEP in process
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
@RunWith(Parameterized.class)
public abstract class MultipleSepTargetInProcessTest {

    //parametrized test config
    protected final boolean compiledSep;
    protected boolean instanceOfDispatch = true;
    @Rule
    public TestName testName = new TestName();
    protected StaticEventProcessor sep;
    protected boolean generateMetaInformation = false;
    protected boolean writeSourceFile = false;
    protected boolean fixedPkg = true;
    protected boolean reuseSep = false;
    protected boolean generateLogging = false;
    protected TestMutableNumber time;
    protected boolean timeAdded = false;
    protected boolean callInit;
    protected boolean inlineCompiled = false;
    protected SimpleEventProcessorModel simpleEventProcessorModel;
    private boolean addAuditor = false;
    private InMemoryEventProcessor inMemorySep;

    public MultipleSepTargetInProcessTest(SepTestConfig testConfig) {
        this.compiledSep = testConfig.isCompiled();
        inlineCompiled = testConfig == SepTestConfig.COMPILED_INLINE;
        instanceOfDispatch = !(testConfig == SepTestConfig.COMPILED_SWITCH_DISPATCH);
    }

    @Parameterized.Parameters
    public static Collection<?> compiledSepStrategy() {
        return Arrays.asList(
                SepTestConfig.COMPILED_SWITCH_DISPATCH,
                SepTestConfig.COMPILED_METHOD_PER_EVENT,
                SepTestConfig.INTERPRETED
        );
    }

    @Before
    public void beforeTest() {
        fixedPkg = true;
        addAuditor = false;
        reuseSep = false;
        callInit = true;
        InstanceCallbackEvent.reset();
    }

    @After
    public void afterTest() {
        tearDown();
    }

    protected EventProcessor<?> eventProcessor() {
        return (EventProcessor<?>) sep;
    }

    protected StaticEventProcessor sep(RootNodeConfig rootNode) {
        return sep((EventProcessorConfig cfg) -> cfg.setRootNodeConfig(rootNode));
    }

    @SuppressWarnings("unchecked")
    protected <T extends StaticEventProcessor> T sep(Class<T> handlerClass) {
        GenerationContext.setupStaticContext(pckName(), sepClassName(),
                new File(OutputRegistry.JAVA_TESTGEN_DIR),
                new File(OutputRegistry.RESOURCE_GENERATED_TEST_DIR));
        try {
            sep = handlerClass.getDeclaredConstructor().newInstance();
            init();
            return (T) sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected StaticEventProcessor sep(Consumer<EventProcessorConfig> cfgBuilder) {
        return sep(cfgBuilder, new HashMap<>());
    }

    protected StaticEventProcessor sep(Object... nodes) {
        return sep(c -> {
            for (int i = 0; i < nodes.length; i++) {
                c.addNode(nodes[i]);
            }
        });
    }

    protected StaticEventProcessor sep(Consumer<EventProcessorConfig> cfgBuilder, Map<Object, Object> contextMap) {
        Consumer<EventProcessorConfig> wrappedBuilder = cfgBuilder;
        if (addAuditor || inlineCompiled || !instanceOfDispatch) {
            wrappedBuilder = cfg -> {
                cfgBuilder.accept(cfg);
                if (addAuditor)
                    cfg.addEventAudit(EventLogControlEvent.LogLevel.INFO);
                cfg.setInlineEventHandling(inlineCompiled);
                cfg.setInstanceOfDispatch(instanceOfDispatch);
            };
        }

        try {
            if (!compiledSep) {
                GenerationContext.setupStaticContext(pckName(), sepClassName(),
                        new File(OutputRegistry.JAVA_TESTGEN_DIR),
                        new File(OutputRegistry.RESOURCE_GENERATED_TEST_DIR));
                EventProcessorConfig cfg = new EventProcessorConfig();
                cfg.setSupportDirtyFiltering(true);
                wrappedBuilder.accept(cfg);
                EventProcessorGenerator eventProcessorGenerator = new EventProcessorGenerator();
                inMemorySep = eventProcessorGenerator.inMemoryProcessor(cfg, generateMetaInformation);
                inMemorySep.setContextParameterMap(contextMap);
                sep = inMemorySep;
                init();
                simpleEventProcessorModel = eventProcessorGenerator.getSimpleEventProcessorModel();
            } else {
                if (reuseSep) {
                    try {
                        GenerationContext.setupStaticContext("", "",
                                new File(OutputRegistry.JAVA_TESTGEN_DIR),
                                new File(OutputRegistry.RESOURCE_GENERATED_TEST_DIR));
                        sep = (StaticEventProcessor) Class.forName(fqn()).getDeclaredConstructor().newInstance();
                        sep.setContextParameterMap(contextMap);
                        init();
                    } catch (Exception e) {
                    }
                }
                if (sep == null) {

                    sep = compileTestInstance(wrappedBuilder, pckName(), sepClassName(), writeSourceFile, generateMetaInformation);
                    sep.setContextParameterMap(contextMap);
                    init();
                }
            }
            return sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void enableInitCheck(boolean callInit) {
        this.callInit = callInit;
    }

    protected void writeOutputsToFile(boolean write) {
        generateMetaInformation = write;
        writeSourceFile = write;
    }

    protected StaticEventProcessor init() {
        if (callInit && sep instanceof Lifecycle) {
            ((Lifecycle) sep).init();
        }
        return sep;
    }

    protected StaticEventProcessor start() {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).start();
        }
        return sep;
    }

    protected StaticEventProcessor stop() {
        if (sep instanceof Lifecycle) {
            ((Lifecycle) sep).stop();
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
        return "TestSep_" + testName.getMethodName().replaceAll("\\[([0-9]*?)]", "") + (inlineCompiled ? "Inline" : "");
    }

    protected String fqn() {
        return pckName() + "." + sepClassName();
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    protected <T> T getField(String name) {
        if (compiledSep) {
            return sep.getNodeById(name);//T) new Mirror().on(sep).get().field(name);
        }
        return inMemorySep.getNodeById(name);
    }

    protected <T> T getField(String name, Class<T> clazz) {
        return getField(name);
    }

    @SneakyThrows
    protected <T> T getAuditor(String name) {
        if (compiledSep) {
            return (T) new Mirror().on(sep).get().field(name);
        }
        return getField(name);
    }

    protected <T> T getStreamed(String name) {
        FlowFunction<T> stream = getField(name);
        return stream.get();
    }

    protected <T> T getStreamed(String name, Class<T> clazz) {
        FlowFunction<T> stream = getField(name);
        return stream.get();
    }

    protected void onEvent(Object e) {
        try {
            sep.onEvent(e);
        } catch (Exception exception) {
            System.out.println("Exception:" + exception + ", " + exception.getMessage());
            System.out.println("Last log:\n" + sep.getLastAuditLogRecord());
            throw new RuntimeException(exception);
        }
    }

    protected void bufferEvent(Object e) {
        sep.bufferEvent(e);
    }

    protected void triggerCalculation() {
        sep.triggerCalculation();
    }

    protected void onEvent(byte value) {
        onEvent((Byte) value);
    }

    protected void onEvent(char value) {
        onEvent((Character) value);
    }

    protected void onEvent(short value) {
        onEvent((Short) value);
    }

    protected void onEvent(int value) {
        onEvent((Integer) value);
    }

    protected void onEvent(float value) {
        onEvent((Float) value);
    }

    protected void onEvent(double value) {
        onEvent((Double) value);
    }

    protected void onEvent(long value) {
        onEvent((Long) value);
    }

    protected void onGenericEvent(Object e) {
        onEvent(e);
    }

    protected <T> void addSink(String id, Consumer<T> sink) {
        sep.addSink(id, sink);
    }

    protected void addIntSink(String id, IntConsumer sink) {
        sep.addIntSink(id, sink);
    }

    protected void addDoubleSink(String id, DoubleConsumer sink) {
        sep.addDoubleSink(id, sink);
    }

    protected void addLongSink(String id, LongConsumer sink) {
        sep.addLongSink(id, sink);
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

    protected <T> void publishInstance(T value) {
        sep.publishObjectSignal(value);
    }

    protected <S, T> void publishInstance(Class<S> classFilter, T value) {
        sep.publishObjectSignal(classFilter, value);
    }

    protected void publishIntSignal(String filter, int value) {
        sep.publishSignal(filter, value);
    }

    protected void publishDoubleSignal(String filter, double value) {
        sep.publishSignal(filter, value);
    }

    protected void publishLongSignal(String filter, long value) {
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
     * sets the time of the clock and then calls start after the clock has been set
     *
     * @param newTime
     * @return
     */
    protected StaticEventProcessor startTime(long newTime) {
        addClock();
        time.set(newTime);
        start();
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
