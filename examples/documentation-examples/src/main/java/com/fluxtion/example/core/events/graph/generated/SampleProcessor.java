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
package com.fluxtion.example.core.events.graph.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.graph.CombinerNode;
import com.fluxtion.example.shared.ChildNode;
import com.fluxtion.example.shared.DataEvent;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.shared.PipelineNode;

/*
 * <pre>
 * generation time   : 2020-02-23T15:48:43.602917900
 * generator version : 1.9.4-SNAPSHOT
 * api version       : 1.9.4-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final DataEventHandler dataEventHandler_1 = new DataEventHandler();
  private final MyEventHandler myEventHandler_7 = new MyEventHandler();
  private final CombinerNode combinerNode_9 =
      new CombinerNode(myEventHandler_7, dataEventHandler_1);
  private final PipelineNode pipelineNode_3 = new PipelineNode(dataEventHandler_1);
  private final ChildNode childNode_5 = new ChildNode(pipelineNode_3);
  //Dirty flags
  private boolean isDirty_dataEventHandler_1 = false;
  private boolean isDirty_myEventHandler_7 = false;
  private boolean isDirty_pipelineNode_3 = false;
  //Filter constants

  public SampleProcessor() {}

  @Override
  public void onEvent(Object event) {
    switch (event.getClass().getName()) {
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

  public void handleEvent(DataEvent typedEvent) {
    //Default, no filter methods
    isDirty_dataEventHandler_1 = true;
    dataEventHandler_1.handleEvent(typedEvent);
    if (isDirty_dataEventHandler_1 | isDirty_myEventHandler_7) {
      combinerNode_9.update();
    }
    if (isDirty_dataEventHandler_1) {
      isDirty_pipelineNode_3 = true;
      pipelineNode_3.update();
    }
    if (isDirty_pipelineNode_3) {
      childNode_5.recalculate();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(MyEvent typedEvent) {
    //Default, no filter methods
    isDirty_myEventHandler_7 = true;
    myEventHandler_7.handleEvent(typedEvent);
    if (isDirty_dataEventHandler_1 | isDirty_myEventHandler_7) {
      combinerNode_9.update();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {

    isDirty_dataEventHandler_1 = false;
    isDirty_myEventHandler_7 = false;
    isDirty_pipelineNode_3 = false;
  }

  @Override
  public void init() {}

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
