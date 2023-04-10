package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.dataflow.groupby.GroupBy;

public class AbstractGroupByBuilder<K, V, T extends GroupBy<K, V>> extends FlowBuilder<T> {

    AbstractGroupByBuilder(TriggeredFlowFunction<T> eventStream) {
        super(eventStream);
    }

    <I, G extends GroupBy<K, V>>
    AbstractGroupByBuilder(MapFlowFunction<I, T, TriggeredFlowFunction<I>> eventStream) {
        super(eventStream);
    }


}
