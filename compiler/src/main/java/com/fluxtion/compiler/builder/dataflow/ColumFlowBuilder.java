package com.fluxtion.compiler.builder.dataflow;

import com.fluxtion.runtime.EventProcessorBuilderService;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.dataflow.column.Column;
import com.fluxtion.runtime.dataflow.column.ColumnFilterFlowFunction;
import com.fluxtion.runtime.dataflow.column.ColumnMapFlowFunction;
import com.fluxtion.runtime.dataflow.function.MapFlowFunction;
import com.fluxtion.runtime.dataflow.function.NotifyFlowFunction;
import com.fluxtion.runtime.dataflow.function.PeekFlowFunction;
import com.fluxtion.runtime.dataflow.function.PushFlowFunction;
import com.fluxtion.runtime.dataflow.helpers.Peekers;
import com.fluxtion.runtime.output.SinkPublisher;
import com.fluxtion.runtime.partition.LambdaReflection;

import java.util.List;

public class ColumFlowBuilder<T> implements FlowDataSupplier<FlowSupplier<Column<T>>> {

    protected TriggeredFlowFunction<Column<T>> eventStream;

    public ColumFlowBuilder(TriggeredFlowFunction<Column<T>> eventStream) {
        this.eventStream = eventStream;
        EventProcessorBuilderService.service().add(eventStream);
    }

    @Override
    public FlowSupplier<Column<T>> flowSupplier() {
        return eventStream;
    }

    public <R> ColumFlowBuilder<R> map(LambdaReflection.SerializableFunction<T, R> mapColumnFunction) {
        LambdaReflection.SerializableFunction<Column<T>, Column<R>> function = new ColumnMapFlowFunction<>(mapColumnFunction)::map;
        MapFlowFunction.MapRef2RefFlowFunction<Column<T>, Column<R>, TriggeredFlowFunction<Column<T>>> mapRef2RefFlowFunction = new MapFlowFunction.MapRef2RefFlowFunction<>(eventStream, function);
        return new ColumFlowBuilder<>(mapRef2RefFlowFunction);
    }

    public ColumFlowBuilder<T> filter(LambdaReflection.SerializableFunction<T, Boolean> filterFunction) {
        LambdaReflection.SerializableFunction<Column<T>, Column<T>> filter = new ColumnFilterFlowFunction<>(filterFunction)::filter;
        MapFlowFunction.MapRef2RefFlowFunction<Column<T>, Column<T>, TriggeredFlowFunction<Column<T>>> mapRef2RefFlowFunction = new MapFlowFunction.MapRef2RefFlowFunction<>(eventStream, filter);
        return new ColumFlowBuilder<>(mapRef2RefFlowFunction);
    }

    public FlowBuilder<List<T>> asList() {
        return new FlowBuilder<>(new MapFlowFunction.MapRef2RefFlowFunction<>(eventStream, Column::values));
    }

    //OUTPUTS - START
    public ColumFlowBuilder<T> notify(Object target) {
        EventProcessorBuilderService.service().add(target);
        return new ColumFlowBuilder<>(new NotifyFlowFunction<>(eventStream, target));
    }

    public ColumFlowBuilder<T> sink(String sinkId) {
        return push(new SinkPublisher<>(sinkId)::publish);
    }

    public ColumFlowBuilder<T> push(LambdaReflection.SerializableConsumer<Column<T>> pushFunction) {
        if (pushFunction.captured().length > 0) {
            EventProcessorBuilderService.service().add(pushFunction.captured()[0]);
        }
        return new ColumFlowBuilder<>(new PushFlowFunction<>(eventStream, pushFunction));
    }

    public ColumFlowBuilder<T> peek(LambdaReflection.SerializableConsumer<Column<T>> peekFunction) {
        return new ColumFlowBuilder<>(new PeekFlowFunction<>(eventStream, peekFunction));
    }


    public <R> ColumFlowBuilder<T> console(String in, LambdaReflection.SerializableFunction<Column<T>, R> peekFunction) {
        peek(Peekers.console(in, peekFunction));
        return this;
    }

    public ColumFlowBuilder<T> console(String in) {
        peek(Peekers.console(in, Peekers::columnToList));
        return this;
    }

    public ColumFlowBuilder<T> console() {
        return console("{}");
    }

    //META-DATA
    public ColumFlowBuilder<T> id(String nodeId) {
        EventProcessorBuilderService.service().add(eventStream, nodeId);
        return this;
    }

}
