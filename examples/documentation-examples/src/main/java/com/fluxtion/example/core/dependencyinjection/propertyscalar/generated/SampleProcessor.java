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
package com.fluxtion.example.core.dependencyinjection.propertyscalar.generated;

import com.fluxtion.runtime.lifecycle.BatchHandler;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.runtime.lifecycle.Lifecycle;
import com.fluxtion.example.shared.SampleEnum;
import com.fluxtion.example.core.dependencyinjection.propertyscalar.PropertyHandler;
import com.fluxtion.example.shared.MyEvent;

public class SampleProcessor implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final PropertyHandler propertyHandler_1 =
      new PropertyHandler(true, (byte) 0, 'a', (short) 0, 0.0f, 0, 0.0, 0L, "0", SampleEnum.MONDAY);
  //Dirty flags

  //Filter constants

  public SampleProcessor() {
    propertyHandler_1.setBooleanBeanProp(true);
    propertyHandler_1.setByteBeanProp((byte) 3);
    propertyHandler_1.setCharBeanProp('d');
    propertyHandler_1.setDoubleBeanProp(3.3);
    propertyHandler_1.setEnumBeanProp(SampleEnum.THURSDAY);
    propertyHandler_1.setFloatBeanProp(3.3f);
    propertyHandler_1.setIntBeanProp(3);
    propertyHandler_1.setLongBeanProp(3L);
    propertyHandler_1.setShortBeanProp((short) 3);
    propertyHandler_1.booleanPublicProp = (boolean) true;
    propertyHandler_1.bytePublicProp = (byte) 1;
    propertyHandler_1.charPublicProp = 'b';
    propertyHandler_1.shortPublicProp = (short) 1;
    propertyHandler_1.floatPublicProp = (float) 1.1;
    propertyHandler_1.intPublicProp = (int) 1;
    propertyHandler_1.doublePublicProp = (double) 1.1;
    propertyHandler_1.longPublicProp = (long) 1;
    propertyHandler_1.enumPublicProp = com.fluxtion.example.shared.SampleEnum.TUESDAY;
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
    propertyHandler_1.myEvent(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {}

  @Override
  public void init() {
    propertyHandler_1.init();
  }

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
