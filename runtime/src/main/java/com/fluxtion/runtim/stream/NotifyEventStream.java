package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.NoEventReference;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.annotations.builder.Inject;
import com.fluxtion.runtim.audit.NodeNameLookup;
import lombok.ToString;

import java.util.Objects;

@ToString
public class NotifyEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    @PushReference
    private final Object target;
    private final transient String auditInfo;
    private String instanceNameToNotify;
    @Inject
    @NoEventReference
    public NodeNameLookup nodeNameLookup;

    public NotifyEventStream(S inputEventStream, Object target) {
        super(inputEventStream, null);
        this.target = target;
        auditInfo = target.getClass().getSimpleName();
    }

    protected void initialise() {
        instanceNameToNotify = nodeNameLookup.lookup(target);
    }

    @OnEvent
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
    public static class IntNotifyEventStream extends NotifyEventStream<Integer, IntEventStream> implements IntEventStream {

        public IntNotifyEventStream(IntEventStream inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }
    }

    @ToString
    public static class DoubleNotifyEventStream extends NotifyEventStream<Double, DoubleEventStream> implements DoubleEventStream {

        public DoubleNotifyEventStream(DoubleEventStream inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public double getAsDouble() {
            return getInputEventStream().getAsDouble();
        }
    }

    @ToString
    public static class LongNotifyEventStream extends NotifyEventStream<Long, LongEventStream> implements LongEventStream {

        public LongNotifyEventStream(LongEventStream inputEventStream, Object target) {
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
        if (!(o instanceof NotifyEventStream)) return false;
        NotifyEventStream<?, ?> that = (NotifyEventStream<?, ?>) o;
        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target);
    }
}
