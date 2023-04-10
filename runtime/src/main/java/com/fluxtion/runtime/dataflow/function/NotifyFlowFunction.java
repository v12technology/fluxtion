package com.fluxtion.runtime.dataflow.function;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.Inject;
import com.fluxtion.runtime.dataflow.DoubleFlowFunction;
import com.fluxtion.runtime.dataflow.FlowFunction;
import com.fluxtion.runtime.dataflow.IntFlowFunction;
import com.fluxtion.runtime.dataflow.LongFlowFunction;
import com.fluxtion.runtime.node.NodeNameLookup;
import lombok.ToString;

import java.util.Objects;

@ToString
public class NotifyFlowFunction<T, S extends FlowFunction<T>> extends AbstractFlowFunction<T, T, S> {

    @PushReference
    private final Object target;
    private final transient String auditInfo;
    private String instanceNameToNotify;
    @Inject
    @NoTriggerReference
    public NodeNameLookup nodeNameLookup;

    public NotifyFlowFunction(S inputEventStream, Object target) {
        super(inputEventStream, null);
        this.target = target;
        auditInfo = target.getClass().getSimpleName();
    }

    protected void initialise() {
        instanceNameToNotify = nodeNameLookup.lookupInstanceName(target);
    }

    @OnTrigger
    public boolean notifyChild() {
        auditLog.info("notifyClass", auditInfo);
        auditLog.info("notifyInstance", instanceNameToNotify);
        return fireEventUpdateNotification();
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

    @ToString
    public static class IntNotifyFlowFunction extends NotifyFlowFunction<Integer, IntFlowFunction> implements IntFlowFunction {

        public IntNotifyFlowFunction(IntFlowFunction inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }
    }

    @ToString
    public static class DoubleNotifyFlowFunction extends NotifyFlowFunction<Double, DoubleFlowFunction> implements DoubleFlowFunction {

        public DoubleNotifyFlowFunction(DoubleFlowFunction inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public double getAsDouble() {
            return getInputEventStream().getAsDouble();
        }
    }

    @ToString
    public static class LongNotifyFlowFunction extends NotifyFlowFunction<Long, LongFlowFunction> implements LongFlowFunction {

        public LongNotifyFlowFunction(LongFlowFunction inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public long getAsLong() {
            return getInputEventStream().getAsLong();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotifyFlowFunction)) return false;
        NotifyFlowFunction<?, ?> that = (NotifyFlowFunction<?, ?>) o;
        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target);
    }
}
