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
package com.fluxtion.example.core.dependencyinjection.propertyvector.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.example.core.dependencyinjection.propertyvector.PropertyHandler;
import com.fluxtion.example.shared.MyEvent;
import com.fluxtion.example.shared.SampleEnum;
import java.util.Arrays;

/*
 * <pre>
 * generation time   : 2020-02-23T10:51:23.237937400
 * generator version : 1.9.4-SNAPSHOT
 * api version       : 1.9.4-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class SampleProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final PropertyHandler propertyHandler_1 =
      new PropertyHandler(
          new boolean[] {true, true, false},
          Arrays.asList(1, 2, 3, 4, 5),
          new String[] {"one", "two"});
  //Dirty flags

  //Filter constants

  public SampleProcessor() {
    propertyHandler_1.setBooleanBeanProp(new boolean[] {false, false, false, false});
    propertyHandler_1.setEnumBeanProp(
        new SampleEnum[] {SampleEnum.TUESDAY, SampleEnum.THURSDAY, SampleEnum.SATURDAY});
    propertyHandler_1.setIntBeanProp(Arrays.asList(1, 2, 3, 4, 5));
    propertyHandler_1.setStringBeanProp(Arrays.asList("AA", "BB", "CC"));
    propertyHandler_1.intPublicProp = new int[4];
    propertyHandler_1.intPublicProp[0] = 100;
    propertyHandler_1.intPublicProp[1] = 200;
    propertyHandler_1.intPublicProp[2] = 300;
    propertyHandler_1.intPublicProp[3] = 400;
    propertyHandler_1.stringPublicProp.add("1");
    propertyHandler_1.stringPublicProp.add("2");
    propertyHandler_1.stringPublicProp.add("3");
    propertyHandler_1.stringPublicProp.add("4");
    propertyHandler_1.enumPublioProp.add(SampleEnum.SUNDAY);
    propertyHandler_1.enumPublioProp.add(SampleEnum.MONDAY);
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
    propertyHandler_1.myEvent(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {}

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
