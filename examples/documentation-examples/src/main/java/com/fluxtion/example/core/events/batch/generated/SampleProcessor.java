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
package com.fluxtion.example.core.events.batch.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.batch.BatchNode;
import com.fluxtion.example.core.events.batch.DataHandler;
import com.fluxtion.example.shared.ChildNode;
import com.fluxtion.example.shared.DataEvent;

/*
 * <pre>
 * generation time   : 2020-02-23T10:51:23.902361500
 * generator version : 1.9.4-SNAPSHOT
 * api version       : 1.9.4-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final DataHandler dataHandler_1 = new DataHandler();
  private final BatchNode batchNode_3 = new BatchNode(dataHandler_1);
  private final ChildNode childNode_5 = new ChildNode(batchNode_3);
  //Dirty flags
  private boolean isDirty_dataHandler_1 = false;
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
    }
  }

  public void handleEvent(DataEvent typedEvent) {
    //Default, no filter methods
    isDirty_dataHandler_1 = true;
    dataHandler_1.dataEvent(typedEvent);
    if (isDirty_dataHandler_1) {
      childNode_5.recalculate();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {

    isDirty_dataHandler_1 = false;
  }

  @Override
  public void init() {}

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {
    batchNode_3.batchPause();
    dataHandler_1.batchPause();
  }

  @Override
  public void batchEnd() {
    batchNode_3.batchEnd();
    dataHandler_1.batchEnd();
  }
}
