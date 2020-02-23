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
package com.fluxtion.example.core.dependencyinjection.reflection.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.dependencyinjection.reflection.FactoryNode;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;

/*
 * <pre>
 * generation time   : 2020-02-23T15:48:41.922920700
 * generator version : 1.9.4-SNAPSHOT
 * api version       : 1.9.4-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  final net.vidageek.mirror.dsl.Mirror constructor = new net.vidageek.mirror.dsl.Mirror();
  private final MyEventHandler myEventHandler_1 = new MyEventHandler();
  private final FactoryNode factoryNode_3 =
      constructor.on(FactoryNode.class).invoke().constructor().bypasser();
  //Dirty flags
  private boolean isDirty_myEventHandler_1 = false;
  //Filter constants

  public SampleProcessor() {
    final net.vidageek.mirror.dsl.Mirror assigner = new net.vidageek.mirror.dsl.Mirror();
    assigner.on(factoryNode_3).set().field("parent").withValue(myEventHandler_1);
    assigner.on(factoryNode_3).set().field("limit").withValue((int) 10000);
  }

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
    isDirty_myEventHandler_1 = true;
    myEventHandler_1.handleEvent(typedEvent);
    if (isDirty_myEventHandler_1) {
      factoryNode_3.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {

    isDirty_myEventHandler_1 = false;
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
