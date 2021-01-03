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
 * Generated group by aggregation
 *
 * <pre>
 *  <ul>
 *      <li>template file   : template/GroupByTemplate.vsl
 *      <li>target class    : {@link  CarrierDelay}
 *      <li>input class     : {@link  Filter_getDelay_By_positiveInt0}
 *  </ul>
 * </pre>
 *
 * @author Greg Higgins
 */
public final class GroupBy_4 implements GroupBy<CarrierDelay> {

  @NoEventReference public Object resetNotifier;
  @SepNode @PushReference public ArrayListWrappedCollection<CarrierDelay> wrappedList;
  private boolean allMatched;
  private MutableNumber number = new MutableNumber();
  public Filter_getDelay_By_positiveInt0 filter_getDelay_By_positiveInt00;
  private CarrierDelay target;
  private GroupByTargetMap<String, CarrierDelay, CalculationStateGroupBy_4> calcState;
  private GroupByIniitialiser<FlightDetails, CarrierDelay>
      initialiserfilter_getDelay_By_positiveInt00;
  private SerializableBiConsumer<CarrierDelay, CarrierDelay> initFromCopy;

  public GroupBy_4() {
    wrappedList = new ArrayListWrappedCollection<>();
  }

  @Override
  public void setTargetCollecion(ArrayListWrappedCollection<CarrierDelay> targetCollection) {
    this.wrappedList = targetCollection;
  }

  @OnParentUpdate("filter_getDelay_By_positiveInt00")
  public boolean updatefilter_getDelay_By_positiveInt00(
      Filter_getDelay_By_positiveInt0 eventWrapped) {
    FlightDetails event = (FlightDetails) eventWrapped.event();
    CalculationStateGroupBy_4 instance = calcState.getOrCreateInstance(event.getCarrier());
    boolean firstMatched =
        instance.processSource(1, initialiserfilter_getDelay_By_positiveInt00, event);
    allMatched = instance.allMatched();
    target = instance.target;
    {
      double value = instance.sum3;
      value = instance.sum3Function.addValue((java.lang.Number) number.set(event.getDelay()));
      target.setTotalDelayMins((int) value);
      instance.sum3 = value;
    }
    {
      double value = instance.average1;
      value = instance.average1Function.addValue((double) event.getDelay());
      target.setAvgDelay((int) value);
      instance.average1 = value;
    }
    {
      int value = instance.count2;
      value = instance.count2Function.increment((java.lang.Object) event);
      target.setTotalFlights((int) value);
      instance.count2 = value;
    }
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
    calcState = new GroupByTargetMap<>(CalculationStateGroupBy_4::new);
    wrappedList.init();
    allMatched = false;
    target = null;
    number.set(0);
    initialiserfilter_getDelay_By_positiveInt00 =
        new GroupByIniitialiser<FlightDetails, CarrierDelay>() {

          @Override
          public void apply(FlightDetails source, CarrierDelay target) {
            target.setCarrierId((java.lang.String) source.getCarrier());
          }
        };
    initFromCopy = (a, b) -> {};
  }

  @Override
  public CarrierDelay value(Object key) {
    return calcState.getInstance((String) key).target;
  }

  @Override
  public Collection<CarrierDelay> collection() {
    return wrappedList == null ? Collections.EMPTY_LIST : wrappedList.collection();
  }

  @Override
  public <V extends Wrapper<CarrierDelay>> Map<String, V> getMap() {
    return (Map<String, V>) calcState.getInstanceMap();
  }

  @Override
  public CarrierDelay record() {
    return target;
  }

  @Override
  public Class<CarrierDelay> recordClass() {
    return CarrierDelay.class;
  }

  @Override
  public GroupBy_4 resetNotifier(Object resetNotifier) {
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
  public void combine(GroupBy<CarrierDelay> other) {
    GroupBy_4 otherGroupBy = (GroupBy_4) other;
    Map<String, CalculationStateGroupBy_4> sourceMap = otherGroupBy.calcState.getInstanceMap();
    Map<String, CalculationStateGroupBy_4> targetMap = calcState.getInstanceMap();
    sourceMap
        .entrySet()
        .forEach(
            (Map.Entry<String, CalculationStateGroupBy_4> e) -> {
              if (targetMap.containsKey(e.getKey())) {
                final CalculationStateGroupBy_4 sourceState = e.getValue();
                CalculationStateGroupBy_4 newInstance = calcState.getOrCreateInstance(e.getKey());
                newInstance.combine(sourceState);
                newInstance.target.setTotalDelayMins((int) newInstance.sum3);
                newInstance.target.setAvgDelay((int) newInstance.average1);
                newInstance.target.setTotalFlights((int) newInstance.count2);
              } else {
                final CalculationStateGroupBy_4 sourceState = e.getValue();
                CalculationStateGroupBy_4 newInstance = calcState.getOrCreateInstance(e.getKey());
                newInstance.updateMap.clear();
                newInstance.updateMap.or(sourceState.updateMap);
                initFromCopy.accept(sourceState.target, newInstance.target);
                newInstance.combine(sourceState);
                newInstance.target.setTotalDelayMins((int) newInstance.sum3);
                newInstance.target.setAvgDelay((int) newInstance.average1);
                newInstance.target.setTotalFlights((int) newInstance.count2);
                wrappedList.addItem(newInstance.target);
              }
            });
    wrappedList.sort();
  }

  @Override
  public void deduct(GroupBy<CarrierDelay> other) {
    GroupBy_4 otherGroupBy = (GroupBy_4) other;
    Map<String, CalculationStateGroupBy_4> sourceMap = otherGroupBy.calcState.getInstanceMap();
    Map<String, CalculationStateGroupBy_4> targetMap = calcState.getInstanceMap();
    sourceMap
        .entrySet()
        .forEach(
            (Map.Entry<String, CalculationStateGroupBy_4> e) -> {
              if (targetMap.containsKey(e.getKey())) {
                final CalculationStateGroupBy_4 sourceState = e.getValue();
                CalculationStateGroupBy_4 newInstance = calcState.getOrCreateInstance(e.getKey());
                newInstance.deduct(sourceState);
                newInstance.target.setTotalDelayMins((int) newInstance.sum3);
                newInstance.target.setAvgDelay((int) newInstance.average1);
                newInstance.target.setTotalFlights((int) newInstance.count2);
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
  public WrappedList<CarrierDelay> comparator(Comparator comparator) {
    return wrappedList.comparator(comparator);
  }

  @Override
  public WrappedList<CarrierDelay> comparing(SerializableFunction comparingFunction) {
    return wrappedList.comparing(comparingFunction);
  }
}
