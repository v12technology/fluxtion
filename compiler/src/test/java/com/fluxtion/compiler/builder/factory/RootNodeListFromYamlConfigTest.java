package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.compiler.builder.factory.GraphOfInstancesTest.DoubleSum;
import com.fluxtion.compiler.builder.factory.GraphOfInstancesTest.StringHandler;
import com.fluxtion.runtime.EventProcessor;
import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RootNodeListFromYamlConfigTest {
    private static final String compileString = "compilerConfig:\n" +
            "  className: MyProcessor\n" +
            "  packageName: com.mypackage\n" +
            "  compileSource: true\n" +
            "  formatSource: false\n" +
            "  generateDescription: false\n" +
            "  writeSourceToFile: false\n" +
            "nodes:\n" +
            "  - !!com.fluxtion.compiler.builder.factory.GraphOfInstancesTest$StringHandler\n" +
            "    id: C\n" +
            "  - !!com.fluxtion.compiler.builder.factory.GraphOfInstancesTest$DoubleSum\n" +
            "    doubleSuppliers:\n" +
            "    - !!com.fluxtion.compiler.builder.factory.GraphOfInstancesTest$StringHandler\n" +
            "      id: A\n" +
            "    - !!com.fluxtion.compiler.builder.factory.GraphOfInstancesTest$StringHandler\n" +
            "      id: B\n" +
            "";

    @Test
    public void interpretFromStringTest() throws NoSuchFieldException {
        EventProcessor eventProcessor = Fluxtion.compileFromReader(new StringReader(compileString));
        eventProcessor.init();

        DoubleSum aggregator = eventProcessor.getNodeById("aggregator");
        StringHandler handlerC = eventProcessor.getNodeById("stringHandler_C");
        eventProcessor.onEvent("A");
        assertThat(aggregator.sum, is(1.0));
        assertThat(handlerC.value, is(0));
        eventProcessor.onEvent("A");
        assertThat(aggregator.sum, is(2.0));
        assertThat(handlerC.value, is(0));
        eventProcessor.onEvent("B");
        assertThat(aggregator.sum, is(3.0));
        assertThat(handlerC.value, is(0));
        eventProcessor.onEvent("C");
        assertThat(aggregator.sum, is(3.0));
        assertThat(handlerC.value, is(1));
    }
}
