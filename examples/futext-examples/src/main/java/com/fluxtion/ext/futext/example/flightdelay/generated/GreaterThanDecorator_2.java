package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.Test;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;

/**
 * generated Test wrapper.
 *
 * <p>target class : com.fluxtion.ext.futext.api.filter.BinaryPredicates.GreaterThan target method :
 * isGreaterThan
 *
 * @author Greg Higgins
 */
public class GreaterThanDecorator_2 implements Wrapper<FlightDetails> {

  //source operand inputs
  public FlightDetailsCsvDecoder0 filterSubject;
  public FlightDetailsCsvDecoder0 source_FlightDetailsCsvDecoder0_1;
  @NoEventReference public com.fluxtion.ext.futext.api.filter.BinaryPredicates.GreaterThan f;

  @Initialise
  public void init() {
    f = new com.fluxtion.ext.futext.api.filter.BinaryPredicates.GreaterThan();
  }

  @OnEvent
  public boolean onEvent() {
    return f.isGreaterThan(
        ((FlightDetails) source_FlightDetailsCsvDecoder0_1.event()).getDelay(), (double) 0);
  }

  @Override
  public FlightDetails event() {
    return filterSubject.event();
  }

  @Override
  public Class<FlightDetails> eventClass() {
    return FlightDetails.class;
  }
}
