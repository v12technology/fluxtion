---
title: DataFlow pnl
parent: Examples
has_children: false
nav_order: 5
published: true
example_src: https://github.com/v12technology/fluxtion-examples/tree/main/cookbook/src/main/java/com/fluxtion/example/cookbook/pnl

---
# Example project
A maven project is [here]({{page.example_src}}) 

# DataFlow example to calculate realtime pnl
---
This example demonstrates using tbe DataFlow api to calculate realtime positions and pnl from streaming 
trade and market data events. A DataFlow acts like a database materialized view defined with a java 
streams like api. 

- A DataFlow is set of stateful or stateless calculations that are defined using DataFlow api
- An event processor embeds the DataFlow and routes events to the calculations
- Pass a DataFlow definition to a Fluxtion builder to embed the DataFlow in an event processor

# Project requirements

Calculate the pnl of a set of assets relative to a base currency or instrument. Assets are bought and sold with trades
a running total of positions is aggregated, each asset aggregate total is tracked separately. A trade comprises two
assets and an asset can be on either side of trade, the order of assets is arbitrary. The aggregate sum of an asset is
the dealt aggregate and dealt aggregate. The asset mark to market position(mtmPosition) is then calculated in terms of 
some other instrument using a conversion rate. The mtmPositions are added together to give the total pnl for all trading.

# Events
incoming events that must be processed:

- Trade a pair of assets that will affect the current position
- MidPrice the conversion rate between asssets can afftect mtmPosition value
- MtmInstrument the insturment that mtmPositions are relative to

# Project solution

the complete solution looks like this:

```java
public static void main(String[] args) {
    var pnlCalculator = Fluxtion.interpret(c -> {
              var tradeStream = DataFlow.subscribe(Trade.class);
              var dealtPosition = tradeStream.groupBy(Trade::dealtInstrument, TradeToPosition::aggregateDealt);
              var contraPosition = tradeStream.groupBy(Trade::contraInstrument, TradeToPosition::aggregateContra);
  
              DerivedRateNode derivedRate = new DerivedRateNode();
              JoinFlowBuilder.outerJoin(dealtPosition, contraPosition, InstrumentPosMtm::merge)
                      .publishTrigger(derivedRate)
                      .mapValues(derivedRate::calculateInstrumentPosMtm)
                      .map(new PnlSummaryCalc()::updateSummary)
                      .console();
            }
    );
  
    pnlCalculator.init();
}
```

## Getting data into a DataFlow

To get external data into a DataFlow we use the `DataFlow.subscribe(Class<T> eventClass)` which can be pushed to from the outside
world using `EventProcess.onEvent(<T> eventInstaance)`, demonstrated in the snippet below:

```java
public static void main(String[] args) {
    var processor = Fluxtion.interpret(c ->{
      DataFlow.subscribe(Trade.class)
              .console("trade in -> {}");
    });
    processor.init();
  
    processor.onEvent(new Trade(symbolEURJPY, -400, 80000));
    processor.onEvent(new Trade(symbolEURUSD, 500, -1100));
    processor.onEvent(new Trade(symbolUSDCHF, 500, -1100));
}
    
```    
produces the following output to console

{% highlight console %}
trade in -> Trade[symbol=Symbol[symbolName=EURJPY, dealtInstrument=Instrument[instrumentName=EUR], contraInstrument=Instrument[instrumentName=JPY]], dealtVolume=-400.0, contraVolume=80000.0]
trade in -> Trade[symbol=Symbol[symbolName=EURUSD, dealtInstrument=Instrument[instrumentName=EUR], contraInstrument=Instrument[instrumentName=USD]], dealtVolume=500.0, contraVolume=-1100.0]
trade in -> Trade[symbol=Symbol[symbolName=USDCHF, dealtInstrument=Instrument[instrumentName=USD], contraInstrument=Instrument[instrumentName=CHF]], dealtVolume=500.0, contraVolume=-1100.0]
{% endhighlight %}

## Calculating aggregate position of an asset

Once we have a DataFlow stream defined with a DataFlow.subscribe we can now perform operations on it to filter and aggregate. We
will group the trade volume by its dealt instrument and apply an aggregate function to each new item to be added to a group.
Think of the group like a table in memory whose primary key is supplied with the method reference `Trade::dealtInstrument` 

