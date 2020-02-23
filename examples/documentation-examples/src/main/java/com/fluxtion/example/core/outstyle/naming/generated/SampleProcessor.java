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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.outstyle.naming.DataHandler;
import com.fluxtion.example.shared.DataEvent;

/*
 * <pre>
 * generation time   : 2020-02-23T15:48:45.608921800
 * generator version : 1.9.4-SNAPSHOT
 * api version       : 1.9.4-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final DataHandler handler_FX = new DataHandler("FX");
  private final DataHandler handler_EQUITIES = new DataHandler("EQUITIES");
  private final DataHandler handler_BONDS = new DataHandler("BONDS");
  //Dirty flags

  //Filter constants

  public SampleProcessor() {}

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
        //filtering for BONDS
      case ("BONDS"):
        handler_BONDS.processUpdate(typedEvent);
        afterEvent();
        return;
        //filtering for EQUITIES
      case ("EQUITIES"):
        handler_EQUITIES.processUpdate(typedEvent);
        afterEvent();
        return;
        //filtering for FX
      case ("FX"):
        handler_FX.processUpdate(typedEvent);
        afterEvent();
        return;
    }
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
