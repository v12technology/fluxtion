package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.AfterEvent;
import com.fluxtion.runtime.annotations.AfterTrigger;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.node.NamedNode;
import org.junit.Assert;
import org.junit.Test;

public class ExportMultipleServiceTest extends MultipleSepTargetInProcessTest {


    public ExportMultipleServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }


    @Test
    public void multiServiceExportAuditTest() {
//        writeSourceFile = true;
        addAuditor();
        sep(new BottomNode());
        sep.setAuditLogProcessor(l -> {
        });
        Top top = sep.getExportedService();
        top.notifyTop(10);
        top.notifyTopNoArgs();
    }

    @Test
    public void multiServiceExportTest() {
//        writeSourceFile = true;
        sep(new BottomNode());
        //services
        Top top = sep.getExportedService();
        Middle middle = sep.getExportedService();
        Bottom bottom = sep.getExportedService();
        //nodes
        TopNode topNode = getField("top");
        MiddleNode middleNode = getField("middle");
        BottomNode bottomNode = getField("bottom");
        Assert.assertEquals(1, bottomNode.afterEventCount);
        //
        top.notifyTop(10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(0, middleNode.functionCount);
        Assert.assertEquals(1, middleNode.triggerCount);
        Assert.assertEquals(0, bottomNode.functionCount);
        Assert.assertEquals(1, bottomNode.triggerCount);
        Assert.assertEquals(1, bottomNode.afterTriggerCount);
        Assert.assertEquals(2, bottomNode.afterEventCount);

        //
        bottom.notifyBottom(10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(0, middleNode.functionCount);
        Assert.assertEquals(1, middleNode.triggerCount);
        Assert.assertEquals(1, bottomNode.functionCount);
        Assert.assertEquals(1, bottomNode.triggerCount);
        Assert.assertEquals(1, bottomNode.afterTriggerCount);
        Assert.assertEquals(3, bottomNode.afterEventCount);

        //no trigger bottom
        middle.notifyMiddle(-10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(1, middleNode.functionCount);
        Assert.assertEquals(1, middleNode.triggerCount);//?
        Assert.assertEquals(1, bottomNode.functionCount);
        Assert.assertEquals(1, bottomNode.triggerCount);
        Assert.assertEquals(1, bottomNode.afterTriggerCount);
        Assert.assertEquals(4, bottomNode.afterEventCount);

        //trigger middle
        middle.notifyMiddle(10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(2, middleNode.functionCount);
        Assert.assertEquals(1, middleNode.triggerCount);
        Assert.assertEquals(1, bottomNode.functionCount);
        Assert.assertEquals(2, bottomNode.triggerCount);
        Assert.assertEquals(2, bottomNode.afterTriggerCount);
        Assert.assertEquals(5, bottomNode.afterEventCount);
    }

    public interface Top {
        void notifyTop(int arg);

        void notifyTopNoArgs();
    }

    public interface Middle {
        boolean notifyMiddle(int arg);
    }

    public interface Bottom {
        boolean notifyBottom(int arg);
    }

    public static class TopNode implements @ExportService Top, NamedNode {

        int functionCount = 0;

        @Override
        public void notifyTop(int arg) {
            functionCount++;
        }

        public void notifyTopNoArgs() {

        }

        public boolean trigger() {
            return true;
        }

        @Override
        public String getName() {
            return "top";
        }
    }

    public static class MiddleNode implements @ExportService Middle, NamedNode {
        private final TopNode topNode;
        int triggerCount = 0;
        int functionCount = 0;

        public MiddleNode(TopNode topNode) {
            this.topNode = topNode;
        }

        public MiddleNode() {
            this(new TopNode());
        }

        @Override
        public boolean notifyMiddle(int arg) {
            functionCount++;
            return arg > 0;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @Override
        public String getName() {
            return "middle";
        }
    }

    public static class BottomNode implements @ExportService Bottom, NamedNode {

        private final MiddleNode middleNode;
        int triggerCount = 0;
        int functionCount = 0;
        int afterEventCount = 0;
        int afterTriggerCount = 0;

        public BottomNode(MiddleNode middleNode) {
            this.middleNode = middleNode;
        }

        public BottomNode() {
            this(new MiddleNode());
        }

        @Override
        public boolean notifyBottom(int arg) {
            functionCount++;
            return false;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            return true;
        }

        @AfterEvent
        public void afterEvent() {
            afterEventCount++;
        }

        @AfterTrigger
        public void afterTrigger() {
            afterTriggerCount++;
        }

        @Override
        public String getName() {
            return "bottom";
        }
    }

}