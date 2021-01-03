package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.SepContext;
import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.api.annotations.PushReference;
import com.fluxtion.api.annotations.SepNode;
import com.fluxtion.api.partition.LambdaReflection.SerializableBiConsumer;
import com.fluxtion.api.partition.LambdaReflection.SerializableFunction;
import com.fluxtion.ext.futext.example.flightdelay.CarrierDelay;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import com.fluxtion.ext.futext.example.flightdelay.generated.Filter_getDelay_By_positiveInt0;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.group.GroupByIniitialiser;
import com.fluxtion.ext.streaming.api.group.GroupByTargetMap;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Average;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Count;
import com.fluxtion.ext.streaming.api.stream.StreamFunctions.Sum;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Generated group by calculation state holder.
 *
 * <pre>
 *  <ul>
 *      <li>template file   : template/GroupByCalculationState.vsl
 *      <li>input class     : target class  : CarrierDelay
 *  </ul>
 * </pre>
 *
 * @author Greg Higgins
 */
public final class CalculationStateGroupBy_4 implements Wrapper<CarrierDelay> {

  private static final int SOURCE_COUNT = 1;
  final BitSet updateMap = new BitSet(SOURCE_COUNT);
  private final MutableNumber tempNumber = new MutableNumber();
  private int combineCount;
  public CarrierDelay target;
  public Sum sum3Function = new Sum();
  public double sum3;
  public Average average1Function = new Average();
  public double average1;
  public Count count2Function = new Count();
  public int count2;

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
  public boolean processSource(int index, GroupByIniitialiser initialiser, Object source) {
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

  public void combine(CalculationStateGroupBy_4 other) {
    //list the combining operations
    combineCount++;
    sum3 = sum3Function.combine(other.sum3Function, tempNumber).doubleValue();
    average1 = average1Function.combine(other.average1Function, tempNumber).doubleValue();
    count2 = count2Function.combine(other.count2Function, tempNumber).intValue();
  }

  public void deduct(CalculationStateGroupBy_4 other) {
    combineCount--;
    sum3 = sum3Function.deduct(other.sum3Function, tempNumber).doubleValue();
    average1 = average1Function.deduct(other.average1Function, tempNumber).doubleValue();
    count2 = count2Function.deduct(other.count2Function, tempNumber).intValue();
  }

  public void aggregateNonStateful(Collection<CalculationStateGroupBy_4> states) {
    //TODO
  }

  public int getCombineCount() {
    return combineCount;
  }

  public boolean isExpired() {
    return combineCount < 1;
  }
}
