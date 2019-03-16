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
package com.fluxtion.example.core.outstyle.naming.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.outstyle.naming.DataHandler;
import com.fluxtion.example.shared.DataEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final DataHandler handler_FX = new DataHandler("FX");
  private final DataHandler handler_EQUITIES = new DataHandler("EQUITIES");
  private final DataHandler handler_BONDS = new DataHandler("BONDS");
  //Dirty flags
  private boolean isDirty_handler_BONDS = false;
  private boolean isDirty_handler_EQUITIES = false;
  private boolean isDirty_handler_FX = false;
  //Filter constants

  public SampleProcessor() {}

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
        //filtering for BONDS
      case ("BONDS"):
        isDirty_handler_BONDS = true;
        handler_BONDS.processUpdate(typedEvent);
        afterEvent();
        return;
        //filtering for EQUITIES
      case ("EQUITIES"):
        isDirty_handler_EQUITIES = true;
        handler_EQUITIES.processUpdate(typedEvent);
        afterEvent();
        return;
        //filtering for FX
      case ("FX"):
        isDirty_handler_FX = true;
        handler_FX.processUpdate(typedEvent);
        afterEvent();
        return;
    }
    afterEvent();
  }

  @Override
  public void afterEvent() {

    isDirty_handler_BONDS = false;
    isDirty_handler_EQUITIES = false;
    isDirty_handler_FX = false;
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
