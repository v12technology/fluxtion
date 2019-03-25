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
import com.fluxtion.ext.declarative.api.stream.StreamFunctions.Count;
import com.fluxtion.ext.futext.api.csv.ValidationLogSink;
import com.fluxtion.ext.futext.api.csv.ValidationLogger;
import com.fluxtion.ext.futext.api.event.CharEvent;
import com.fluxtion.ext.futext.api.event.EofEvent;
import com.fluxtion.ext.futext.api.event.RegisterEventHandler;
import com.fluxtion.ext.futext.api.util.EventPublsher;

public class FlightDelayAnalyser implements EventHandler, BatchHandler, Lifecycle {

  //Node declarations
  private final Count count_4 = new Count();
  private final FlightDetailsCsvDecoder0 flightDetailsCsvDecoder0_0 =
      new FlightDetailsCsvDecoder0();
  private final EventPublsher eventPublsher_1 = new EventPublsher();
  private final GreaterThanDecorator_2 greaterThanDecorator_2_2 = new GreaterThanDecorator_2();
  public final GroupBy_7 carrierDelayMap = new GroupBy_7();
  public final Map_FlightDetails_By_increment_8 totalFlights =
      new Map_FlightDetails_By_increment_8();
  private final ValidationLogger validationLogger_6 = new ValidationLogger("validationLog");
  private final ValidationLogSink validationLogSink_7 = new ValidationLogSink("validationLogSink");
  //Dirty flags
  private boolean isDirty_eventPublsher_1 = false;
  private boolean isDirty_flightDetailsCsvDecoder0_0 = false;
  private boolean isDirty_greaterThanDecorator_2_2 = false;
  private boolean isDirty_totalFlights = false;
  private boolean isDirty_validationLogSink_7 = false;
  //Filter constants

  public FlightDelayAnalyser() {
    validationLogSink_7.setPublishLogImmediately(true);
    validationLogger_6.logSink = validationLogSink_7;
    eventPublsher_1.publishOnValidate = (boolean) false;
    flightDetailsCsvDecoder0_0.errorLog = validationLogger_6;
    greaterThanDecorator_2_2.filterSubject = flightDetailsCsvDecoder0_0;
    greaterThanDecorator_2_2.source_FlightDetailsCsvDecoder0_1 = flightDetailsCsvDecoder0_0;
    carrierDelayMap.greaterThanDecorator_20 = greaterThanDecorator_2_2;
    totalFlights.setAlwaysReset(false);
    totalFlights.setNotifyOnChangeOnly(false);
    totalFlights.setResetImmediate(true);
    totalFlights.filterSubject = flightDetailsCsvDecoder0_0;
    totalFlights.f = count_4;
  }

  @Override
  public void onEvent(com.fluxtion.api.event.Event event) {
    switch (event.eventId()) {
      case (LogControlEvent.ID):
        {
          LogControlEvent typedEvent = (LogControlEvent) event;
          handleEvent(typedEvent);
          break;
        }
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
      case (RegisterEventHandler.ID):
        {
          RegisterEventHandler typedEvent = (RegisterEventHandler) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(LogControlEvent typedEvent) {
    switch (typedEvent.filterString()) {
        //Event Class:[com.fluxtion.ext.declarative.api.log.LogControlEvent] filterString:[CHANGE_LOG_PROVIDER]
      case ("CHANGE_LOG_PROVIDER"):
        isDirty_validationLogSink_7 = true;
        validationLogSink_7.controlLogProvider(typedEvent);
        afterEvent();
        return;
    }
    afterEvent();
  }

  public void handleEvent(CharEvent typedEvent) {
    //Default, no filter methods
    isDirty_flightDetailsCsvDecoder0_0 = flightDetailsCsvDecoder0_0.charEvent(typedEvent);
    if (isDirty_flightDetailsCsvDecoder0_0) {
      eventPublsher_1.wrapperUpdate(flightDetailsCsvDecoder0_0);
      totalFlights.updated_filterSubject(flightDetailsCsvDecoder0_0);
    }
    if (isDirty_flightDetailsCsvDecoder0_0) {
      isDirty_greaterThanDecorator_2_2 = greaterThanDecorator_2_2.onEvent();
      if (isDirty_greaterThanDecorator_2_2) {
        carrierDelayMap.updategreaterThanDecorator_20(greaterThanDecorator_2_2);
      }
    }
    if (isDirty_flightDetailsCsvDecoder0_0) {
      isDirty_totalFlights = totalFlights.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(EofEvent typedEvent) {
    //Default, no filter methods
    isDirty_flightDetailsCsvDecoder0_0 = flightDetailsCsvDecoder0_0.eof(typedEvent);
    if (isDirty_flightDetailsCsvDecoder0_0) {
      eventPublsher_1.wrapperUpdate(flightDetailsCsvDecoder0_0);
      totalFlights.updated_filterSubject(flightDetailsCsvDecoder0_0);
    }
    if (isDirty_flightDetailsCsvDecoder0_0) {
      isDirty_greaterThanDecorator_2_2 = greaterThanDecorator_2_2.onEvent();
      if (isDirty_greaterThanDecorator_2_2) {
        carrierDelayMap.updategreaterThanDecorator_20(greaterThanDecorator_2_2);
      }
    }
    if (isDirty_flightDetailsCsvDecoder0_0) {
      isDirty_totalFlights = totalFlights.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(RegisterEventHandler typedEvent) {
    //Default, no filter methods
    isDirty_eventPublsher_1 = true;
    eventPublsher_1.registerEventHandler(typedEvent);
    //event stack unwind callbacks
    afterEvent();
  }

  @Override
  public void afterEvent() {
    totalFlights.resetAfterEvent();
    isDirty_eventPublsher_1 = false;
    isDirty_flightDetailsCsvDecoder0_0 = false;
    isDirty_greaterThanDecorator_2_2 = false;
    isDirty_totalFlights = false;
    isDirty_validationLogSink_7 = false;
  }

  @Override
  public void init() {
    flightDetailsCsvDecoder0_0.init();
    eventPublsher_1.init();
    greaterThanDecorator_2_2.init();
    carrierDelayMap.init();
    totalFlights.init();
    validationLogSink_7.init();
  }

  @Override
  public void tearDown() {}

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
