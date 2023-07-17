package com.fluxtion.compiler.generation.exportservice;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.ExportService;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.callback.ExportFunctionNode;
import com.fluxtion.runtime.node.NamedNode;
import org.junit.Assert;
import org.junit.Test;

public class ExportMultipleServiceTest extends MultipleSepTargetInProcessTest {


    public ExportMultipleServiceTest(CompiledAndInterpretedSepTest.SepTestConfig testConfig) {
        super(testConfig);
    }

    @Test
    public void multiServiceExportTest() {
//        writeSourceFile = true;
        sep(new BottomNode());
        //services
        Top top = sep.asInterface();
        Middle middle = sep.asInterface();
        Bottom bottom = sep.asInterface();
        //nodes
        TopNode topNode = getField("top");
        MiddleNode middleNode = getField("middle");
        BottomNode bottomNode = getField("bottom");
        //
        top.notifyTop(10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(0, middleNode.functionCount);
        Assert.assertEquals(1, middleNode.triggerCount);
        Assert.assertEquals(0, bottomNode.functionCount);
        Assert.assertEquals(1, bottomNode.triggerCount);

        //
        bottom.notifyBottom(10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(0, middleNode.functionCount);
        Assert.assertEquals(1, middleNode.triggerCount);
        Assert.assertEquals(1, bottomNode.functionCount);
        Assert.assertEquals(2, bottomNode.triggerCount);

        //no trigger bottom
        middle.notifyMiddle(-10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(1, middleNode.functionCount);
        Assert.assertEquals(2, middleNode.triggerCount);//?
        Assert.assertEquals(1, bottomNode.functionCount);
        Assert.assertEquals(2, bottomNode.triggerCount);

        //trigger middle
        middle.notifyMiddle(10);
        Assert.assertEquals(1, topNode.functionCount);
        Assert.assertEquals(2, middleNode.functionCount);
        Assert.assertEquals(3, middleNode.triggerCount);
        Assert.assertEquals(1, bottomNode.functionCount);
        Assert.assertEquals(3, bottomNode.triggerCount);
    }

    public interface Top {
        void notifyTop(int arg);
    }

    public interface Middle {
        boolean notifyMiddle(int arg);
    }

    public interface Bottom {
        boolean notifyBottom(int arg);
    }

    public static class TopNode extends ExportFunctionNode implements @ExportService Top, NamedNode {

        int functionCount = 0;

        @Override
        public void notifyTop(int arg) {
            functionCount++;
        }

        @Override
        public String getName() {
            return "top";
        }
    }

    public static class MiddleNode extends ExportFunctionNode implements @ExportService Middle, NamedNode {
        private final TopNode topNode;
        int triggerCount = 0;
        int functionCount = 0;
        private boolean parentTriggered;

        public MiddleNode(TopNode topNode) {
            this.topNode = topNode;
        }

        public MiddleNode() {
            this(new TopNode());
        }

        @OnParentUpdate
        public void topUpdated(TopNode topNode) {
            parentTriggered = true;
        }

        @Override
        public boolean notifyMiddle(int arg) {
            functionCount++;
            return arg > 0;
        }

        @OnTrigger
        public boolean triggered() {
            triggerCount++;
            boolean tmp = parentTriggered;
            parentTriggered = false;
            return tmp || isTriggered();
        }

        @Override
        public String getName() {
            return "middle";
        }
    }

    public static class BottomNode extends ExportFunctionNode implements @ExportService Bottom, NamedNode {

        private final MiddleNode middleNode;
        int triggerCount = 0;
        int functionCount = 0;

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
            return isTriggered();
        }

        @Override
        public String getName() {
            return "bottom";
        }
    }

}
