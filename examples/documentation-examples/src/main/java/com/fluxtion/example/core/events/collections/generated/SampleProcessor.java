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
package com.fluxtion.example.core.events.collections.generated;

import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.collections.Aggregator;
import com.fluxtion.example.core.events.collections.ConfigHandler;
import com.fluxtion.example.shared.ConfigEvent;
import com.fluxtion.example.shared.DataEvent;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;
import java.util.Arrays;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final ConfigHandler configHandler_7 = new ConfigHandler();
  private final ConfigHandler configHandler_9 = new ConfigHandler();
  private final ConfigHandler configHandler_11 = new ConfigHandler();
  private final DataEventHandler dataEventHandler_5 = new DataEventHandler();
  private final MyEventHandler myEventHandler_1 = new MyEventHandler();
  private final MyEventHandler myEventHandler_3 = new MyEventHandler();
  private final Aggregator aggregator_13 =
      new Aggregator(
          new Object[] {myEventHandler_1, myEventHandler_3, dataEventHandler_5, configHandler_7},
          Arrays.asList(configHandler_9, configHandler_11));
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
      case ("com.fluxtion.example.shared.MyEvent"):
        {
          MyEvent typedEvent = (MyEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(ConfigEvent typedEvent) {
    //Default, no filter methods
    configHandler_7.cfgUpdate(typedEvent);
    aggregator_13.parentUdated(configHandler_7);
    configHandler_9.cfgUpdate(typedEvent);
    aggregator_13.parentCfgUdated(configHandler_9);
    configHandler_11.cfgUpdate(typedEvent);
    aggregator_13.parentCfgUdated(configHandler_11);
    aggregator_13.update();
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(DataEvent typedEvent) {
    //Default, no filter methods
    dataEventHandler_5.handleEvent(typedEvent);
    aggregator_13.parentUdated(dataEventHandler_5);
    aggregator_13.update();
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(MyEvent typedEvent) {
    //Default, no filter methods
    myEventHandler_1.handleEvent(typedEvent);
    aggregator_13.parentUdated(myEventHandler_1);
    myEventHandler_3.handleEvent(typedEvent);
    aggregator_13.parentUdated(myEventHandler_3);
    aggregator_13.update();
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
