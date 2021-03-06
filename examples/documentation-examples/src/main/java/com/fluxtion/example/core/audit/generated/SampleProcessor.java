/*
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.example.core.audit.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.api.audit.Auditor;
import com.fluxtion.api.event.Event;
import com.fluxtion.example.core.audit.Combiner;
import com.fluxtion.example.core.audit.NodeAuditor;
import com.fluxtion.example.shared.ChildNode;
import com.fluxtion.example.shared.ConfigEvent;
import com.fluxtion.example.shared.DataEvent;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.shared.PipelineNode;

/*
 * <pre>
 * generation time   : 2020-03-28T17:52:27.519192100
 * generator version : 1.9.7-SNAPSHOT
 * api version       : 1.9.7-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final DataEventHandler dataEventHandler_5 = new DataEventHandler();
  private final MyEventHandler myEventHandler_1 = new MyEventHandler();
  private final ChildNode childNode_3 = new ChildNode(myEventHandler_1);
  private final PipelineNode pipelineNode_7 = new PipelineNode(dataEventHandler_5);
  private final Combiner combiner_9 = new Combiner(childNode_3, pipelineNode_7);
  public final NodeAuditor nodeAuditor = new NodeAuditor();
  //Dirty flags
  private boolean isDirty_childNode_3 = false;
  private boolean isDirty_dataEventHandler_5 = false;
  private boolean isDirty_myEventHandler_1 = false;
  private boolean isDirty_pipelineNode_7 = false;
  //Filter constants

  public SampleProcessor() {
    //node auditors
    initialiseAuditor(nodeAuditor);
  }

  @Override
  public void onEvent(Object event) {
    switch (event.getClass().getName()) {
      case ("com.fluxtion.example.shared.ConfigEvent"):
        {
          ConfigEvent typedEvent = (ConfigEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.example.shared.DataEvent"):
        {
          DataEvent typedEvent = (DataEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.example.shared.MyEvent"):
        {
          MyEvent typedEvent = (MyEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(ConfigEvent typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    auditInvocation(combiner_9, "combiner_9", "processConfig", typedEvent);
    combiner_9.processConfig(typedEvent);
    if (isDirty_childNode_3 | isDirty_pipelineNode_7) {
      auditInvocation(combiner_9, "combiner_9", "onEvent", typedEvent);
      combiner_9.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(DataEvent typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    auditInvocation(dataEventHandler_5, "dataEventHandler_5", "handleEvent", typedEvent);
    isDirty_dataEventHandler_5 = true;
    dataEventHandler_5.handleEvent(typedEvent);
    if (isDirty_dataEventHandler_5) {
      auditInvocation(pipelineNode_7, "pipelineNode_7", "update", typedEvent);
      isDirty_pipelineNode_7 = true;
      pipelineNode_7.update();
    }
    if (isDirty_childNode_3 | isDirty_pipelineNode_7) {
      auditInvocation(combiner_9, "combiner_9", "onEvent", typedEvent);
      combiner_9.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(MyEvent typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    auditInvocation(myEventHandler_1, "myEventHandler_1", "handleEvent", typedEvent);
    isDirty_myEventHandler_1 = true;
    myEventHandler_1.handleEvent(typedEvent);
    if (isDirty_myEventHandler_1) {
      auditInvocation(childNode_3, "childNode_3", "recalculate", typedEvent);
      isDirty_childNode_3 = true;
      childNode_3.recalculate();
    }
    if (isDirty_childNode_3 | isDirty_pipelineNode_7) {
      auditInvocation(combiner_9, "combiner_9", "onEvent", typedEvent);
      combiner_9.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void auditEvent(Object typedEvent) {
    nodeAuditor.eventReceived(typedEvent);
  }

  private void auditEvent(Event typedEvent) {
    nodeAuditor.eventReceived(typedEvent);
  }

  private void auditInvocation(Object node, String nodeName, String methodName, Object typedEvent) {
    nodeAuditor.nodeInvoked(node, nodeName, methodName, typedEvent);
  }

  private void initialiseAuditor(Auditor auditor) {
    auditor.init();
    auditor.nodeRegistered(combiner_9, "combiner_9");
    auditor.nodeRegistered(childNode_3, "childNode_3");
    auditor.nodeRegistered(dataEventHandler_5, "dataEventHandler_5");
    auditor.nodeRegistered(myEventHandler_1, "myEventHandler_1");
    auditor.nodeRegistered(pipelineNode_7, "pipelineNode_7");
  }

  private void afterEvent() {
    nodeAuditor.processingComplete();
    isDirty_childNode_3 = false;
    isDirty_dataEventHandler_5 = false;
    isDirty_myEventHandler_1 = false;
    isDirty_pipelineNode_7 = false;
  }

  @Override
  public void init() {}

  @Override
  public void tearDown() {
    nodeAuditor.tearDown();
  }

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
