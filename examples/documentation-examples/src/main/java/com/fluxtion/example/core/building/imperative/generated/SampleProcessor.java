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
package com.fluxtion.example.core.building.imperative.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.building.imperative.PropertySubNode;
import com.fluxtion.example.core.building.imperative.SubNode;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;

/*
 * <pre>
 * generation time   : 2020-02-22T09:01:03.631
 * generator version : ${generator_version_information}
 * api version       : ${api_version_information}
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final MyEventHandler myEventHandler_1 = new MyEventHandler();
  public final SubNode subNode = new SubNode(myEventHandler_1);
  public final PropertySubNode propNode = new PropertySubNode();
  //Dirty flags
  private boolean isDirty_myEventHandler_1 = false;
  //Filter constants

  public SampleProcessor() {
    propNode.setMySubNode(subNode);
    propNode.someParent = myEventHandler_1;
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
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {

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
