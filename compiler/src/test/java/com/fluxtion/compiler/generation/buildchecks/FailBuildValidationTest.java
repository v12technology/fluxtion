package com.fluxtion.compiler.generation.buildchecks;

import com.fluxtion.compiler.generation.util.CompiledAndInterpretedSepTest.SepTestConfig;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.callback.DirtyStateMonitor;
import com.fluxtion.runtime.node.NamedNode;
import com.fluxtion.test.event.DefaultEventHandlerNode;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FailBuildValidationTest extends MultipleSepTargetInProcessTest {


    public FailBuildValidationTest(SepTestConfig compiledSep) {
        super(compiledSep);
    }

    @Test
    public void noFailBuild_TriggerScalarNoEventDependency() throws NoSuchFieldException {
        sep(c -> {
            c.addNode(new Triggering()).dataOnly = c.addNode(new DataOnly());
        });
        Triggering triggering = sep.getNodeById("triggerNode");
        Assert.assertFalse(triggering.triggerInvoked);
        onEvent("test");
        Assert.assertTrue(triggering.triggerInvoked);
    }

    @Test
    public void noFailBuild_TriggerCollectionNoEventDependency() throws NoSuchFieldException {
        List<DataOnly> dependencyList = new ArrayList<>();
        dependencyList.add(new DataOnly());
        dependencyList.add(new DataOnly());
        dependencyList.add(new DataOnly());
        sep(c -> {
            Triggering triggerNode = c.addNode(new Triggering());
            triggerNode.dataOnlyList.addAll(dependencyList);
        });
        Triggering triggering = sep.getNodeById("triggerNode");
        Assert.assertFalse(triggering.triggerInvoked);
        onEvent("test");
        Assert.assertTrue(triggering.triggerInvoked);
    }

    @Test
    public void noFailBuild_TriggerArrayNoEventDependency() throws NoSuchFieldException {
        DataOnly[] dependencyList = new DataOnly[]{
                new DataOnly(),
                new DataOnly(),
                new DataOnly()
        };
        sep(c -> {
            Triggering triggerNode = c.addNode(new Triggering());
            triggerNode.dataOnlyArray = dependencyList;
        });
        Triggering triggering = sep.getNodeById("triggerNode");
        Assert.assertFalse(triggering.triggerInvoked);
        onEvent("test");
        Assert.assertTrue(triggering.triggerInvoked);
    }

    @Test
    public void noFailBuild_TriggerScalarPush_NoEventDependency() throws NoSuchFieldException {
        sep(c -> {
            c.addNode(new Triggering()).pushRef = c.addNode(new DataOnly());
        });
        Triggering triggering = sep.getNodeById("triggerNode");
        Assert.assertFalse(triggering.triggerInvoked);
        onEvent("test");
        Assert.assertTrue(triggering.triggerInvoked);

    }

    public static class Triggering implements NamedNode {
        @Inject
        public DirtyStateMonitor dirtyStateMonitor;
        public DataOnly dataOnly;
        @SepNode
        public DefaultEventHandlerNode handler = new DefaultEventHandlerNode(String.class);
        @SepNode
        public List<DataOnly> dataOnlyList = new ArrayList<>();
        @SepNode
        public DataOnly[] dataOnlyArray;
        @PushReference
        @SepNode
        public DataOnly pushRef;
        public boolean triggerInvoked = false;

        @OnTrigger
        public boolean triggered() {
            triggerInvoked = true;
            return true;
        }

        @Override
        public String getName() {
            return "triggerNode";
        }
    }

    public static class DataOnly {

    }
}
