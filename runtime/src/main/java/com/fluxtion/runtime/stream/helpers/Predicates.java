package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.stream.Stateful;
import lombok.Value;

import java.util.*;

import static com.fluxtion.runtime.partition.LambdaReflection.*;

public interface Predicates {

    SerializableIntFunction<Boolean> HAS_CHANGED_INT_FILTER = new HasChanged()::intChanged;
    SerializableDoubleFunction<Boolean> HAS_CHANGED_DOUBLE_FILTER = new HasChanged()::doubleChanged;
    SerializableLongFunction<Boolean> HAS_CHANGED_LONG_FILTER = new HasChanged()::longChanged;

    static <T> LambdaReflection.SerializableFunction<T, Boolean> hasChangedFilter() {
        return new HasChanged()::objChanged;
    }

    static SerializableIntFunction<Boolean> gt(int limit) {
        return new GreaterThan(limit, Double.NaN)::check;
    }

    static SerializableLongFunction<Boolean> gt(long limit) {
        return new GreaterThan(limit, Double.NaN)::check;
    }

    static SerializableDoubleFunction<Boolean> gt(double limit) {
        return new GreaterThan(Long.MAX_VALUE, limit)::check;
    }


    static SerializableIntFunction<Boolean> lt(int limit) {
        return new LessThan(limit, Double.NaN)::check;
    }

    static SerializableLongFunction<Boolean> lt(long limit) {
        return new LessThan(limit, Double.NaN)::check;
    }

    static SerializableDoubleFunction<Boolean> lt(double limit) {
        return new LessThan(Long.MAX_VALUE, limit)::check;
    }

    class HasChanged {
        long longPrevious;
        double doublePrevious;
        int previousInt;
        Object previousObject;

        public boolean intChanged(int newValue) {
            boolean changed = newValue != previousInt;
            previousInt = newValue;
            return changed;
        }

        public boolean longChanged(long newValue) {
            boolean changed = newValue != longPrevious;
            longPrevious = newValue;
            return changed;
        }


        public boolean doubleChanged(double newValue) {
            boolean changed = newValue != doublePrevious;
            doublePrevious = newValue;
            return changed;
        }

        public <T> boolean objChanged(T newValue) {
            boolean changed = !newValue.equals(previousObject);
            previousObject = newValue;
            return changed;
        }

    }

    @Value
    class GreaterThan {
        long limit;
        double doubleLimit;

        public boolean check(int input) {
            return input > limit;
        }

        public boolean check(double input) {
            return input > doubleLimit;
        }

        public boolean check(long input) {
            return input > limit;
        }
    }

    @Value
    class LessThan {
        long limit;
        double doubleLimit;

        public boolean check(int input) {
            return input < limit;
        }

        public boolean check(double input) {
            return input < doubleLimit;
        }

        public boolean check(long input) {
            return input < limit;
        }
    }

    class AllUpdatedPredicate extends Stateful.StatefulWrapper  {

        private final List<Object> monitored = new ArrayList<>();
        private final transient Map<Object, Boolean> updateMap = new HashMap<>();
        private boolean allUpdated;

        public AllUpdatedPredicate(List<?> monitored, Object resetKey) {
            super(resetKey);
            this.monitored.addAll(monitored);
        }

        public AllUpdatedPredicate(List<?> monitored) {
            this(monitored, null);
        }

        @OnParentUpdate("monitored")
        public void parentUpdated(Object parent) {
            if (!allUpdated) {
                updateMap.put(parent, true);
                allUpdated = updateMap.values().stream().allMatch(v -> v);
            }
        }

        @OnTrigger
        public boolean propagateEvent() {
            return allUpdated;
        }

        @Initialise
        public void init() {
            allUpdated = false;
            updateMap.clear();
            monitored.forEach(p -> updateMap.put(p, false));
        }

        @Override
        public void reset() {
            init();
        }
    }
}
