package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Signal;
import com.fluxtion.runtime.stream.helpers.Mappers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SignalTest extends MultipleSepTargetInProcessTest {
    public SignalTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void cfgTest(){
        sep(c -> c.addNode(new ConfigReceiver(), "cfg"));
        publishSignal("route", "hello world");
        publishSignal("intRoute", 12);
        publishSignal("integerRoute", (Integer)(35));

        ConfigReceiver cfg = getField("cfg");
        assertThat(cfg.stringValue, is("hello world"));
        assertThat(cfg.integerValue, is( 35));
        assertThat(cfg.intValue, is(12));
    }

    @Test
    public void multipleSignalSubscriptionsTest(){
        sep(c->{
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

    public static class ConfigReceiver{

        public String stringValue;
        public Integer integerValue;
        public int intValue;

        @OnEventHandler(filterString = "route")
        public void setConfig(Signal<String> cfg){
            this.stringValue = cfg.getValue();
            this.stringValue = cfg.getValue();
        }

        @OnEventHandler(filterString = "integerRoute")
        public void setIntegerConfig(Signal<Integer> cfg){
            this.integerValue = cfg.getValue();
        }

        @OnEventHandler(filterString = "intRoute")
        public void setIntConfig(Signal.IntSignal cfg){
            this.intValue = cfg.getValue();
        }
    }
}
