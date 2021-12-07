/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.compiler.generation.audit;

import com.fluxtion.runtim.annotations.EventHandler;
import com.fluxtion.runtim.annotations.builder.Inject;
import com.fluxtion.runtim.audit.Auditor;
import com.fluxtion.runtim.event.Event;
import com.fluxtion.compiler.builder.node.NodeFactory;
import com.fluxtion.compiler.builder.node.NodeRegistry;
import com.fluxtion.compiler.generation.util.MultipleSepTargetInProcessTest;
import com.fluxtion.test.event.CharEvent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class FactoryAuditorTest extends MultipleSepTargetInProcessTest {

    public FactoryAuditorTest(boolean compiledSep) {
        super(compiledSep);
    }

    @Test
    public void test() {
        sep(c ->c.addNode(new ParentNode()));
        MyNode myNode = getField("myNode");
        Assert.assertTrue(myNode.registerCalled);
        onEvent(new CharEvent('a'));
        assertThat(myNode.eventAuditCount, is(1));
    }

    public static class ParentNode {

        @Inject
        public MyNode myNode;

        @EventHandler
        public void charEvent(CharEvent event) {

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