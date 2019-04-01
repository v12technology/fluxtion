package com.fluxtion.ext.futext.example.flightdelay.generated;

import com.fluxtion.api.annotations.EventHandler;
import com.fluxtion.api.annotations.Initialise;
import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnEventComplete;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.declarative.api.Wrapper;
import com.fluxtion.ext.declarative.api.group.AggregateFunctions.AggregateAverage;
import com.fluxtion.ext.declarative.api.group.AggregateFunctions.AggregateCount;
import com.fluxtion.ext.declarative.api.group.AggregateFunctions.AggregateSum;
import com.fluxtion.ext.declarative.api.group.GroupBy;
import com.fluxtion.ext.declarative.api.group.GroupByIniitialiser;
import com.fluxtion.ext.declarative.api.group.GroupByTargetMap;
import com.fluxtion.ext.futext.example.flightdelay.CarrierDelay;
import com.fluxtion.ext.futext.example.flightdelay.FlightDetails;
import com.fluxtion.ext.futext.example.flightdelay.generated.Filter_getDelay_By_positiveInt_1;
import java.util.BitSet;
import java.util.Map;

/**
 * generated group by holder.
 *
 * <p>target class : CarrierDelay
 *
 * @author Greg Higgins
 */
public final class GroupBy_6 implements GroupBy<CarrierDelay> {

  @NoEventReference public Object resetNotifier;
  public Filter_getDelay_By_positiveInt_1 filter_getDelay_By_positiveInt_10;
  private CarrierDelay target;
  private GroupByTargetMap<CarrierDelay, CalculationStateGroupBy_6> calcState;
  private GroupByIniitialiser<FlightDetails, CarrierDelay>
      initialiserfilter_getDelay_By_positiveInt_10;

  @OnParentUpdate("filter_getDelay_By_positiveInt_10")
  public boolean updatefilter_getDelay_By_positiveInt_10(
      Filter_getDelay_By_positiveInt_1 eventWrapped) {
    FlightDetails event = (FlightDetails) eventWrapped.event();
    CalculationStateGroupBy_6 instance = calcState.getOrCreateInstance(event.getCarrier());
    boolean allMatched =
        instance.processSource(1, initialiserfilter_getDelay_By_positiveInt_10, event);
    target = instance.target;
    {
      double value = instance.aggregateAverage3;
      value =
          instance.aggregateAverage3Function.calcAverage((double) event.getDelay(), (double) value);
      target.setAvgDelay((int) value);
      instance.aggregateAverage3 = value;
    }
    {
      int value = instance.aggregateCount4;
      value = AggregateCount.increment((int) 0, (int) value);
      target.setTotalFlights((int) value);
      instance.aggregateCount4 = value;
    }
    {
      double value = instance.aggregateSum5;
      value = AggregateSum.calcSum((double) event.getDelay(), (double) value);
      target.setTotalDelayMins((int) value);
      instance.aggregateSum5 = value;
    }
    return allMatched;
  }

  @Initialise
  public void init() {
    calcState = new GroupByTargetMap<>(CalculationStateGroupBy_6.class);
    initialiserfilter_getDelay_By_positiveInt_10 =
        new GroupByIniitialiser<FlightDetails, CarrierDelay>() {

          @Override
          public void apply(FlightDetails source, CarrierDelay target) {
            target.setCarrierId((java.lang.String) source.getCarrier());
          }
        };
  }

  @Override
  public CarrierDelay value(Object key) {
    return calcState.getInstance(key).target;
  }

  @Override
  public <V extends Wrapper<CarrierDelay>> Map<?, V> getMap() {
    return (Map<?, V>) calcState.getInstanceMap();
  }

  @Override
  public CarrierDelay event() {
    return target;
  }

  @Override
  public Class<CarrierDelay> eventClass() {
    return CarrierDelay.class;
  }

  public GroupBy_6 resetNotifier(Object resetNotifier) {
    this.resetNotifier = resetNotifier;
    return this;
  }

  @OnParentUpdate("resetNotifier")
  public void resetNotification(Object resetNotifier) {
    init();
  }
}
