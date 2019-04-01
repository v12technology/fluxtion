package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.stream.AbstractFilterWrapper;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Count;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import com.fluxtion.ext.futext.example.flightdelay.generated.FlightDetailsCsvDecoder0;

/**
 * generated mapper function wrapper for a numeric primitive.
 *
 * <ul>
 *   <li>output class : {@link Number}
 *   <li>input class : {@link FlightDetails}
 *   <li>map function : {@link Count#increment}
 * </ul>
 *
 * @author Greg Higgins
 */
public class Map_FlightDetails_By_increment_7 extends AbstractFilterWrapper<Number> {

  public FlightDetailsCsvDecoder0 filterSubject;
  private boolean filterSubjectUpdated;
  @NoEventReference public Count f;
  private Number result;
  @NoEventReference public Object resetNotifier;
  private boolean parentReset = false;
  private MutableNumber value;
  private MutableNumber oldValue;

  @OnEvent
  public boolean onEvent() {
    oldValue.set(result);
    if (filterSubjectUpdated) {
      result = f.increment((Object) ((FlightDetails) filterSubject.event()));
    }
    value.set(result);
    return !notifyOnChangeOnly | (!oldValue.equals(value));
  }

  private boolean allSourcesUpdated() {
    boolean updated = filterSubjectUpdated;
    return updated;
  }

  @OnParentUpdate("filterSubject")
  public void updated_filterSubject(FlightDetailsCsvDecoder0 updated) {
    filterSubjectUpdated = true;
  }

  @OnParentUpdate("resetNotifier")
  public void resetNotification(Object resetNotifier) {
    parentReset = true;
    if (isResetImmediate()) {
      result = 0;
      f.reset();
      parentReset = false;
    }
  }

  @AfterEvent
  public void resetAfterEvent() {
    if (parentReset | alwaysReset) {
      result = 0;
      f.reset();
    }
    parentReset = false;
  }

  @Override
  public Wrapper<Number> resetNotifier(Object resetNotifier) {
    this.resetNotifier = resetNotifier;
    return this;
  }

  @Override
  public Number event() {
    return value;
  }

  @Override
  public Class<Number> eventClass() {
    return Number.class;
  }

  @Initialise
  public void init() {
    result = 0;
    value = new MutableNumber();
    oldValue = new MutableNumber();
    filterSubjectUpdated = false;
  }
}
