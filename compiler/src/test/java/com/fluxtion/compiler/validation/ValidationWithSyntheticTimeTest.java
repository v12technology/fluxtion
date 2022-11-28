package com.fluxtion.compiler.validation;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.extension.csvcompiler.annotations.CsvMarshaller;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.stream.SinkPublisher;
import com.fluxtion.runtime.time.Clock;
import org.junit.Test;

import java.util.function.Supplier;

/**
 * Example demonstrating using data to drive wall clock time in the graph. The {@link Clock} in the graph
 * can be data driven
 */
public class ValidationWithSyntheticTimeTest extends BaseEventProcessorRowBasedTest {

    @Test
    public void dataControlledClockTime() {
        useSyntheticTime();
        validate(
                Fluxtion.interpret(c -> c.addNode(new DeltaTime())),
                "timeDelta",
                DeltaValidation.class,
                "time,signal,expectedDelta\n" +
                        "10,ddd,10\n" +
                        "300,sdsd,290\n" +
                        "500,sdcdfd,200"
        );
    }

    public static class DeltaTime {

        public SinkPublisher<Long> deltaPublisher = new SinkPublisher<>("timeDelta");
        @Inject
        public Clock clock;

        private long previousTime = 0;

        @OnEventHandler
        public boolean handleString(String in) {
            long wallClockTime = clock.getWallClockTime();
            deltaPublisher.publish(wallClockTime - previousTime);
            previousTime = wallClockTime;
            return true;
        }
    }

    @CsvMarshaller(trim = true)
    public static class DeltaValidation implements Supplier<TestRowValidationRecord<Long>> {

        private long time;
        private String signal;
        private long expectedDelta;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getSignal() {
            return signal;
        }

        public void setSignal(String signal) {
            this.signal = signal;
        }

        public long getExpectedDelta() {
            return expectedDelta;
        }

        public void setExpectedDelta(long expectedDelta) {
            this.expectedDelta = expectedDelta;
        }

        @Override
        public TestRowValidationRecord<Long> get() {
            return new TestRowValidationRecord<>(time, signal, expectedDelta);
        }
    }
}
