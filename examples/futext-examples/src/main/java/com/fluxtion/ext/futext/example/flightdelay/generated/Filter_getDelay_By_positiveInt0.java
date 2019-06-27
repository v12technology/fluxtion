package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.stream.AbstractFilterWrapper;
import com.fluxtion.ext.streaming.api.stream.NumericPredicates;

/**
 * generated filter function wrapper.
 *
 * <ul>
 *   <li>input class : {@link FlightDetails}
 *   <li>filter function : {@link NumericPredicates#positiveInt}
 * </ul>
 *
 * @author Greg Higgins
 */
public class Filter_getDelay_By_positiveInt0 extends AbstractFilterWrapper<FlightDetails> {

  //source operand inputs
  public FlightDetailsCsvDecoder0 filterSubject;
  public FlightDetailsCsvDecoder0 source_0;
  @NoEventReference public Object resetNotifier;
  private boolean parentReset = false;

  @Initialise
  public void init() {
    result = false;
  }

  @OnEvent
  public boolean onEvent() {
    boolean oldValue = result;
    result =
        (boolean)
            NumericPredicates.positiveInt((double) ((FlightDetails) source_0.event()).getDelay());
    //this is probably right - to be tested
    //return (!notifyOnChangeOnly | !oldValue) & result;
    return (!notifyOnChangeOnly & result) | ((!oldValue) & result);
  }

  @OnParentUpdate("resetNotifier")
  public void resetNotification(Object resetNotifier) {
    parentReset = true;
    if (isResetImmediate()) {
      result = false;
      parentReset = false;
    }
  }

  @AfterEvent
  public void resetAfterEvent() {
    if (parentReset) {
      result = false;
    }
    parentReset = false;
  }

  @Override
  public FilterWrapper<FlightDetails> resetNotifier(Object resetNotifier) {
    this.resetNotifier = resetNotifier;
    return this;
  }

  @Override
  public FlightDetails event() {
    return (FlightDetails) filterSubject.event();
  }

  @Override
  public Class<FlightDetails> eventClass() {
    return FlightDetails.class;
  }
}
