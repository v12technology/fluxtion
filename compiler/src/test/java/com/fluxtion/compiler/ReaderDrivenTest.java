package com.fluxtion.compiler;

import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Test;

import java.io.StringReader;

public class ReaderDrivenTest {

    @Test
    public void fromStringTest(){
        EventProcessor eventProcessor = Fluxtion.compileFromReader(new StringReader(mapDump));
        eventProcessor.init();
        eventProcessor.onEvent("hello");
    }
//              outputDirectory: null
//              buildOutputDirectory: null
//              resourcesOutputDirectory: null

    private static String mapDump = """
            compilerConfig:
              className: MyProcessor
              packageName: com.mypackage
              compileSource: false
              formatSource: false
              generateDescription: false
              writeSourceToFile: false
            configMap:
              anotherKey: hello
              test: 12
            name: myRoot
            rootClass: com.fluxtion.compiler.ReaderDrivenTest$MyRootClass
            """;

    public static class MyRootClass{
        @OnEventHandler
        public void updated(String in){}
    }
}
