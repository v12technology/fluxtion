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
package com.fluxtion.example.core.building.factories.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.building.factories.FactoryNode;
import com.fluxtion.example.core.building.factories.FilteredDataHandler;
import com.fluxtion.example.shared.DataEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final FilteredDataHandler handler = new FilteredDataHandler("myTestFilter");
  public final FactoryNode factoryBuilt = new FactoryNode(handler);
  //Dirty flags

  //Filter constants

  public SampleProcessor() {
    handler.setLimit(2000);
  }

  @Override
  public void onEvent(com.fluxtion.api.event.Event event) {
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
    switch (typedEvent.filterString()) {
      case ("myTestFilter"):
        handler.dataEvent(typedEvent);
        factoryBuilt.update();
        afterEvent();
        return;
    }
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
