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
package com.fluxtion.example.core.outstyle.mappeddispatch.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.api.lifecycle.FilteredHandlerInvoker;
import com.fluxtion.example.core.outstyle.naming.DataHandler;
import com.fluxtion.example.shared.DataEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashMap;

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
    FilteredHandlerInvoker invoker = dispatchStringMapDataEvent.get(typedEvent.filterString());
    if (invoker != null) {
      invoker.invoke(typedEvent);
      afterEvent();
      return;
    }
    afterEvent();
  }

  //int filter maps
  //String filter maps
  private final HashMap<String, FilteredHandlerInvoker> dispatchStringMapDataEvent =
      initdispatchStringMapDataEvent();

  private HashMap<String, FilteredHandlerInvoker> initdispatchStringMapDataEvent() {
    HashMap<String, FilteredHandlerInvoker> dispatchMap = new HashMap<>();
    dispatchMap.put(
        "BONDS",
        new FilteredHandlerInvoker() {

          @Override
          public void invoke(Object event) {
            handle_DataEvent_BONDS((com.fluxtion.example.shared.DataEvent) event);
          }
        });
    dispatchMap.put(
        "EQUITIES",
        new FilteredHandlerInvoker() {

          @Override
          public void invoke(Object event) {
            handle_DataEvent_EQUITIES((com.fluxtion.example.shared.DataEvent) event);
          }
        });
    dispatchMap.put(
        "FX",
        new FilteredHandlerInvoker() {

          @Override
          public void invoke(Object event) {
            handle_DataEvent_FX((com.fluxtion.example.shared.DataEvent) event);
          }
        });
    return dispatchMap;
  }

  private void handle_DataEvent_BONDS(com.fluxtion.example.shared.DataEvent typedEvent) {
    //method body - invoke call tree
    isDirty_handler_BONDS = true;
    handler_BONDS.processUpdate(typedEvent);
  }

  private void handle_DataEvent_EQUITIES(com.fluxtion.example.shared.DataEvent typedEvent) {
    //method body - invoke call tree
    isDirty_handler_EQUITIES = true;
    handler_EQUITIES.processUpdate(typedEvent);
  }

  private void handle_DataEvent_FX(com.fluxtion.example.shared.DataEvent typedEvent) {
    //method body - invoke call tree
    isDirty_handler_FX = true;
    handler_FX.processUpdate(typedEvent);
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
