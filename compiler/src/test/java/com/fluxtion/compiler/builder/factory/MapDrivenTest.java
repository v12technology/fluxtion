package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.event.Signal;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class MapDrivenTest extends MultipleSepTargetInProcessTest {

    public MapDrivenTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void graphFromMapTest(){
        sep(new RootNodeConfig(
                "root",
                SignalGroupCalculator.class,
                ImmutableMap.of("keys", Arrays.asList("key1", "key2", "key3"))));

        SignalGroupCalculator calculator = getField("root");
        publishSignal("key1", 20);
        assertThat(calculator.getSum(), is(20));

        publishSignal("key2", 500);
        assertThat(calculator.getSum(), is(520));

        publishSignal("key3", 480);
        assertThat(calculator.getSum(), is(1000));

        publishSignal("keyXXXX", 545484);
        assertThat(calculator.getSum(), is(1000));
    }

    @Data
    public static class SignalHandler {
        private final String filter;
        private transient int value;

        @OnEventHandler(filterVariable = "filter")
        public void signalUpdate(Signal.IntSignal intSignal) {
            value = intSignal.getValue();
        }
    }

    @Data
    public static class SignalGroupCalculator{
        private final List<SignalHandler> handlers;
        private int sum;

        @OnTrigger
        public void calculate(){
            sum = 0;
            for (SignalHandler handler: handlers) {
                sum += handler.getValue();
            }
        }
    }

    @AutoService(NodeFactory.class)
    public static class SignalGroupCalculatorFactory implements NodeFactory<SignalGroupCalculator>{

        @Override
        public SignalGroupCalculator createNode(Map<String, Object> config, NodeRegistry registry) {
            @SuppressWarnings("unchecked")
            List<String> keys = (List<String>) config.get("keys");
            return new SignalGroupCalculator(keys.stream().map(SignalHandler::new).collect(Collectors.toList()));
        }
    }
}
