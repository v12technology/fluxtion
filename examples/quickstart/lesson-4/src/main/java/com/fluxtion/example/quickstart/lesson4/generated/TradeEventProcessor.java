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
package com.fluxtion.example.quickstart.lesson4.generated;

import com.fluxtion.api.StaticEventProcessor;
import com.fluxtion.api.lifecycle.BatchHandler;
import com.fluxtion.api.lifecycle.Lifecycle;

import com.fluxtion.api.audit.Auditor;
import com.fluxtion.api.event.Event;
import com.fluxtion.api.time.Clock;
import com.fluxtion.api.time.ClockStrategy.ClockStrategyEvent;
import com.fluxtion.example.quickstart.lesson4.TradeMonitor.Trade;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.SubList;
import com.fluxtion.ext.streaming.api.log.LogControlEvent;
import com.fluxtion.ext.streaming.api.util.Tuple.NumberValueComparator;
import com.fluxtion.ext.streaming.api.window.SlidingGroupByAggregator;
import com.fluxtion.ext.streaming.api.window.TimeReset;

/*
 * <pre>
 * generation time   : 2021-01-12T20:30:05.345809900
 * generator version : 2.10.14-SNAPSHOT
 * api version       : 2.10.14-SNAPSHOT
 * </pre>
 * @author Greg Higgins
 */
@SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
public class TradeEventProcessor implements StaticEventProcessor, BatchHandler, Lifecycle {

  //Node declarations
  public final Clock clock = new Clock();
  private final GroupBy_2 groupBy_2_0 = new GroupBy_2();
  private final ArrayListWrappedCollection arrayListWrappedCollection_6 =
      new ArrayListWrappedCollection();
  private final NumberValueComparator numberValueComparator_9 = new NumberValueComparator();
  private final TimeReset timeReset_7 = new TimeReset(groupBy_2_0, 1000L, clock);
  private final SlidingGroupByAggregator slidingGroupByAggregator_1 =
      new SlidingGroupByAggregator(groupBy_2_0, groupBy_2_0, 5);
  private final ArrayListWrappedCollection arrayListWrappedCollection_8 =
      new ArrayListWrappedCollection();
  public final SubList top3 = new SubList(arrayListWrappedCollection_8, 0, 3);
  private final GetField_SubList_List0 getField_SubList_List0_3 = new GetField_SubList_List0();
  private final Map_List_With_formatTradeList0 map_List_With_formatTradeList0_4 =
      new Map_List_With_formatTradeList0();
  private final LogMsgBuilder4 logMsgBuilder4_5 = new LogMsgBuilder4();
  //Dirty flags
  private boolean isDirty_arrayListWrappedCollection_8 = false;
  private boolean isDirty_getField_SubList_List0_3 = false;
  private boolean isDirty_groupBy_2_0 = false;
  private boolean isDirty_map_List_With_formatTradeList0_4 = false;
  private boolean isDirty_slidingGroupByAggregator_1 = false;
  private boolean isDirty_timeReset_7 = false;
  private boolean isDirty_top3 = false;
  //Filter constants

  public TradeEventProcessor() {
    getField_SubList_List0_3.setNotifyOnChangeOnly(false);
    getField_SubList_List0_3.setValidOnStart(false);
    getField_SubList_List0_3.filterSubject = top3;
    groupBy_2_0.wrappedList = arrayListWrappedCollection_6;
    logMsgBuilder4_5.setLogPrefix(false);
    logMsgBuilder4_5.source_Map_List_With_formatTradeList0_3 = map_List_With_formatTradeList0_4;
    logMsgBuilder4_5.logNotifier = map_List_With_formatTradeList0_4;
    logMsgBuilder4_5.logLevel = (int) 3;
    logMsgBuilder4_5.initCapacity = (int) 256;
    map_List_With_formatTradeList0_4.setNotifyOnChangeOnly(false);
    map_List_With_formatTradeList0_4.setValidOnStart(false);
    map_List_With_formatTradeList0_4.filterSubject = getField_SubList_List0_3;
    arrayListWrappedCollection_6.setReversed(false);
    arrayListWrappedCollection_8.setComparator(numberValueComparator_9);
    arrayListWrappedCollection_8.setReversed(true);
    top3.setRecalculated(false);
    slidingGroupByAggregator_1.setCollection(arrayListWrappedCollection_8);
    slidingGroupByAggregator_1.setExpiredCount(0);
    slidingGroupByAggregator_1.setFirstExpiry(false);
    slidingGroupByAggregator_1.setTimeReset(timeReset_7);
    //node auditors
    initialiseAuditor(clock);
  }

