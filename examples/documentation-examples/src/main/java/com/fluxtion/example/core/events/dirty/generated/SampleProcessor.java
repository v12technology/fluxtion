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
package com.fluxtion.example.core.events.dirty.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.dirty.DirtyAggregator;
import com.fluxtion.example.core.events.dirty.DirtyNode;
import com.fluxtion.example.shared.DataEvent;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;

/*
 * <pre>
 * generation time   : 2020-03-28T17:52:33.603213700
 * generator version : 1.9.7-SNAPSHOT
 * api version       : 1.9.7-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final DataEventHandler dataEventHandler_1 = new DataEventHandler();
  private final DirtyNode dirtyNode_5 = new DirtyNode(dataEventHandler_1);
  private final DirtyNode dirtyNode_7 = new DirtyNode(dataEventHandler_1);
  private final MyEventHandler myEventHandler_3 = new MyEventHandler();
  private final DirtyNode dirtyNode_9 = new DirtyNode(myEventHandler_3);
  private final DirtyAggregator dirtyAggregator_11 =
      new DirtyAggregator(dirtyNode_5, dirtyNode_7, dirtyNode_9);
  //Dirty flags
  private boolean isDirty_dataEventHandler_1 = false;
  private boolean isDirty_dirtyNode_5 = false;
  private boolean isDirty_dirtyNode_7 = false;
  private boolean isDirty_dirtyNode_9 = false;
  private boolean isDirty_myEventHandler_3 = false;
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
    if (isDirty_dataEventHandler_1) {
      isDirty_dirtyNode_5 = dirtyNode_5.isDirty();
    }
    if (isDirty_dataEventHandler_1) {
      isDirty_dirtyNode_7 = dirtyNode_7.isDirty();
    }
    if (isDirty_dirtyNode_5 | isDirty_dirtyNode_7 | isDirty_dirtyNode_9) {
      dirtyAggregator_11.publishDirty();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(MyEvent typedEvent) {
    //Default, no filter methods
    isDirty_myEventHandler_3 = true;
    myEventHandler_3.handleEvent(typedEvent);
    if (isDirty_myEventHandler_3) {
      isDirty_dirtyNode_9 = dirtyNode_9.isDirty();
    }
    if (isDirty_dirtyNode_5 | isDirty_dirtyNode_7 | isDirty_dirtyNode_9) {
      dirtyAggregator_11.publishDirty();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {

    isDirty_dataEventHandler_1 = false;
    isDirty_dirtyNode_5 = false;
    isDirty_dirtyNode_7 = false;
    isDirty_dirtyNode_9 = false;
    isDirty_myEventHandler_3 = false;
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
