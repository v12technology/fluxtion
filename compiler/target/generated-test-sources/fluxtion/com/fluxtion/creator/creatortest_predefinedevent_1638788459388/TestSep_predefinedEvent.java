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
package com.fluxtion.creator.creatortest_predefinedevent_1638788459388;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;

import com.fluxtion.api.audit.Auditor;
import com.fluxtion.api.event.Event;
import com.fluxtion.creator.MyPredefinedEvent;
import com.fluxtion.creator.MyPredefinedNode;
import com.fluxtion.creator.TestAuditor;

/*
 * <pre>
 * generation time   : 2021-12-06T11:00:59.959541900
 * generator version : ${generator_version_information}
 * api version       : 2.11.2-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
public class TestSep_predefinedEvent implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  public final DataHandler dataHandler = new DataHandler();
  public final MyPredefinedNode myProcessor = new MyPredefinedNode();
  public final TestAuditor auditor = new TestAuditor();
  //Dirty flags
  private boolean isDirty_dataHandler = false;
  //Filter constants

  public TestSep_predefinedEvent() {
    myProcessor.parent = dataHandler;
    //node auditors
    initialiseAuditor(auditor);
  }

  @Override
  public void onEvent(Object event) {
    switch (event.getClass().getName()) {
      case ("com.fluxtion.creator.MyPredefinedEvent"):
        {
          MyPredefinedEvent typedEvent = (MyPredefinedEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(MyPredefinedEvent typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    auditInvocation(dataHandler, "dataHandler", "handlerMyPredefinedEvent", typedEvent);
    isDirty_dataHandler = dataHandler.handlerMyPredefinedEvent(typedEvent);
    if (isDirty_dataHandler) {
      auditInvocation(myProcessor, "myProcessor", "process", typedEvent);
      myProcessor.process();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void auditEvent(Object typedEvent) {
    auditor.eventReceived(typedEvent);
  }

  private void auditEvent(Event typedEvent) {
    auditor.eventReceived(typedEvent);
  }

  private void auditInvocation(Object node, String nodeName, String methodName, Object typedEvent) {
    auditor.nodeInvoked(node, nodeName, methodName, typedEvent);
  }

  private void initialiseAuditor(Auditor auditor) {
    auditor.init();
    auditor.nodeRegistered(myProcessor, "myProcessor");
    auditor.nodeRegistered(dataHandler, "dataHandler");
  }

  private void afterEvent() {
    auditor.processingComplete();
    isDirty_dataHandler = false;
  }

  @Override
  public void init() {
    dataHandler.init();
  }

  @Override
  public void tearDown() {
    auditor.tearDown();
    dataHandler.teardown();
  }

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