  @Override
  public void onEvent(Object event) {
    switch (event.getClass().getName()) {
      case ("com.fluxtion.api.time.ClockStrategy$ClockStrategyEvent"):
        {
          ClockStrategyEvent typedEvent = (ClockStrategyEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.example.quickstart.lesson4.TradeMonitor$Trade"):
        {
          Trade typedEvent = (Trade) event;
          handleEvent(typedEvent);
          break;
        }
      case ("com.fluxtion.ext.streaming.api.log.LogControlEvent"):
        {
          LogControlEvent typedEvent = (LogControlEvent) event;
          handleEvent(typedEvent);
          break;
        }
      case ("java.lang.Object"):
        {
          Object typedEvent = (Object) event;
          handleEvent(typedEvent);
          break;
        }
    }
  }

  public void handleEvent(ClockStrategyEvent typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    clock.setClockStrategy(typedEvent);
    isDirty_timeReset_7 = timeReset_7.anyEvent(typedEvent);
    if (isDirty_groupBy_2_0 | isDirty_timeReset_7) {
      isDirty_slidingGroupByAggregator_1 = slidingGroupByAggregator_1.aggregate();
    }
    if (isDirty_slidingGroupByAggregator_1) {
      isDirty_arrayListWrappedCollection_8 = arrayListWrappedCollection_8.updated();
      if (isDirty_arrayListWrappedCollection_8) {
        top3.parentUpdate(arrayListWrappedCollection_8);
      }
    }
    if (isDirty_arrayListWrappedCollection_8) {
      isDirty_top3 = top3.onEvent();
    }
    if (isDirty_top3) {
      isDirty_getField_SubList_List0_3 = getField_SubList_List0_3.onEvent();
    }
    if (isDirty_getField_SubList_List0_3) {
      isDirty_map_List_With_formatTradeList0_4 = map_List_With_formatTradeList0_4.onEvent();
      if (isDirty_map_List_With_formatTradeList0_4) {
        logMsgBuilder4_5.postLog(map_List_With_formatTradeList0_4);
      }
    }
    if (isDirty_map_List_With_formatTradeList0_4) {
      logMsgBuilder4_5.logMessage();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(Trade typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    isDirty_groupBy_2_0 = groupBy_2_0.updatetrade0(typedEvent);
    isDirty_groupBy_2_0 = groupBy_2_0.updated();
    if (isDirty_groupBy_2_0) {
      arrayListWrappedCollection_6.updated();
    }
    isDirty_timeReset_7 = timeReset_7.anyEvent(typedEvent);
    if (isDirty_groupBy_2_0 | isDirty_timeReset_7) {
      isDirty_slidingGroupByAggregator_1 = slidingGroupByAggregator_1.aggregate();
    }
    if (isDirty_slidingGroupByAggregator_1) {
      isDirty_arrayListWrappedCollection_8 = arrayListWrappedCollection_8.updated();
      if (isDirty_arrayListWrappedCollection_8) {
        top3.parentUpdate(arrayListWrappedCollection_8);
      }
    }
    if (isDirty_arrayListWrappedCollection_8) {
      isDirty_top3 = top3.onEvent();
    }
    if (isDirty_top3) {
      isDirty_getField_SubList_List0_3 = getField_SubList_List0_3.onEvent();
    }
    if (isDirty_getField_SubList_List0_3) {
      isDirty_map_List_With_formatTradeList0_4 = map_List_With_formatTradeList0_4.onEvent();
      if (isDirty_map_List_With_formatTradeList0_4) {
        logMsgBuilder4_5.postLog(map_List_With_formatTradeList0_4);
      }
    }
    if (isDirty_map_List_With_formatTradeList0_4) {
      logMsgBuilder4_5.logMessage();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(LogControlEvent typedEvent) {
    auditEvent(typedEvent);
    switch (typedEvent.filterString()) {
        //Event Class:[com.fluxtion.ext.streaming.api.log.LogControlEvent] filterString:[CHANGE_FILTER]
      case ("CHANGE_FILTER"):
        logMsgBuilder4_5.controlLogIdFilter(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.ext.streaming.api.log.LogControlEvent] filterString:[CHANGE_LEVEL]
      case ("CHANGE_LEVEL"):
        logMsgBuilder4_5.controlLogLevelFilter(typedEvent);
        afterEvent();
        return;
        //Event Class:[com.fluxtion.ext.streaming.api.log.LogControlEvent] filterString:[CHANGE_LOG_PROVIDER]
      case ("CHANGE_LOG_PROVIDER"):
        logMsgBuilder4_5.controlLogProvider(typedEvent);
        afterEvent();
        return;
    }
    //Default, no filter methods
    isDirty_timeReset_7 = timeReset_7.anyEvent(typedEvent);
    if (isDirty_groupBy_2_0 | isDirty_timeReset_7) {
      isDirty_slidingGroupByAggregator_1 = slidingGroupByAggregator_1.aggregate();
    }
    if (isDirty_slidingGroupByAggregator_1) {
      isDirty_arrayListWrappedCollection_8 = arrayListWrappedCollection_8.updated();
      if (isDirty_arrayListWrappedCollection_8) {
        top3.parentUpdate(arrayListWrappedCollection_8);
      }
    }
    if (isDirty_arrayListWrappedCollection_8) {
      isDirty_top3 = top3.onEvent();
    }
    if (isDirty_top3) {
      isDirty_getField_SubList_List0_3 = getField_SubList_List0_3.onEvent();
    }
    if (isDirty_getField_SubList_List0_3) {
      isDirty_map_List_With_formatTradeList0_4 = map_List_With_formatTradeList0_4.onEvent();
      if (isDirty_map_List_With_formatTradeList0_4) {
        logMsgBuilder4_5.postLog(map_List_With_formatTradeList0_4);
      }
    }
    if (isDirty_map_List_With_formatTradeList0_4) {
      logMsgBuilder4_5.logMessage();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  public void handleEvent(Object typedEvent) {
    auditEvent(typedEvent);
    //Default, no filter methods
    isDirty_timeReset_7 = timeReset_7.anyEvent(typedEvent);
    if (isDirty_groupBy_2_0 | isDirty_timeReset_7) {
      isDirty_slidingGroupByAggregator_1 = slidingGroupByAggregator_1.aggregate();
    }
    if (isDirty_slidingGroupByAggregator_1) {
      isDirty_arrayListWrappedCollection_8 = arrayListWrappedCollection_8.updated();
      if (isDirty_arrayListWrappedCollection_8) {
        top3.parentUpdate(arrayListWrappedCollection_8);
      }
    }
    if (isDirty_arrayListWrappedCollection_8) {
      isDirty_top3 = top3.onEvent();
    }
    if (isDirty_top3) {
      isDirty_getField_SubList_List0_3 = getField_SubList_List0_3.onEvent();
    }
    if (isDirty_getField_SubList_List0_3) {
      isDirty_map_List_With_formatTradeList0_4 = map_List_With_formatTradeList0_4.onEvent();
      if (isDirty_map_List_With_formatTradeList0_4) {
        logMsgBuilder4_5.postLog(map_List_With_formatTradeList0_4);
      }
    }
    if (isDirty_map_List_With_formatTradeList0_4) {
      logMsgBuilder4_5.logMessage();
    }
    //event stack unwind callbacks
    afterEvent();
  }

  private void auditEvent(Object typedEvent) {
    clock.eventReceived(typedEvent);
  }

  private void auditEvent(Event typedEvent) {
    clock.eventReceived(typedEvent);
  }

  private void initialiseAuditor(Auditor auditor) {
    auditor.init();
    auditor.nodeRegistered(getField_SubList_List0_3, "getField_SubList_List0_3");
    auditor.nodeRegistered(groupBy_2_0, "groupBy_2_0");
    auditor.nodeRegistered(logMsgBuilder4_5, "logMsgBuilder4_5");
    auditor.nodeRegistered(map_List_With_formatTradeList0_4, "map_List_With_formatTradeList0_4");
    auditor.nodeRegistered(arrayListWrappedCollection_6, "arrayListWrappedCollection_6");
    auditor.nodeRegistered(arrayListWrappedCollection_8, "arrayListWrappedCollection_8");
    auditor.nodeRegistered(top3, "top3");
    auditor.nodeRegistered(numberValueComparator_9, "numberValueComparator_9");
    auditor.nodeRegistered(slidingGroupByAggregator_1, "slidingGroupByAggregator_1");
    auditor.nodeRegistered(timeReset_7, "timeReset_7");
  }

  private void afterEvent() {
    timeReset_7.resetIfNecessary();
    clock.processingComplete();
    isDirty_arrayListWrappedCollection_8 = false;
    isDirty_getField_SubList_List0_3 = false;
    isDirty_groupBy_2_0 = false;
    isDirty_map_List_With_formatTradeList0_4 = false;
    isDirty_slidingGroupByAggregator_1 = false;
    isDirty_timeReset_7 = false;
    isDirty_top3 = false;
  }

  @Override
  public void init() {
    clock.init();
    groupBy_2_0.init();
    groupBy_2_0.reset();
    arrayListWrappedCollection_6.init();
    arrayListWrappedCollection_6.reset();
    timeReset_7.init();
    slidingGroupByAggregator_1.reset();
    arrayListWrappedCollection_8.init();
    arrayListWrappedCollection_8.reset();
    top3.init();
    top3.reset();
    getField_SubList_List0_3.reset();
    map_List_With_formatTradeList0_4.reset();
    logMsgBuilder4_5.init();
  }

  @Override
  public void tearDown() {
    clock.tearDown();
  }

  @Override
  public void batchPause() {}

  @Override
  public void batchEnd() {}
}
