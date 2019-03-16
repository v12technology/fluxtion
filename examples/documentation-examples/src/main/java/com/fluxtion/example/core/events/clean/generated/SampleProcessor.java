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
package com.fluxtion.example.core.events.clean.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.clean.CleanListener;
import com.fluxtion.example.core.events.clean.ConditioningHandler;
import com.fluxtion.example.core.events.clean.DirtyCleanListener;
import com.fluxtion.example.core.events.clean.DirtyListener;
import com.fluxtion.example.shared.MyEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final ConditioningHandler conditioningHandler_1 = new ConditioningHandler();
  private final CleanListener cleanListener_3 = new CleanListener(conditioningHandler_1);
  private final DirtyCleanListener dirtyCleanListener_7 =
      new DirtyCleanListener(conditioningHandler_1);
  private final DirtyListener dirtyListener_5 = new DirtyListener(conditioningHandler_1);
  //Dirty flags
  private boolean isDirty_cleanListener_3 = false;
  private boolean isDirty_conditioningHandler_1 = false;
  private boolean isDirty_dirtyCleanListener_7 = false;
  private boolean isDirty_dirtyListener_5 = false;
  //Filter constants

  public SampleProcessor() {}

  @Override
  public void onEvent(com.fluxtion.api.event.Event event) {
    switch (event.getClass().getName()) {
      case ("com.fluxtion.example.shared.MyEvent"):
        {
          MyEvent typedEvent = (MyEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(MyEvent typedEvent) {
    //Default, no filter methods
    isDirty_conditioningHandler_1 = conditioningHandler_1.onEvent(typedEvent);
    if (!isDirty_conditioningHandler_1) {
      isDirty_cleanListener_3 = true;
      cleanListener_3.noChangeUpdate();
    }
    if (isDirty_conditioningHandler_1) {
      isDirty_dirtyCleanListener_7 = true;
      dirtyCleanListener_7.changeUpdate();
    }
    if (!isDirty_conditioningHandler_1) {
      isDirty_dirtyCleanListener_7 = true;
      dirtyCleanListener_7.noChangeUpdate();
    }
    if (isDirty_conditioningHandler_1) {
      isDirty_dirtyListener_5 = true;
      dirtyListener_5.changeUpdate();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {

    isDirty_cleanListener_3 = false;
    isDirty_conditioningHandler_1 = false;
    isDirty_dirtyCleanListener_7 = false;
    isDirty_dirtyListener_5 = false;
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
