package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.RootNodeConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.event.Signal.IntSignal;
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

public class RootNodeWithFactoryTest extends MultipleSepTargetInProcessTest {

    public RootNodeWithFactoryTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void graphFromMapTest() {
        sep(new RootNodeConfig(
                "root",
                SignalGroupCalculator.class,
                ImmutableMap.of("keys", Arrays.asList("key1", "key2", "key3")),
                null));

        SignalGroupCalculator calculator = getField("root");
        publishIntSignal("key1", 20);
        assertThat(calculator.getSum(), is(20));

        publishIntSignal("key2", 500);
        assertThat(calculator.getSum(), is(520));

        publishIntSignal("key3", 480);
        assertThat(calculator.getSum(), is(1000));

        publishIntSignal("keyXXXX", 545484);
        assertThat(calculator.getSum(), is(1000));
    }

    @Data
    public static class SignalHandler {
        private final String filter;
        private transient int value;

        @OnEventHandler(filterVariable = "filter")
        public boolean signalUpdate(IntSignal intSignal) {
            value = intSignal.getValue();
            return true;
        }
    }

    @Data
    public static class SignalGroupCalculator {
        private final List<SignalHandler> handlers;
        private int sum;

        @OnTrigger
        public boolean calculate() {
            sum = 0;
            for (SignalHandler handler : handlers) {
                sum += handler.getValue();
            }
            return true;
        }
    }

    @AutoService(NodeFactory.class)
    public static class SignalGroupCalculatorFactory implements NodeFactory<SignalGroupCalculator> {

        @Override
        public SignalGroupCalculator createNode(Map<String, Object> config, NodeRegistry registry) {
            @SuppressWarnings("unchecked")
            List<String> keys = (List<String>) config.get("keys");
            return new SignalGroupCalculator(keys.stream().map(SignalHandler::new).collect(Collectors.toList()));
        }
    }
}
