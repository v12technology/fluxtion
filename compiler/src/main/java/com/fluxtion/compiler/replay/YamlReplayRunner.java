package com.fluxtion.compiler.replay;

import com.fluxtion.compiler.validation.BaseEventProcessorRowBasedTest;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.event.ReplayRecord;

import java.io.Reader;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility for replaying {@link ReplayRecord}'s into an {@link EventProcessor}. ReplayRecord's in the form of yaml are
 * read using a supplied reader. Supports:
 *
 * <ul>
 *     <li>Optionally call init on the EventProcessor under test</li>
 *     <li>Optionally call start on the EventProcessor under test</li>
 *     <li>Optionally set a time range for records to push to the EventProcessor under test</li>
 *     <li>Optionally set a minimum start time for records to push to the EventProcessor under test</li>
 *     <li>Optionally set a maximum end time for records to push to the EventProcessor under test</li>
 * </ul>
 * <p>
 * Sample usage for between times, calling lifecycle methods init and start:
 * <pre>
 *  EventProcessor eventProcessor = new MyEventProcessor();
 *  YamlReplayRunner.newSession(new StringReader(replayEventLog), eventProcessor)
 *         .betweenTimes(600, 680)
 *         .callInit()
 *         .callStart()
 *         .runReplay();
 * </pre>
 */
public class YamlReplayRunner {

    private final Reader yamlReplayRecordSource;
    private final EventProcessor<?> eventProcessor;
    private long minimumTime = Long.MIN_VALUE;
    private long maximumTime = Long.MAX_VALUE;

    private YamlReplayRunner(Reader yamlReplayRecordSource, EventProcessor<?> eventProcessor) {
        this.yamlReplayRecordSource = yamlReplayRecordSource;
        this.eventProcessor = eventProcessor;
    }

    public static YamlReplayRunner newSession(Reader yamlReplayRecordSource, EventProcessor<?> eventProcessor) {
        return new YamlReplayRunner(yamlReplayRecordSource, eventProcessor);
    }

    public YamlReplayRunner callInit() {
        eventProcessor.init();
        return this;
    }

    public YamlReplayRunner callStart() {
        eventProcessor.start();
        return this;
    }

    public YamlReplayRunner startComplete() {
        eventProcessor.startComplete();
        return this;
    }

    public YamlReplayRunner afterTime(long startTime) {
        return betweenTimes(startTime, Long.MAX_VALUE);
    }

    public YamlReplayRunner beforeTime(long stopTime) {
        return betweenTimes(Long.MIN_VALUE, stopTime);
    }

    public YamlReplayRunner betweenTimes(long startTime, long stopTime) {
        minimumTime = startTime;
        maximumTime = stopTime;
        return this;
    }

    public void runReplay() {
        AtomicLong timeSupplier = new AtomicLong();
        eventProcessor.setClockStrategy(timeSupplier::get);
        //run the audit log setting the clock time programmatically from the replay time
        BaseEventProcessorRowBasedTest.yamlToStream(yamlReplayRecordSource, ReplayRecord.class)
                .forEachOrdered(t -> {
                    long wallClockTime = t.getWallClockTime();
                    if (wallClockTime < maximumTime && wallClockTime > minimumTime) {
                        timeSupplier.set(wallClockTime);
                        eventProcessor.onEvent(t.getEvent());
                    }
                });
    }
}
