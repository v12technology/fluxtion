package com.fluxtion.compiler.builder.factory;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.runtime.EventProcessor;
import com.fluxtion.runtime.annotations.OnEventHandler;
import org.junit.Test;

import java.io.StringReader;

public class YamlReaderDrivenTest {

    @Test
    public void fromStringTest(){
        EventProcessor eventProcessor = Fluxtion.compileFromReader(new StringReader(mapDump));
        eventProcessor.init();
        eventProcessor.onEvent("hello");
    }
//              outputDirectory: null
//              buildOutputDirectory: null
//              resourcesOutputDirectory: null

    private static String mapDump = "compilerConfig:\n" +
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
            "rootClass: com.fluxtion.compiler.builder.factory.YamlReaderDrivenTest$MyRootClass";

    public static class MyRootClass{
        @OnEventHandler
        public void updated(String in){
            System.out.println("This worked!! " + in);
        }
    }
}
