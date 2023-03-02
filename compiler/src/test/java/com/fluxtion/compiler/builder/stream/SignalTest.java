package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.junit.Test;

import static com.fluxtion.compiler.builder.stream.EventFlow.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SignalTest extends MultipleSepTargetInProcessTest {
    public SignalTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void cfgTest() {
        sep(c -> c.addNode(new ConfigReceiver(), "cfg"));
        publishSignal("route", "hello world");
        publishIntSignal("intRoute", 12);
        publishSignal("integerRoute", (Integer) (35));

        ConfigReceiver cfg = getField("cfg");
        assertThat(cfg.stringValue, is("hello world"));
        assertThat(cfg.integerValue, is(35));
        assertThat(cfg.intValue, is(12));
    }

    @Test
    public void multipleSignalSubscriptionsTest() {
        sep(c -> {
            EventFlow.subscribeToSignal("A").mapToInt(Mappers.count()).id("A_count");
            EventFlow.subscribeToSignal("B").mapToInt(Mappers.count()).id("B_count");
        });
        publishSignal("A");
        publishSignal("B");
        publishSignal("A");
        publishSignal("B");
        publishSignal("VDFFF");
        publishSignal("A");
        publishSignal("A");

        assertThat(getStreamed("A_count"), is(4));
        assertThat(getStreamed("B_count"), is(2));
    }

    @Test
    public void subscribeWithDefaultValue() {
        sep(c -> {
            subscribeToIntSignal("A")
                    .mapBiFunction(Mappers.ADD_INTS, subscribeToIntSignal("B", 10)).id("ansInt");

            subscribeToDoubleSignal("P")
                    .mapBiFunction(Mappers.ADD_DOUBLES, subscribeToDoubleSignal("Q", 2.5)).id("ansDouble");

            subscribeToLongSignal("X")
                    .mapBiFunction(Mappers.ADD_LONGS, subscribeToLongSignal("Y", 10)).id("ansLong");

        });
        publishIntSignal("A", 100);
        assertThat(getStreamed("ansInt"), is(110));
        publishIntSignal("B", 100);
        assertThat(getStreamed("ansInt"), is(200));

        publishDoubleSignal("P", 2.5);
        assertThat(getStreamed("ansDouble"), is(5.0));
        publishDoubleSignal("Q", 100d);
        assertThat(getStreamed("ansDouble"), is(102.5));

        publishLongSignal("X", 1000);
        assertThat(getStreamed("ansLong"), is(1_010L));
        publishLongSignal("Y", 100);
        assertThat(getStreamed("ansLong"), is(1_100L));
    }

    public static class ConfigReceiver {

        public String stringValue;
        public Integer integerValue;
        public int intValue;

        @OnEventHandler(filterString = "route")
        public boolean setConfig(Signal<String> cfg) {
            this.stringValue = cfg.getValue();
            this.stringValue = cfg.getValue();
            return true;
        }

        @OnEventHandler(filterString = "integerRoute")
        public boolean setIntegerConfig(Signal<Integer> cfg) {
            this.integerValue = cfg.getValue();
            return true;
        }

        @OnEventHandler(filterString = "intRoute")
        public boolean setIntConfig(Signal.IntSignal cfg) {
            this.intValue = cfg.getValue();
            return true;
        }
    }
}
