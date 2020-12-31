package com.fluxtion.example.quickstart.lesson3.generated;

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
import com.fluxtion.example.quickstart.lesson3.TradeMonitor.Trade;
import com.fluxtion.ext.streaming.api.ArrayListWrappedCollection;
import com.fluxtion.ext.streaming.api.Stateful;
import com.fluxtion.ext.streaming.api.WrappedCollection;
import com.fluxtion.ext.streaming.api.WrappedList;
import com.fluxtion.ext.streaming.api.Wrapper;
import com.fluxtion.ext.streaming.api.group.AggregateFunctions.AggregateSum;
import com.fluxtion.ext.streaming.api.group.GroupBy;
import com.fluxtion.ext.streaming.api.group.GroupByIniitialiser;
import com.fluxtion.ext.streaming.api.group.GroupByTargetMap;
import com.fluxtion.ext.streaming.api.numeric.MutableNumber;
import com.fluxtion.ext.streaming.api.util.Tuple;
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
 *      <li>input class     : target class  : Tuple
 *  </ul>
 * </pre>
 *
 * @author Greg Higgins
 */
public final class CalculationStateGroupBy_2 implements Wrapper<Tuple> {

  private static final int SOURCE_COUNT = 1;
  final BitSet updateMap = new BitSet(SOURCE_COUNT);
  private final MutableNumber tempNumber = new MutableNumber();
  private int combineCount;
  public Tuple target;
  public AggregateSum aggregateSum1Function = new AggregateSum();
  public double aggregateSum1;

  public CalculationStateGroupBy_2() {
    target = new Tuple();
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
  public Tuple event() {
    return target;
  }

  @Override
  public Class<Tuple> eventClass() {
    return Tuple.class;
  }

  @Override
  public String toString() {
    return event().toString();
  }

  public void combine(CalculationStateGroupBy_2 other) {
    //list the combining operations
    combineCount++;
    aggregateSum1 =
        aggregateSum1Function.combine(other.aggregateSum1Function, tempNumber).doubleValue();
  }

  public void deduct(CalculationStateGroupBy_2 other) {
    combineCount--;
    aggregateSum1 =
        aggregateSum1Function.deduct(other.aggregateSum1Function, tempNumber).doubleValue();
  }

  public void aggregateNonStateful(Collection<CalculationStateGroupBy_2> states) {
    //TODO
  }

  public int getCombineCount() {
    return combineCount;
  }

  public boolean isExpired() {
    return combineCount < 1;
  }
}
