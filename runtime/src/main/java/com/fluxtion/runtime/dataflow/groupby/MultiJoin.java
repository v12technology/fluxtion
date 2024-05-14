package com.fluxtion.runtime.dataflow.groupby;

import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.builder.SepNode;
import com.fluxtion.runtime.dataflow.FlowSupplier;
import com.fluxtion.runtime.dataflow.Stateful;
import com.fluxtion.runtime.dataflow.TriggeredFlowFunction;
import com.fluxtion.runtime.partition.LambdaReflection;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@SepNode
public class MultiJoin<K, T> implements TriggeredFlowFunction<GroupBy<K, T>>, GroupBy<K, T>, Stateful<GroupBy<K, T>> {
    @Getter(AccessLevel.NONE)
    protected final transient GroupByHashMap<K, T> joinedGroup = new GroupByHashMap<>();
    private final Class<K> keyClass;
    @Getter(AccessLevel.NONE)
    private final LambdaReflection.SerializableSupplier<T> target;
    @Setter
    @Getter
    private List<LegMapper<T>> legMappers = new ArrayList<>();
    private boolean updated;


    public <A, B> MultiJoin<K, T> addJoin(FlowSupplier<GroupBy<A, B>> flow1, LambdaReflection.SerializableBiConsumer<T, B> setter1) {
        return _addOptionalJoin(flow1, setter1, false);
    }

    public <A, B> MultiJoin<K, T> addOptionalJoin(FlowSupplier<GroupBy<A, B>> flow1, LambdaReflection.SerializableBiConsumer<T, B> setter1) {
        return _addOptionalJoin(flow1, setter1, true);
    }

    @SuppressWarnings("unchecked")
    public Class<T> targetClass() {
        return (Class<T>) target.getContainingClass();
    }

    public Class<K> keyClass() {
        return keyClass;
    }

    @OnParentUpdate
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void groupByUpdated(LegMapper<T> legMapperUpdated) {
        FlowSupplier<GroupBy<Object, Object>> aflow = legMapperUpdated.flow;
        GroupBy.KeyValue keyValue = aflow.get().lastKeyValue();
        Object key = keyValue.getKey();
        T target = this.target.get();
        updated = true;
        for (int i = 0, legMappersSize = legMappers.size(); i < legMappersSize; i++) {
            LegMapper<T> legMapper = legMappers.get(i);
            if (legMapper.flow.get() == null || legMapper.flow.get().toMap().get(key) == null) {
                if (legMapper.optional) {
                    continue;
                } else {
                    updated = false;
                    joinedGroup.toMap().remove(key);
                    return;
                }
            }
            Object value = legMapper.flow.get().toMap().get(key);
            legMapper.updateTarget(target, value);
        }

        joinedGroup.toMap().put((K) key, target);
    }

    @OnTrigger
    public boolean updated() {
        boolean tempUpdateFLag = updated;
        updated = false;
        return tempUpdateFLag;
    }

    public <A, B> MultiJoin<K, T> _addOptionalJoin(
            FlowSupplier<GroupBy<A, B>> flow1,
            LambdaReflection.SerializableBiConsumer<T, B> setter1,
            boolean optional
    ) {
        LegMapper<T> legMapper = new MultiJoin.LegMapper<>();
        legMapper.setFlow(flow1);
        legMapper.setSetter(setter1);
        legMapper.setOptional(optional);
        legMappers.add(legMapper);
        return this;
    }

    @Override
    public void setUpdateTriggerNode(Object updateTriggerNode) {

    }

    @Override
    public void setPublishTriggerNode(Object publishTriggerNode) {

    }

    @Override
    public void setResetTriggerNode(Object resetTriggerNode) {

    }

    @Override
    public void setPublishTriggerOverrideNode(Object publishTriggerOverrideNode) {

    }

    @Override
    public boolean hasChanged() {
        return updated;
    }

    @Override
    public void parallel() {

    }

    @Override
    public boolean parallelCandidate() {
        return false;
    }

    @Override
    public GroupBy<K, T> get() {
        return joinedGroup;
    }

    @Override
    public Map<K, T> toMap() {
        return joinedGroup.toMap();
    }

    @Override
    public Collection<T> values() {
        return joinedGroup.values();
    }

    @Override
    public T lastValue() {
        return joinedGroup.lastValue();
    }

    @Override
    public KeyValue<K, T> lastKeyValue() {
        return joinedGroup.lastKeyValue();
    }

    @Override
    public GroupBy<K, T> reset() {
        joinedGroup.reset();
        return joinedGroup;
    }

    @Data
    @SepNode
    public static class LegMapper<T> {
        private FlowSupplier<GroupBy<Object, Object>> flow;
        private LambdaReflection.SerializableBiConsumer<T, Object> setter;
        private boolean optional = false;

        @SuppressWarnings("unchecked")
        public <A, B> void setFlow(FlowSupplier<GroupBy<A, B>> flow1) {
            this.flow = (FlowSupplier<GroupBy<Object, Object>>) (Object) flow1;
        }

        @SuppressWarnings("unchecked")
        public <R> void setSetter(LambdaReflection.SerializableBiConsumer<T, R> setter1) {
            this.setter = (LambdaReflection.SerializableBiConsumer<T, Object>) (Object) setter1;
        }

        public <R> void updateTarget(T target, R value) {
            setter.accept(target, value);
        }

        @SuppressWarnings("unchecked")
        public Class<T> targetClass() {
            return (Class<T>) setter.getContainingClass();
        }

        @OnTrigger
        public boolean groupByUpdated() {
            return true;
        }
    }
}
