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
package com.fluxtion.example.core.events.postevent.generated;

import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.core.events.postevent.ResetDataEvent;
import com.fluxtion.example.core.events.postevent.ResetGlobal;
import com.fluxtion.example.core.events.postevent.ResetAfterEvent;
import com.fluxtion.example.shared.ConfigEvent;
import com.fluxtion.example.shared.DataEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final DataEventHandler dataEventHandler_1 = new DataEventHandler();
  private final ResetDataEvent resetDataEvent_3 = new ResetDataEvent(dataEventHandler_1);
  private final ResetGlobal resetGlobal_5 = new ResetGlobal(resetDataEvent_3);
  private final ResetAfterEvent resetAfterEvent_7 = new ResetAfterEvent(resetGlobal_5);
  //Dirty flags

  //Filter constants

  public SampleProcessor() {}

  @Override
  public void onEvent(com.fluxtion.runtime.event.Event event) {
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
    }
  }

  public void handleEvent(ConfigEvent typedEvent) {
    //Default, no filter methods
    resetGlobal_5.conifgUpdate(typedEvent);
    resetGlobal_5.eventUpdate();
    //event stack unwind callbacks
    resetGlobal_5.eventComplete();
    afterEvent();
  }

  public void handleEvent(DataEvent typedEvent) {
    //Default, no filter methods
    dataEventHandler_1.handleEvent(typedEvent);
    resetDataEvent_3.eventUpdate();
    resetGlobal_5.eventUpdate();
    //event stack unwind callbacks
    resetDataEvent_3.dataEventReset();
    resetGlobal_5.eventComplete();
    afterEvent();
  }

  @Override
  public void afterEvent() {
    resetAfterEvent_7.afterEvent();
    resetGlobal_5.afterEvent();
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
