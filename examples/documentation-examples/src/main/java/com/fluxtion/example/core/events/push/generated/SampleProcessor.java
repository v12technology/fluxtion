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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.events.push.Cache;
import com.fluxtion.example.core.events.push.CacheReader;
import com.fluxtion.example.core.events.push.CacheWriter;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.MyEventHandler;

/*
 * <pre>
 * generation time   : 2020-02-23T10:51:26.753453500
 * generator version : 1.9.4-SNAPSHOT
 * api version       : 1.9.4-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final MyEventHandler myEventHandler_1 = new MyEventHandler();
  private final CacheWriter cacheWriter_7 = new CacheWriter(myEventHandler_1);
  private final Cache cache_3 = new Cache();
  private final CacheReader cacheReader_5 = new CacheReader(cache_3, myEventHandler_1);
  //Dirty flags
  private boolean isDirty_cacheWriter_7 = false;
  private boolean isDirty_cache_3 = false;
  private boolean isDirty_myEventHandler_1 = false;
  //Filter constants

  public SampleProcessor() {
    cacheWriter_7.cache = cache_3;
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
    if (isDirty_myEventHandler_1) {
      isDirty_cacheWriter_7 = true;
      cacheWriter_7.pushToCache();
    }
    if (isDirty_cacheWriter_7) {
      isDirty_cache_3 = true;
      cache_3.reconcileCache();
    }
    if (isDirty_cache_3 | isDirty_myEventHandler_1) {
      cacheReader_5.readFromCache();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {

    isDirty_cacheWriter_7 = false;
    isDirty_cache_3 = false;
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
