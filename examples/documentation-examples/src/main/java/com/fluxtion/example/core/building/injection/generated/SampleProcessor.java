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
package com.fluxtion.example.core.building.injection.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.building.injection.FilteredDataHandler;
import com.fluxtion.example.core.building.injection.InjectingDataProcessor;
import com.fluxtion.example.shared.DataEvent;

/*
 * <pre>
 * generation time   : 2020-02-21T13:34:32.340745
 * generator version : 1.9.1-SNAPSHOT
 * api version       : 1.9.1-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final FilteredDataHandler filteredDataHandler_2 =
      new FilteredDataHandler("myfilter_string");
  private final InjectingDataProcessor injectingDataProcessor_1 =
      new InjectingDataProcessor("myfilter_string");
  //Dirty flags
  private boolean isDirty_filteredDataHandler_2 = false;
  //Filter constants

  public SampleProcessor() {
    filteredDataHandler_2.setLimit(150);
    injectingDataProcessor_1.filter = filteredDataHandler_2;
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
        //filtering for myfilter_string
      case ("myfilter_string"):
        isDirty_filteredDataHandler_2 = true;
        filteredDataHandler_2.dataEvent(typedEvent);
        afterEvent();
        return;
    }
    afterEvent();
  }

  @Override
  public void afterEvent() {

    isDirty_filteredDataHandler_2 = false;
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