The aggregate function is stateful, an instance is allocated to a group. A supplier of function is passed into the DataFlow
groupBy declaration with the method reference `TradeToPosition::aggregateDealt`

### Aggregate function
The aggregate function that converts and reduces trades into a single aggregate InstrumentPosMtm. A user implements the 
AggregateFlowFunction interface to build an aggregate function.

```java
public class TradeToPosition implements AggregateFlowFunction<Trade, InstrumentPosMtm, TradeToPosition> {
    private InstrumentPosMtm instrumentPosMtm = new InstrumentPosMtm();
    private final boolean dealtSide;

    public TradeToPosition(boolean dealtSide) {
        this.dealtSide = dealtSide;
    }

    public static TradeToPosition aggregateDealt() {
        return new TradeToPosition(true);
    }

    public static TradeToPosition aggregateContra() {
        return new TradeToPosition(false);
    }

    @Override
    public InstrumentPosMtm aggregate(Trade input) {
        final double previousPosition = instrumentPosMtm.getPosition();
        if (dealtSide) {
            instrumentPosMtm.setInstrument(input.dealtInstrument());
            final double dealtPosition = input.dealtVolume();
            instrumentPosMtm.setPosition(Double.isNaN(previousPosition) ? dealtPosition : dealtPosition + previousPosition);
        } else {
            instrumentPosMtm.setInstrument(input.contraInstrument());
            final double contraPosition = input.contraVolume();
            instrumentPosMtm.setPosition(Double.isNaN(previousPosition) ? contraPosition : contraPosition + previousPosition);
        }
        return instrumentPosMtm;
    }

    @Override
    public InstrumentPosMtm get() {
        return instrumentPosMtm;
    }

    @Override
    public InstrumentPosMtm reset() {
        instrumentPosMtm = new InstrumentPosMtm();
        return instrumentPosMtm;
    }
}
```
### Aggregating DataFlow

Using the aggregate function with a groupBy is defined as follows

```java
public static void main(String[] args) {
    var processor = Fluxtion.interpret(c ->{
      DataFlow.subscribe(Trade.class)
              .groupBy(Trade::dealtInstrument, TradeToPosition::aggregateDealt)
              .console("trade dealt position by instrument -> {}");
    });
    processor.init();
  
    processor.onEvent(new Trade(symbolEURJPY, -400, 80000));
    processor.onEvent(new Trade(symbolEURUSD, 500, -1100));
    processor.onEvent(new Trade(symbolUSDCHF, 500, -1100));
}
```  
produces the following output to console

{% highlight console %}
trade dealt position by instrument -> GroupByFlowFunctionWrapper{mapOfValues={Instrument[instrumentName=EUR]=InstrumentPosMtm(instrument=Instrument[instrumentName=EUR], position=-400.0, mtmPosition=0.0)}}
trade dealt position by instrument -> GroupByFlowFunctionWrapper{mapOfValues={Instrument[instrumentName=EUR]=InstrumentPosMtm(instrument=Instrument[instrumentName=EUR], position=100.0, mtmPosition=0.0)}}
trade dealt position by instrument -> GroupByFlowFunctionWrapper{mapOfValues={Instrument[instrumentName=EUR]=InstrumentPosMtm(instrument=Instrument[instrumentName=EUR], position=100.0, mtmPosition=0.0), Instrument[instrumentName=USD]=InstrumentPosMtm(instrument=Instrument[instrumentName=USD], position=500.0, mtmPosition=0.0)}}
{% endhighlight %}

The running total of contra positions are calculated with


```java
public static void main(String[] args) {
    var processor = Fluxtion.interpret(c ->{
      DataFlow.subscribe(Trade.class)
              .groupBy(Trade::contraInstrument, TradeToPosition::aggregateContra)
              .console("trade contra position by instrument -> {}");
    });
    processor.init();
  
    processor.onEvent(new Trade(symbolEURJPY, -400, 80000));
    processor.onEvent(new Trade(symbolEURUSD, 500, -1100));
    processor.onEvent(new Trade(symbolUSDCHF, 500, -1100));
}
```  

## Merging dealt and contra positions
We now have two separate group by tables that hold position data rows. We need to merge these together so we have single 
position for an asset. We use the outerJoin as we want to include all rows as an asset may appear on either side or both
sides of the join

