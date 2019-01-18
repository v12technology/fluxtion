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
package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.declarative.api.log.LogControlEvent;
import com.fluxtion.ext.futext.api.csv.ValidationLogSink;
import com.fluxtion.ext.futext.api.csv.ValidationLogger;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.fluxtion.ext.futext.api.event.RegisterEventHandler;
import com.fluxtion.ext.futext.api.util.EventPublsher;
import com.fluxtion.ext.futext.builder.math.CountFunction;

public class FlightDelayAnalyser implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final FlightDetailsCsvMarshaller0 flightDetailsCsvMarshaller0_0 =
      new FlightDetailsCsvMarshaller0();
  private final EventPublsher eventPublsher_1 = new EventPublsher();
  private final GreaterThanDecorator_2 greaterThanDecorator_2_2 = new GreaterThanDecorator_2();
  public final GroupBy_7 carrierDelayMap = new GroupBy_7();
  public final CountFunction totalFlights = new CountFunction();
  private final ValidationLogger validationLogger_5 = new ValidationLogger("validationLog");
  private final ValidationLogSink validationLogSink_6 = new ValidationLogSink("validationLogSink");
  //Dirty flags
  private boolean isDirty_flightDetailsCsvMarshaller0_0 = false;
  private boolean isDirty_greaterThanDecorator_2_2 = false;
  //Filter constants

  public FlightDelayAnalyser() {
    validationLogger_5.logSink = validationLogSink_6;
    eventPublsher_1.publishOnValidate = (boolean) false;
    totalFlights.tracked = flightDetailsCsvMarshaller0_0;
    flightDetailsCsvMarshaller0_0.errorLog = validationLogger_5;
    greaterThanDecorator_2_2.filterSubject = flightDetailsCsvMarshaller0_0;
    greaterThanDecorator_2_2.source_FlightDetailsCsvMarshaller0_1 = flightDetailsCsvMarshaller0_0;
    carrierDelayMap.greaterThanDecorator_20 = greaterThanDecorator_2_2;
  }

  @Override
  public void onEvent(com.fluxtion.api.event.Event event) {
    switch (event.eventId()) {
      case (CharEvent.ID):
        {
          CharEvent typedEvent = (CharEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case (EofEvent.ID):
        {
          EofEvent typedEvent = (EofEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
    switch (event.getClass().getName()) {
      case ("com.fluxtion.ext.declarative.api.log.LogControlEvent"):
        {
          LogControlEvent typedEvent = (LogControlEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.ext.futext.api.event.RegisterEventHandler"):
        {
          RegisterEventHandler typedEvent = (RegisterEventHandler) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(LogControlEvent typedEvent) {
    switch (typedEvent.filterString()) {
      case ("CHANGE_LOG_PROVIDER"):
        validationLogSink_6.controlLogProvider(typedEvent);
        afterEvent();
        return;
    }
    afterEvent();
  }

  public void handleEvent(CharEvent typedEvent) {
    //Default, no filter methods
    isDirty_flightDetailsCsvMarshaller0_0 = flightDetailsCsvMarshaller0_0.charEvent(typedEvent);
    if (isDirty_flightDetailsCsvMarshaller0_0) {
      eventPublsher_1.wrapperUpdate(flightDetailsCsvMarshaller0_0);
    }
    if (isDirty_flightDetailsCsvMarshaller0_0) {
      isDirty_greaterThanDecorator_2_2 = greaterThanDecorator_2_2.onEvent();
      if (isDirty_greaterThanDecorator_2_2) {
        carrierDelayMap.updategreaterThanDecorator_20(greaterThanDecorator_2_2);
      }
    }
    if (isDirty_flightDetailsCsvMarshaller0_0) {
      totalFlights.increment();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(EofEvent typedEvent) {
    //Default, no filter methods
    isDirty_flightDetailsCsvMarshaller0_0 = flightDetailsCsvMarshaller0_0.eof(typedEvent);
    if (isDirty_flightDetailsCsvMarshaller0_0) {
      eventPublsher_1.wrapperUpdate(flightDetailsCsvMarshaller0_0);
    }
    if (isDirty_flightDetailsCsvMarshaller0_0) {
      isDirty_greaterThanDecorator_2_2 = greaterThanDecorator_2_2.onEvent();
      if (isDirty_greaterThanDecorator_2_2) {
        carrierDelayMap.updategreaterThanDecorator_20(greaterThanDecorator_2_2);
      }
    }
    if (isDirty_flightDetailsCsvMarshaller0_0) {
      totalFlights.increment();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(RegisterEventHandler typedEvent) {
    //Default, no filter methods
    eventPublsher_1.registerEventHandler(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {

    isDirty_flightDetailsCsvMarshaller0_0 = false;
    isDirty_greaterThanDecorator_2_2 = false;
  }

  @Override
  public void init() {
    flightDetailsCsvMarshaller0_0.init();
    eventPublsher_1.init();
    greaterThanDecorator_2_2.init();
    carrierDelayMap.init();
    totalFlights.init();
    validationLogSink_6.init();
  }

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
