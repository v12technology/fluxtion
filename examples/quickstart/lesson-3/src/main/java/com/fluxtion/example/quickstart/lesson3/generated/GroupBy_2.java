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
 * Generated group by aggregation
 *
 * <pre>
 *  <ul>
 *      <li>template file   : template/GroupByTemplate.vsl
 *      <li>target class    : {@link  Tuple}
 *      <li>input class     : {@link  Trade}
 *  </ul>
 * </pre>
 *
 * @author Greg Higgins
 */
public final class GroupBy_2 implements GroupBy<Tuple> {

  @NoEventReference public Object resetNotifier;
  @SepNode @PushReference public ArrayListWrappedCollection<Tuple> wrappedList;
  private boolean allMatched;
  private Tuple target;
  private GroupByTargetMap<String, Tuple, CalculationStateGroupBy_2> calcState;
  private GroupByIniitialiser<Trade, Tuple> initialisertrade0;
  private SerializableBiConsumer<Tuple, Tuple> initFromCopy;

  public GroupBy_2() {
    wrappedList = new ArrayListWrappedCollection<>();
  }

  @Override
  public void setTargetCollecion(ArrayListWrappedCollection<Tuple> targetCollection) {
    this.wrappedList = targetCollection;
  }

  @EventHandler
  public boolean updatetrade0(Trade event) {
    CalculationStateGroupBy_2 instance = calcState.getOrCreateInstance(event.getSymbol());
    boolean firstMatched = instance.processSource(1, initialisertrade0, event);
    allMatched = instance.allMatched();
    target = instance.target;
    {
      double value = instance.aggregateSum1;
      value = instance.aggregateSum1Function.calcCumSum((double) event.getAmount(), (double) value);
      target.setValue((java.lang.Object) value);
      instance.aggregateSum1 = value;
    }
    //    updated();
    if (firstMatched) {
      wrappedList.addItem(target);
    }
    return allMatched;
  }

  @OnEvent
  public boolean updated() {
    boolean updated = allMatched;
    allMatched = false;
    return updated;
  }

  @Initialise
  public void init() {
    calcState = new GroupByTargetMap<>(CalculationStateGroupBy_2::new);
    wrappedList.init();
    allMatched = false;
    target = null;
    initialisertrade0 =
        new GroupByIniitialiser<Trade, Tuple>() {

          @Override
          public void apply(Trade source, Tuple target) {
            target.setKey((java.lang.Object) source.getSymbol());
          }
        };
    initFromCopy = Tuple::copyKey;
  }

  @Override
  public Tuple value(Object key) {
    return calcState.getInstance((String) key).target;
  }

  @Override
  public Collection<Tuple> collection() {
    return wrappedList == null ? Collections.EMPTY_LIST : wrappedList.collection();
  }

  @Override
  public <V extends Wrapper<Tuple>> Map<String, V> getMap() {
    return (Map<String, V>) calcState.getInstanceMap();
  }

  @Override
  public Tuple record() {
    return target;
  }

  @Override
  public Class<Tuple> recordClass() {
    return Tuple.class;
  }

  @Override
  public GroupBy_2 resetNotifier(Object resetNotifier) {
    this.resetNotifier = resetNotifier;
    return this;
  }

  @OnParentUpdate("resetNotifier")
  public void resetNotification(Object resetNotifier) {
    init();
  }

  @Override
  public void reset() {
    init();
  }

  @Override
  public void combine(GroupBy<Tuple> other) {
    GroupBy_2 otherGroupBy = (GroupBy_2) other;
    Map<String, CalculationStateGroupBy_2> sourceMap = otherGroupBy.calcState.getInstanceMap();
    Map<String, CalculationStateGroupBy_2> targetMap = calcState.getInstanceMap();
    sourceMap
        .entrySet()
        .forEach(
            (Map.Entry<String, CalculationStateGroupBy_2> e) -> {
              if (targetMap.containsKey(e.getKey())) {
                final CalculationStateGroupBy_2 sourceState = e.getValue();
                CalculationStateGroupBy_2 newInstance = calcState.getOrCreateInstance(e.getKey());
                newInstance.combine(sourceState);
                newInstance.target.setValue((java.lang.Object) newInstance.aggregateSum1);
              } else {
                final CalculationStateGroupBy_2 sourceState = e.getValue();
                CalculationStateGroupBy_2 newInstance = calcState.getOrCreateInstance(e.getKey());
                newInstance.updateMap.clear();
                newInstance.updateMap.or(sourceState.updateMap);
                initFromCopy.accept(sourceState.target, newInstance.target);
                newInstance.combine(sourceState);
                newInstance.target.setValue((java.lang.Object) newInstance.aggregateSum1);
                wrappedList.addItem(newInstance.target);
              }
            });
    wrappedList.sort();
  }

  @Override
  public void deduct(GroupBy<Tuple> other) {
    GroupBy_2 otherGroupBy = (GroupBy_2) other;
    Map<String, CalculationStateGroupBy_2> sourceMap = otherGroupBy.calcState.getInstanceMap();
    Map<String, CalculationStateGroupBy_2> targetMap = calcState.getInstanceMap();
    sourceMap
        .entrySet()
        .forEach(
            (Map.Entry<String, CalculationStateGroupBy_2> e) -> {
              if (targetMap.containsKey(e.getKey())) {
                final CalculationStateGroupBy_2 sourceState = e.getValue();
                CalculationStateGroupBy_2 newInstance = calcState.getOrCreateInstance(e.getKey());
                newInstance.deduct(sourceState);
                newInstance.target.setValue((java.lang.Object) newInstance.aggregateSum1);
                if (newInstance.isExpired()) {
                  calcState.expireInstance(e.getKey());
                  wrappedList.removeItem(newInstance.target);
                }
              } else {

              }
            });
    wrappedList.sort();
  }

  @Override
  public WrappedList<Tuple> comparator(Comparator comparator) {
    return wrappedList.comparator(comparator);
  }

  @Override
  public WrappedList<Tuple> comparing(SerializableFunction comparingFunction) {
    return wrappedList.comparing(comparingFunction);
  }
}
