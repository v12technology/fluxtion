package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.ext.futext.example.flightdelay.CarrierDelay;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.AggregateFunctions.AggregateAverage;
import com.fluxtion.ext.streaming.api.group.GroupByInitializer;
import java.util.BitSet;

/**
 * generated group by calculation state holder. This class holds the state of a group by
 * calculation.
 *
 * <p>target class : CarrierDelay
 *
 * @author Greg Higgins
 */
public final class CalculationStateGroupBy_4 implements Wrapper<CarrierDelay> {

  private static final int SOURCE_COUNT = 1;
  private final BitSet updateMap = new BitSet(SOURCE_COUNT);

  public CarrierDelay target;
  public double aggregateSum3;
  public int aggregateCount2;
  public AggregateAverage aggregateAverage1Function = new AggregateAverage();
  public double aggregateAverage1;

  public CalculationStateGroupBy_4() {
    target = new CarrierDelay();
  }

  public boolean allMatched() {
    return SOURCE_COUNT == updateMap.cardinality();
  }

  /**
   * @param index
   * @param initialiser
   * @param source
   * @return The first time this is a complete record is processed
   */
  public boolean processSource(int index, GroupByInitializer initialiser, Object source) {
    boolean prevMatched = allMatched();
    if (!updateMap.get(index)) {
      initialiser.apply(source, target);
    }
    updateMap.set(index);
    return allMatched() ^ prevMatched;
  }

  /**
   * @param index
   * @param source
   * @return The first time this is a complete record is processed
   */
  public boolean processSource(int index, Object source) {
    boolean prevMatched = allMatched();
    updateMap.set(index);
    return allMatched() ^ prevMatched;
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