```java
public static void main(String[] args) {
    var processor = Fluxtion.interpret(c -> {
                var tradeStream = DataFlow.subscribe(Trade.class);
                var dealtPosition = tradeStream.groupBy(Trade::dealtInstrument, TradeToPosition::aggregateDealt);
                var contraPosition = tradeStream.groupBy(Trade::contraInstrument, TradeToPosition::aggregateContra);

                JoinFlowBuilder.outerJoin(dealtPosition, contraPosition, InstrumentPosMtm::merge)
                        .console("merged trade position by instrument -> {}");
            }
    );
    processor.init();
  
    processor.onEvent(new Trade(symbolEURJPY, -400, 80000));
    processor.onEvent(new Trade(symbolEURUSD, 500, -1100));
    processor.onEvent(new Trade(symbolUSDCHF, 500, -1100));
}
```  

produces the following output to console

{% highlight console %}
merged trade position by instrument -> GroupByHashMap{map={Instrument[instrumentName=JPY]=InstrumentPosMtm(instrument=Instrument[instrumentName=JPY], position=80000.0, mtmPosition=0.0), Instrument[instrumentName=EUR]=InstrumentPosMtm(instrument=Instrument[instrumentName=EUR], position=-400.0, mtmPosition=0.0)}}
merged trade position by instrument -> GroupByHashMap{map={Instrument[instrumentName=JPY]=InstrumentPosMtm(instrument=Instrument[instrumentName=JPY], position=80000.0, mtmPosition=0.0), Instrument[instrumentName=EUR]=InstrumentPosMtm(instrument=Instrument[instrumentName=EUR], position=100.0, mtmPosition=0.0), Instrument[instrumentName=USD]=InstrumentPosMtm(instrument=Instrument[instrumentName=USD], position=-1100.0, mtmPosition=0.0)}}
merged trade position by instrument -> GroupByHashMap{map={Instrument[instrumentName=CHF]=InstrumentPosMtm(instrument=Instrument[instrumentName=CHF], position=-1100.0, mtmPosition=0.0), Instrument[instrumentName=JPY]=InstrumentPosMtm(instrument=Instrument[instrumentName=JPY], position=80000.0, mtmPosition=0.0), Instrument[instrumentName=EUR]=InstrumentPosMtm(instrument=Instrument[instrumentName=EUR], position=100.0, mtmPosition=0.0), Instrument[instrumentName=USD]=InstrumentPosMtm(instrument=Instrument[instrumentName=USD], position=-600.0, mtmPosition=0.0)}}
{% endhighlight %}

## Calculating mark to market

Now we have the aggregated position of an asset we want to calculate its position in terms of a mark to market instrument.
We use a function on a java class DerivedNode that listens to mid rates and calculates a conversion rate between two assets.
The function is applied with the mapValue call on the groupBy DataFlow

`.mapValues(derivedRate::calculateInstrumentPosMtm)`

Once the mtmPosition is calculated for each row in the groupBy table we can reduce the rows to a single PnlSummary using the
map call:

`.map(new PnlSummaryCalc()::updateSummary)`

The DerivedRateNode has event handlers for MidRate and MtmInstrument and ensures the correct conversion rate is uesd in 
the mtm calculation whenever either of these changes.

```java
public static void main(String[] args) {
    var pnlCalculator = Fluxtion.interpret(c -> {
              var tradeStream = DataFlow.subscribe(Trade.class);
              var dealtPosition = tradeStream.groupBy(Trade::dealtInstrument, TradeToPosition::aggregateDealt);
              var contraPosition = tradeStream.groupBy(Trade::contraInstrument, TradeToPosition::aggregateContra);
  
              DerivedRateNode derivedRate = new DerivedRateNode();
              JoinFlowBuilder.outerJoin(dealtPosition, contraPosition, InstrumentPosMtm::merge)
                      .publishTrigger(derivedRate)
                      .mapValues(derivedRate::calculateInstrumentPosMtm)
                      .map(new PnlSummaryCalc()::updateSummary)
                      .console();
            }
    );
    pnlCalculator.init();

    pnlCalculator.onEvent(new Trade(symbolEURJPY, -400, 80000));
    pnlCalculator.onEvent(new Trade(symbolEURUSD, 500, -1100));
    pnlCalculator.onEvent(new Trade(symbolUSDCHF, 500, -1100));
    pnlCalculator.onEvent(new Trade(symbolEURGBP, 1200, -1000));
    pnlCalculator.onEvent(new Trade(symbolGBPUSD, 1500, -700));

    pnlCalculator.onEvent(new MidPrice(symbolEURGBP, 0.9));
    pnlCalculator.onEvent(new MidPrice(symbolEURUSD, 1.1));
    pnlCalculator.onEvent(new MidPrice(symbolEURCHF, 1.2));
    System.out.println("---------- final rate -----------");
    pnlCalculator.onEvent(new MidPrice(symbolEURJPY, 200));

    System.out.println("---------- final trade -----------");
    pnlCalculator.onEvent(new Trade(symbolGBPUSD, 20, -25));

    System.out.println("---------- change mtm EUR -----------");
    pnlCalculator.onEvent(new MtmInstrument(EUR));
}
```

