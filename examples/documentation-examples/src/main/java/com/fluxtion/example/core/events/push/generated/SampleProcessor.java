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
package com.fluxtion.example.core.events.push.generated;

import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.core.events.push.CacheWriter;
import com.fluxtion.example.core.events.push.Cache;
import com.fluxtion.example.core.events.push.CacheReader;
import com.fluxtion.example.shared.MyEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final MyEventHandler myEventHandler_1 = new MyEventHandler();
  private final CacheWriter cacheWriter_7 = new CacheWriter(myEventHandler_1);
  private final Cache cache_3 = new Cache();
  private final CacheReader cacheReader_5 = new CacheReader(cache_3, myEventHandler_1);
  //Dirty flags

  //Filter constants

  public SampleProcessor() {
    cacheWriter_7.cache = cache_3;
  }

  @Override
  public void onEvent(com.fluxtion.runtime.event.Event event) {
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
    myEventHandler_1.handleEvent(typedEvent);
    cacheWriter_7.pushToCache();
    cache_3.reconcileCache();
    cacheReader_5.readFromCache();
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
