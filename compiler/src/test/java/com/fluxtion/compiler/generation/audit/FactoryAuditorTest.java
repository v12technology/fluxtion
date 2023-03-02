/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.audit;

import com.fluxtion.compiler.builder.factory.NodeFactory;
import com.fluxtion.compiler.builder.factory.NodeRegistry;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.audit.Auditor;
import com.fluxtion.runtime.event.Event;
import com.fluxtion.test.event.CharEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FactoryAuditorTest extends MultipleSepTargetInProcessTest {

    public FactoryAuditorTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void test() {
        sep(c -> c.addNode(new ParentNode(), "parent"));
        MyNode myNode = getAuditor("myNode");
        Assert.assertTrue(myNode.registerCalled);
        onEvent(new CharEvent('a'));
        assertThat(myNode.eventAuditCount, is(1));
    }

    public static class ParentNode {

        @Inject
        public MyNode myNode;

        @OnEventHandler
        public boolean charEvent(CharEvent event) {
            return true;
        }
    }

    public static class MyNode implements Auditor {

        public boolean registerCalled = false;
        int eventAuditCount;

        @Override
        public void eventReceived(Object event) {
            eventAuditCount++;
        }

        @Override
        public void eventReceived(Event event) {
            eventAuditCount++;
        }

        @Override
        public void nodeRegistered(Object node, String nodeName) {
            registerCalled = true;
        }

    }

    public static class MyNodeFactory implements NodeFactory<MyNode> {

        @Override
        public MyNode createNode(Map config, NodeRegistry registry) {
            final MyNode myNode = new MyNode();
            registry.registerAuditor(myNode, "myNode");
            return myNode;
        }

    }

}
