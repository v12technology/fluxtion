package com.fluxtion.runtim.stream;

import com.fluxtion.runtim.annotations.NoEventReference;
import com.fluxtion.runtim.annotations.OnEvent;
import com.fluxtion.runtim.annotations.PushReference;
import com.fluxtion.runtim.annotations.builder.Inject;
import com.fluxtion.runtim.audit.NodeNameLookup;

public class NotifyEventStream<T, S extends EventStream<T>> extends AbstractEventStream<T, T, S> {

    @PushReference
    private final Object target;
    private final transient String auditInfo;
    private String instanceName;
    @Inject
    @NoEventReference
    public NodeNameLookup nodeNameLookup;

    public NotifyEventStream(S inputEventStream, Object target) {
        super(inputEventStream);
        this.target = target;
        auditInfo = target.getClass().getSimpleName();
    }

    protected void initialise() {
        instanceName = nodeNameLookup.lookup(target);
    }

    @OnEvent
    public boolean notifyChild() {
        auditLog.info("notifyClass", auditInfo);
        auditLog.info("notifyInstance", instanceName);
        return fireEventUpdateNotification();
    }

    @Override
    public T get() {
        return getInputEventStream().get();
    }

    public static class IntNotifyEventStream extends NotifyEventStream<Integer, IntEventStream> implements IntEventStream {

        public IntNotifyEventStream(IntEventStream inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public int getAsInt() {
            return getInputEventStream().getAsInt();
        }
    }

    public static class DoubleNotifyEventStream extends NotifyEventStream<Double, DoubleEventStream> implements DoubleEventStream {

        public DoubleNotifyEventStream(DoubleEventStream inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public double getAsDouble() {
            return getInputEventStream().getAsDouble();
        }
    }

    public static class LongNotifyEventStream extends NotifyEventStream<Long, LongEventStream> implements LongEventStream {

        public LongNotifyEventStream(LongEventStream inputEventStream, Object target) {
            super(inputEventStream, target);
        }

        @Override
        public long getAsLong() {
            return getInputEventStream().getAsLong();
        }
    }
}
