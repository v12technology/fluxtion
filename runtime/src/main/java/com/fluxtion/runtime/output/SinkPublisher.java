package com.fluxtion.runtime.output;

import com.fluxtion.runtime.annotations.OnEventHandler;
import com.fluxtion.runtime.annotations.builder.AssignToField;
import com.fluxtion.runtime.node.SingleNamedNode;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public class SinkPublisher<T> extends SingleNamedNode {

    private transient final String filterString;
    private Consumer<T> sink;
    private IntConsumer intConsumer;

    private LongConsumer longConsumer;

    private DoubleConsumer doubleConsumer;

    public SinkPublisher(@AssignToField("name") String filterString) {
        super(filterString);
        this.filterString = filterString;
    }

    @OnEventHandler(filterVariable = "filterString", propagate = false)
    public void sinkRegistration(SinkRegistration<T> sinkRegistration) {
        sink = sinkRegistration.getConsumer();
        intConsumer = sinkRegistration.getIntConsumer();
        longConsumer = sinkRegistration.getLongConsumer();
        doubleConsumer = sinkRegistration.getDoubleConsumer();
    }

    @OnEventHandler(filterVariable = "filterString", propagate = false)
    public void unregisterSink(SinkDeregister sinkDeregister) {
        sink = null;
        intConsumer = null;
        longConsumer = null;
        doubleConsumer = null;
    }

    public void publish(T publishItem) {
        if (sink != null)
            sink.accept(publishItem);
    }

    public void publishInt(int value) {
        if (intConsumer != null)
            intConsumer.accept(value);
    }

    public void publishDouble(double value) {
        if (doubleConsumer != null)
            doubleConsumer.accept(value);
    }

    public void publishLong(long value) {
        if (longConsumer != null)
            longConsumer.accept(value);
    }

}
