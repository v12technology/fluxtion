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

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Count;
import com.fluxtion.ext.text.api.csv.ValidationLogSink;
import com.fluxtion.ext.text.api.csv.ValidationLogger;
import com.fluxtion.ext.text.api.event.CharEvent;
import com.fluxtion.ext.text.api.event.EofEvent;

/*
 * <pre>
 * generation time   : 2020-04-28T00:13:00.874918
 * generator version : 2.3.2-SNAPSHOT
 * api version       : 2.3.2-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class FlightDelayAnalyser implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  private final Count count_3 = new Count();
  private final FlightDetailsCsvDecoder0 flightDetailsCsvDecoder0_0 =
      new FlightDetailsCsvDecoder0();
  private final Filter_getDelay_By_positiveInt0 filter_getDelay_By_positiveInt0_1 =
      new Filter_getDelay_By_positiveInt0();
  public final GroupBy_4 carrierDelayMap = new GroupBy_4();
  public final Map_FlightDetails_With_increment0 totalFlights =
      new Map_FlightDetails_With_increment0();
  private final ValidationLogger validationLogger_5 = new ValidationLogger("validationLog");
  private final ValidationLogSink validationLogSink_6 = new ValidationLogSink("validationLogSink");
  //Dirty flags
  private boolean isDirty_filter_getDelay_By_positiveInt0_1 = false;
  private boolean isDirty_flightDetailsCsvDecoder0_0 = false;
  //Filter constants

  public FlightDelayAnalyser() {
    filter_getDelay_By_positiveInt0_1.setAlwaysReset(false);
    filter_getDelay_By_positiveInt0_1.setNotifyOnChangeOnly(false);
    filter_getDelay_By_positiveInt0_1.setResetImmediate(true);
    filter_getDelay_By_positiveInt0_1.setValidOnStart(false);
    filter_getDelay_By_positiveInt0_1.filterSubject = flightDetailsCsvDecoder0_0;
    filter_getDelay_By_positiveInt0_1.source_0 = flightDetailsCsvDecoder0_0;
    flightDetailsCsvDecoder0_0.errorLog = validationLogger_5;
    carrierDelayMap.filter_getDelay_By_positiveInt00 = filter_getDelay_By_positiveInt0_1;
    totalFlights.setAlwaysReset(false);
    totalFlights.setNotifyOnChangeOnly(false);
    totalFlights.setResetImmediate(true);
    totalFlights.setValidOnStart(false);
    totalFlights.filterSubject = flightDetailsCsvDecoder0_0;
    totalFlights.f = count_3;
    validationLogSink_6.setPublishLogImmediately(true);
    validationLogger_5.logSink = validationLogSink_6;
  }

  @Override
  public void onEvent(Object event) {
    switch (event.getClass().getName()) {
      case ("com.fluxtion.ext.streaming.api.log.LogControlEvent"):
        {
          LogControlEvent typedEvent = (LogControlEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.ext.text.api.event.CharEvent"):
        {
          CharEvent typedEvent = (CharEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.ext.text.api.event.EofEvent"):
        {
          EofEvent typedEvent = (EofEvent) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(LogControlEvent typedEvent) {
    switch (typedEvent.filterString()) {
        //Event Class:[com.fluxtion.ext.streaming.api.log.LogControlEvent] filterString:[CHANGE_LOG_PROVIDER]
      case ("CHANGE_LOG_PROVIDER"):
        validationLogSink_6.controlLogProvider(typedEvent);
        afterEvent();
        return;
    }
    afterEvent();
  }

  public void handleEvent(CharEvent typedEvent) {
    //Default, no filter methods
    isDirty_flightDetailsCsvDecoder0_0 = flightDetailsCsvDecoder0_0.charEvent(typedEvent);
    if (isDirty_flightDetailsCsvDecoder0_0) {
      isDirty_filter_getDelay_By_positiveInt0_1 = filter_getDelay_By_positiveInt0_1.onEvent();
      if (isDirty_filter_getDelay_By_positiveInt0_1) {
        carrierDelayMap.updatefilter_getDelay_By_positiveInt00(filter_getDelay_By_positiveInt0_1);
      }
    }
    if (isDirty_filter_getDelay_By_positiveInt0_1) {
      carrierDelayMap.updated();
    }
    if (isDirty_flightDetailsCsvDecoder0_0) {
      totalFlights.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(EofEvent typedEvent) {
    //Default, no filter methods
    isDirty_flightDetailsCsvDecoder0_0 = flightDetailsCsvDecoder0_0.eof(typedEvent);
    if (isDirty_flightDetailsCsvDecoder0_0) {
      isDirty_filter_getDelay_By_positiveInt0_1 = filter_getDelay_By_positiveInt0_1.onEvent();
      if (isDirty_filter_getDelay_By_positiveInt0_1) {
        carrierDelayMap.updatefilter_getDelay_By_positiveInt00(filter_getDelay_By_positiveInt0_1);
      }
    }
    if (isDirty_filter_getDelay_By_positiveInt0_1) {
      carrierDelayMap.updated();
    }
    if (isDirty_flightDetailsCsvDecoder0_0) {
      totalFlights.onEvent();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void afterEvent() {
    totalFlights.resetAfterEvent();
    filter_getDelay_By_positiveInt0_1.resetAfterEvent();
    isDirty_filter_getDelay_By_positiveInt0_1 = false;
    isDirty_flightDetailsCsvDecoder0_0 = false;
  }

  @Override
  public void init() {
    count_3.reset();
    flightDetailsCsvDecoder0_0.init();
    filter_getDelay_By_positiveInt0_1.init();
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
