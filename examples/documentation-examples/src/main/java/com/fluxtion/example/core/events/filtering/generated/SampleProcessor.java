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
package com.fluxtion.example.core.events.filtering.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.filtering.MyEventProcessor;
import com.fluxtion.example.shared.ConfigEvent;
import com.fluxtion.example.shared.MyEvent;

/*
 * <pre>
 * generation time   : 2020-03-28T17:52:34.067725
 * generator version : 1.9.7-SNAPSHOT
 * api version       : 1.9.7-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final MyEventProcessor myEventProcessor_1 = new MyEventProcessor("cfg.acl");
  //Dirty flags

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
      case ("com.fluxtion.example.shared.MyEvent"):
        {
          MyEvent typedEvent = (MyEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(ConfigEvent typedEvent) {
    switch (typedEvent.filterString()) {
        //Event Class:[com.fluxtion.example.shared.ConfigEvent] filterString:[cfg.acl]
      case ("cfg.acl"):
        myEventProcessor_1.handleConfigEvent(typedEvent);
        myEventProcessor_1.handleMyVariableConfig(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.example.shared.ConfigEvent] filterString:[java.util.Date]
      case ("java.util.Date"):
        myEventProcessor_1.dateConfig(typedEvent);
        myEventProcessor_1.handleConfigEvent(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.example.shared.ConfigEvent] filterString:[maxConnection]
      case ("maxConnection"):
        myEventProcessor_1.handleConfigEvent(typedEvent);
        myEventProcessor_1.handleMaxConnectionsConfig(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.example.shared.ConfigEvent] filterString:[timeout]
      case ("timeout"):
        myEventProcessor_1.handleConfigEvent(typedEvent);
        myEventProcessor_1.handleTimeoutConfig(typedEvent);
        afterEvent();
        return;
    }
    //Default, no filter methods
    myEventProcessor_1.handleConfigEvent(typedEvent);
    myEventProcessor_1.unHandledConfig(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(MyEvent typedEvent) {
    //Default, no filter methods
    myEventProcessor_1.handleEvent(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {}

  @Override
  public void init() {}

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
