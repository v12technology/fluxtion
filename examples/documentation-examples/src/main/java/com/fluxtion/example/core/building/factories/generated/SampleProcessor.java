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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.building.factories.FactoryNode;
import com.fluxtion.example.core.building.factories.FilteredDataHandler;
import com.fluxtion.example.shared.DataEvent;

/*
 * <pre>
 * generation time   : 2020-03-28T17:52:38.813373800
 * generator version : 1.9.7-SNAPSHOT
 * api version       : 1.9.7-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final FilteredDataHandler handler = new FilteredDataHandler("myTestFilter");
  public final FactoryNode factoryBuilt = new FactoryNode(handler);
  //Dirty flags
  private boolean isDirty_handler = false;
  //Filter constants

  public SampleProcessor() {
    handler.setLimit(2000);
  }

  @Override
  public void onEvent(Object event) {
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
        //filtering for myTestFilter
      case ("myTestFilter"):
        isDirty_handler = true;
        handler.dataEvent(typedEvent);
        if (isDirty_handler) {
          factoryBuilt.update();
        }
        afterEvent();
        return;
    }
    afterEvent();
  }

  private void afterEvent() {

    isDirty_handler = false;
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
