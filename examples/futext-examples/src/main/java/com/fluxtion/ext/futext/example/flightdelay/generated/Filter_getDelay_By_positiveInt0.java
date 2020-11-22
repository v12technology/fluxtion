package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.AfterEvent;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import com.fluxtion.ext.futext.example.flightdelay.generated.FlightDetailsCsvDecoder0;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Test;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.stream.AbstractFilterWrapper;
import com.fluxtion.ext.streaming.api.stream.NumericPredicates;

/**
 * generated filter function wrapper.
 *
 * <pre>
 *  <ul>
 *      <li>template file   : template/FilterTemplate.vsl
 *      <li>input class     : {@link FlightDetails}
 *      <li>filter function : {@link NumericPredicates#positiveInt}
 *  </ul>
 * </pre>
 *
 * @author Greg Higgins
 */
public class Filter_getDelay_By_positiveInt0 extends AbstractFilterWrapper<FlightDetails> {

  //source operand inputs
  public FlightDetailsCsvDecoder0 filterSubject;
  public FlightDetailsCsvDecoder0 source_0;

  @OnEvent
  @SuppressWarnings("unchecked")
  public boolean onEvent() {
    boolean oldValue = result;
    result =
        (boolean)
            NumericPredicates.positiveInt((double) ((FlightDetails) source_0.event()).getDelay());
    return (!notifyOnChangeOnly | !oldValue) & result;
    //return (!notifyOnChangeOnly & result) | ((!oldValue) & result);
  }

  @AfterEvent
  public void resetAfterEvent() {
    if (reset) {
      result = false;
    }
    reset = false;
  }

  @Override
  public FlightDetails event() {
    return (FlightDetails) filterSubject.event();
  }

  @Override
  public Class<FlightDetails> eventClass() {
    return FlightDetails.class;
  }

  @Override
  public void reset() {
    //add override logic
    result = false;
  }
}
