package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.output.SinkPublisher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;

public class RootNodeWithFactoryAndYamlConfigTest {

    @Test
    public void compileFromStringTest() {
        EventProcessor eventProcessor = Fluxtion.compileFromReader(new StringReader(compileString));
        eventProcessor.init();

        List<String> myList = new ArrayList<>();
        eventProcessor.addSink("sinkA", (Consumer<String>) myList::add);
        eventProcessor.onEvent("hello world");
        assertThat(myList, Matchers.is(Matchers.contains("hello world")));
    }

    @Test
    public void interpretFromStringTest() {
        EventProcessor eventProcessor = Fluxtion.compileFromReader(new StringReader(interpretString));
        eventProcessor.init();

        List<String> myList = new ArrayList<>();
        eventProcessor.addSink("sinkA", (Consumer<String>) myList::add);
        eventProcessor.onEvent("hello world");
        assertThat(myList, Matchers.is(Matchers.contains("hello world")));
    }

    @Test
    public void interpretNoConfigFromStringTest() {
        EventProcessor eventProcessor = Fluxtion.compileFromReader(new StringReader(interpretNoConfigString));
        eventProcessor.init();

        List<String> myList = new ArrayList<>();
        eventProcessor.addSink("sinkA", (Consumer<String>) myList::add);
        eventProcessor.onEvent("hello world");
        assertThat(myList, Matchers.is(Matchers.contains("hello world")));
    }

    private static final String compileString = "compilerConfig:\n" +
            "  className: MyProcessor\n" +
            "  packageName: com.mypackage\n" +
            "  compileSource: true\n" +
            "  formatSource: false\n" +
            "  generateDescription: false\n" +
            "  writeSourceToFile: false\n" +
            "configMap:\n" +
            "  anotherKey: hello\n" +
            "  test: 12\n" +
            "name: myRoot\n" +
            "rootClass: com.fluxtion.compiler.builder.factory.RootNodeWithFactoryAndYamlConfigTest$MyRootClass";

    private static final String interpretString = "compilerConfig:\n" +
            "  className: MyProcessor\n" +
            "  packageName: com.mypackage\n" +
            "  formatSource: false\n" +
            "  interpreted: true\n" +
            "  generateDescription: false\n" +
            "  writeSourceToFile: false\n" +
            "configMap:\n" +
            "  anotherKey: hello\n" +
            "  test: 12\n" +
            "name: myRoot\n" +
            "rootClass: com.fluxtion.compiler.builder.factory.RootNodeWithFactoryAndYamlConfigTest$MyRootClass";

    private static final String interpretNoConfigString = "compilerConfig:\n" +
            "  className: MyProcessor\n" +
            "  packageName: com.mypackage\n" +
            "  interpreted: true\n" +
            "  formatSource: false\n" +
            "  generateDescription: false\n" +
            "  writeSourceToFile: false\n" +
            "name: myRoot\n" +
            "rootClass: com.fluxtion.compiler.builder.factory.RootNodeWithFactoryAndYamlConfigTest$MyRootClass";

    public static class MyRootClass {

        public SinkPublisher<String> publisher = new SinkPublisher<>("sinkA");

        @OnEventHandler
        public boolean updated(String in) {
            publisher.publish(in);
            return true;
        }
    }
}
