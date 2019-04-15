package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.ext.futext.example.flightdelay.CarrierDelay;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.AggregateFunctions.AggregateAverage;
import com.fluxtion.ext.streaming.api.group.GroupByIniitialiser;
import java.util.BitSet;

/**
 * generated group by calculation state holder. This class holds the state of a group by
 * calculation.
 *
 * <p>target class : CarrierDelay
 *
 * @author Greg Higgins
 */
public final class CalculationStateGroupBy_6 implements Wrapper<CarrierDelay> {

  private static final int SOURCE_COUNT = 1;
  private final BitSet updateMap = new BitSet(SOURCE_COUNT);

  public CarrierDelay target;
  public int aggregateCount4;
  public double aggregateSum5;
  public AggregateAverage aggregateAverage3Function = new AggregateAverage();
  public double aggregateAverage3;

  public CalculationStateGroupBy_6() {
    target = new CarrierDelay();
  }

  public boolean allMatched() {
    return SOURCE_COUNT == updateMap.cardinality();
  }

  public boolean processSource(int index, GroupByIniitialiser initialiser, Object source) {
    if (!updateMap.get(index)) {
      initialiser.apply(source, target);
    }
    updateMap.set(index);
    return allMatched();
  }

  public boolean processSource(int index, Object source) {
    updateMap.set(index);
    return allMatched();
  }

  @Override
  public CarrierDelay event() {
    return target;
  }

  @Override
  public Class<CarrierDelay> eventClass() {
    return CarrierDelay.class;
  }

  @Override
  public String toString() {
    return event().toString();
  }
}
