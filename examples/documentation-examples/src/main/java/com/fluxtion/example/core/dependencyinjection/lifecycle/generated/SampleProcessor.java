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
package com.fluxtion.example.core.dependencyinjection.lifecycle.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.dependencyinjection.lifecycle.CleanListener;
import com.fluxtion.example.core.dependencyinjection.lifecycle.ConditioningHandler;
import com.fluxtion.example.core.dependencyinjection.lifecycle.DirtyCleanCombiner;
import com.fluxtion.example.core.dependencyinjection.lifecycle.DirtyListener;
import com.fluxtion.example.shared.MyEvent;

/*
 * <pre>
 * generation time   : 2020-03-10T23:46:56.230518600
 * generator version : 1.9.6-SNAPSHOT
 * api version       : 1.9.6-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final ConditioningHandler conditioningHandler_1 = new ConditioningHandler();
  private final CleanListener cleanListener_3 = new CleanListener(conditioningHandler_1);
  private final DirtyCleanCombiner dirtyCleanCombiner_7 =
      new DirtyCleanCombiner(cleanListener_3, cleanListener_3);
  private final DirtyListener dirtyListener_5 = new DirtyListener(conditioningHandler_1);
  //Dirty flags
  private boolean isDirty_cleanListener_3 = false;
  private boolean isDirty_conditioningHandler_1 = false;
  private boolean notisDirty_conditioningHandler_1 = false;
  //Filter constants

  public SampleProcessor() {}

  @Override
  public void onEvent(Object event) {
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
    notisDirty_conditioningHandler_1 = !isDirty_conditioningHandler_1;
    if (notisDirty_conditioningHandler_1) {
      isDirty_cleanListener_3 = true;
      cleanListener_3.noChangeUpdate();
    }
    if (isDirty_cleanListener_3) {
      dirtyCleanCombiner_7.changeUpdate();
    }
    if (isDirty_conditioningHandler_1) {
      dirtyListener_5.changeUpdate();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {

    isDirty_cleanListener_3 = false;
    isDirty_conditioningHandler_1 = false;
    notisDirty_conditioningHandler_1 = false;
  }

  @Override
  public void init() {
    conditioningHandler_1.init();
    cleanListener_3.init();
    dirtyCleanCombiner_7.init();
  }

  @Override
  public void tearDown() {
    dirtyListener_5.tearDown();
    dirtyCleanCombiner_7.tearDown();
    conditioningHandler_1.tearDown();
  }

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
