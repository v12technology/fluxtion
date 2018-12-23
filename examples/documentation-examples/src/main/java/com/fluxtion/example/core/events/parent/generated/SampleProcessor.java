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
package com.fluxtion.example.core.events.parent.generated;

import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.core.events.parent.ParentIdentifier;
import com.fluxtion.example.shared.DataEvent;
import com.fluxtion.example.shared.MyEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final DataEventHandler dataEventHandler_1 = new DataEventHandler();
  private final DataEventHandler dataEventHandler_3 = new DataEventHandler();
  private final MyEventHandler myEventHandler_5 = new MyEventHandler();
  private final ParentIdentifier parentIdentifier_7 =
      new ParentIdentifier(dataEventHandler_1, dataEventHandler_3, myEventHandler_5);
  //Dirty flags

  //Filter constants

  public SampleProcessor() {}

  @Override
  public void onEvent(com.fluxtion.runtime.event.Event event) {
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
    dataEventHandler_1.handleEvent(typedEvent);
    parentIdentifier_7.dataHandler_1_changed(dataEventHandler_1);
    dataEventHandler_3.handleEvent(typedEvent);
    parentIdentifier_7.dataHandler_2_changed(dataEventHandler_3);
    parentIdentifier_7.process();
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(MyEvent typedEvent) {
    //Default, no filter methods
    myEventHandler_5.handleEvent(typedEvent);
    parentIdentifier_7.myEventHandler_changed(myEventHandler_5);
    parentIdentifier_7.process();
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {}

  @Override
  public void init() {}

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
