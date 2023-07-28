package com.fluxtion.compiler.generation.exportfunction;

import com.fluxtion.compiler.generation.exportfunction.ExportFunctionTest.Aggregator;
import com.fluxtion.compiler.generation.exportfunction.ExportFunctionTest.Aggregator2;
import com.fluxtion.compiler.generation.exportfunction.ExportFunctionTest.ExportingNode;
import com.fluxtion.compiler.generation.exportfunction.ExportFunctionTest.ExportingNodeNoOverride;
import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.InMemoryOnlySepTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class ExportInterpretedTest extends InMemoryOnlySepTest {
    public ExportInterpretedTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void exportTest() {
        sep(c -> {
            c.addNode(new Aggregator(
                    new ExportingNode("export1"),
                    new ExportingNode("export2")
            ), "aggregator");
            c.addInterfaceImplementation(MyExportedInterface.class);
        });
        MyExportedInterface myExportedInterface = sep.getExportedService();
        Aggregator aggregator = getField("aggregator");
        myExportedInterface.updatedDetails("hello", 300);
        Assert.assertEquals(2, aggregator.updateCount);
        Assert.assertEquals(1, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='updatedDetails s:hello y:300'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='updatedDetails s:hello y:300'}", aggregator.parent2.toString());

        //
        myExportedInterface.complexCallBack(Arrays.asList("", "", ""), 33);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='complexCallBack s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='complexCallBack s:3 y:33'}", aggregator.parent2.toString());

        myExportedInterface.complexCallBackDouble(Arrays.asList(24.5), 344);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='complexCallBack s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='complexCallBack s:3 y:33'}", aggregator.parent2.toString());
    }

    @Test
    public void exportTestNoOverride() {
        sep(c -> {
            c.addNode(new Aggregator2(
                    new ExportingNodeNoOverride("export1"),
                    new ExportingNodeNoOverride("export2")
            ), "aggregator");
            c.addInterfaceImplementation(MyExportedInterfaceNoOverride.class);
        });
        MyExportedInterfaceNoOverride myExportedInterface = sep.getExportedService();
        Aggregator2 aggregator = getField("aggregator");
        myExportedInterface.myfunctionString("hello", 300);
        Assert.assertEquals(2, aggregator.updateCount);
        Assert.assertEquals(1, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='myfunction s:hello y:300'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='myfunction s:hello y:300'}", aggregator.parent2.toString());

        //
        myExportedInterface.myfunctionList(Arrays.asList("", "", ""), 33);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='myfunction s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='myfunction s:3 y:33'}", aggregator.parent2.toString());

        myExportedInterface.myfunctionDouble(Arrays.asList(24.5), 344);
        Assert.assertEquals(4, aggregator.updateCount);
        Assert.assertEquals(2, aggregator.triggerCount);
        Assert.assertEquals("ExportingNode{name='export1', result='myfunction s:3 y:33'}", aggregator.parent.toString());
        Assert.assertEquals("ExportingNode{name='export2', result='myfunction s:3 y:33'}", aggregator.parent2.toString());
    }
}
