package com.fluxtion.compiler.builder.stream;

import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.event.Signal;
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
        assertThat(cfg.integerValue, is( (Integer)(35)));
        assertThat(cfg.intValue, is(12));
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
