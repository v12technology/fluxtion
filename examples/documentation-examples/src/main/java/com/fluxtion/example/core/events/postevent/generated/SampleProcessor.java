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

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.postevent.ResetAfterEvent;
import com.fluxtion.example.core.events.postevent.ResetDataEvent;
import com.fluxtion.example.core.events.postevent.ResetGlobal;
import com.fluxtion.example.shared.ConfigEvent;
import com.fluxtion.example.shared.DataEvent;
import com.fluxtion.example.shared.DataEventHandler;

/*
 * <pre>
 * generation time   : 2020-02-21T11:40:10.086622
 * generator version : 1.9.1-SNAPSHOT
 * api version       : 1.9.1-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final DataEventHandler dataEventHandler_1 = new DataEventHandler();
  private final ResetDataEvent resetDataEvent_3 = new ResetDataEvent(dataEventHandler_1);
  private final ResetGlobal resetGlobal_5 = new ResetGlobal(resetDataEvent_3);
  private final ResetAfterEvent resetAfterEvent_7 = new ResetAfterEvent(resetGlobal_5);
  //Dirty flags
  private boolean isDirty_dataEventHandler_1 = false;
  private boolean isDirty_resetDataEvent_3 = false;
  private boolean isDirty_resetGlobal_5 = false;
  //Filter constants

  public SampleProcessor() {}

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
    }
  }

  public void handleEvent(ConfigEvent typedEvent) {
    //Default, no filter methods
    isDirty_resetGlobal_5 = true;
    resetGlobal_5.conifgUpdate(typedEvent);
    if (isDirty_resetDataEvent_3) {
      isDirty_resetGlobal_5 = true;
      resetGlobal_5.eventUpdate();
    }
    //event stack unwind callbacks
    if (isDirty_resetDataEvent_3) {
      resetGlobal_5.eventComplete();
    }
    afterEvent();
  }

  public void handleEvent(DataEvent typedEvent) {
    //Default, no filter methods
    isDirty_dataEventHandler_1 = true;
    dataEventHandler_1.handleEvent(typedEvent);
    if (isDirty_dataEventHandler_1) {
      isDirty_resetDataEvent_3 = true;
      resetDataEvent_3.eventUpdate();
    }
    if (isDirty_resetDataEvent_3) {
      isDirty_resetGlobal_5 = true;
      resetGlobal_5.eventUpdate();
    }
    //event stack unwind callbacks
    if (isDirty_dataEventHandler_1) {
      resetDataEvent_3.dataEventReset();
    }
    if (isDirty_resetDataEvent_3) {
      resetGlobal_5.eventComplete();
    }
    afterEvent();
  }

  @Override
  public void afterEvent() {
    resetAfterEvent_7.afterEvent();
    resetGlobal_5.afterEvent();
    isDirty_dataEventHandler_1 = false;
    isDirty_resetDataEvent_3 = false;
    isDirty_resetGlobal_5 = false;
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