running the example produces the following output

{% highlight console %}
PnlSummary{
mtmInstrument=USD
pnl=NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:-400.0 mtmPos:NaN}
PnlSummary{
mtmInstrument=USD
pnl=NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:100.0 mtmPos:NaN
USD pos:-1100.0 mtmPos:-1100.0}
PnlSummary{
mtmInstrument=USD
pnl=NaN
CHF pos:-1100.0 mtmPos:NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:100.0 mtmPos:NaN
USD pos:-600.0 mtmPos:-600.0}
PnlSummary{
mtmInstrument=USD
pnl=NaN
CHF pos:-1100.0 mtmPos:NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:1300.0 mtmPos:NaN
GBP pos:-1000.0 mtmPos:NaN
USD pos:-600.0 mtmPos:-600.0}
PnlSummary{
mtmInstrument=USD
pnl=NaN
CHF pos:-1100.0 mtmPos:NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:1300.0 mtmPos:NaN
GBP pos:500.0 mtmPos:NaN
USD pos:-1300.0 mtmPos:-1300.0}
PnlSummary{
mtmInstrument=USD
pnl=NaN
CHF pos:-1100.0 mtmPos:NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:1300.0 mtmPos:NaN
GBP pos:500.0 mtmPos:NaN
USD pos:-1300.0 mtmPos:-1300.0}
PnlSummary{
mtmInstrument=USD
pnl=NaN
CHF pos:-1100.0 mtmPos:NaN
JPY pos:80000.0 mtmPos:NaN
EUR pos:1300.0 mtmPos:1430.0000000000002
GBP pos:500.0 mtmPos:611.1111111111112
USD pos:-1300.0 mtmPos:-1300.0}
PnlSummary{
mtmInstrument=USD
pnl=NaN
CHF pos:-1100.0 mtmPos:-1008.3333333333334
JPY pos:80000.0 mtmPos:NaN
EUR pos:1300.0 mtmPos:1430.0000000000002
GBP pos:500.0 mtmPos:611.1111111111112
USD pos:-1300.0 mtmPos:-1300.0}
---------- final rate -----------
PnlSummary{
mtmInstrument=USD
pnl=172.77777777777783
CHF pos:-1100.0 mtmPos:-1008.3333333333334
JPY pos:80000.0 mtmPos:439.99999999999983
EUR pos:1300.0 mtmPos:1430.0000000000002
GBP pos:500.0 mtmPos:611.1111111111112
USD pos:-1300.0 mtmPos:-1300.0}
---------- final trade -----------
PnlSummary{
mtmInstrument=USD
pnl=172.2222222222224
CHF pos:-1100.0 mtmPos:-1008.3333333333334
JPY pos:80000.0 mtmPos:439.99999999999983
EUR pos:1300.0 mtmPos:1430.0000000000002
GBP pos:520.0 mtmPos:635.5555555555557
USD pos:-1325.0 mtmPos:-1325.0}
---------- change mtm EUR -----------
PnlSummary{
mtmInstrument=EUR
pnl=156.56565656565658
CHF pos:-1100.0 mtmPos:-916.6666666666667
JPY pos:80000.0 mtmPos:399.99999999999994
EUR pos:1300.0 mtmPos:1300.0
GBP pos:520.0 mtmPos:577.7777777777778
USD pos:-1325.0 mtmPos:-1204.5454545454545}

{% endhighlight %}
