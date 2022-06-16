package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.annotations.Initialise;
import com.fluxtion.runtime.annotations.OnParentUpdate;
import com.fluxtion.runtime.annotations.OnTrigger;
import com.fluxtion.runtime.partition.LambdaReflection;
import com.fluxtion.runtime.stream.Stateful;
import lombok.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fluxtion.runtime.partition.LambdaReflection.SerializableDoubleFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableIntFunction;
import static com.fluxtion.runtime.partition.LambdaReflection.SerializableLongFunction;

public interface Predicates {

    static boolean isInteger(String in){
        try {
            Integer.parseInt(in);
            return true;
        }catch(Exception e){}
        return false;
    }

    static boolean isDouble(String in){
        try {
            Double.parseDouble(in);
            return true;
        }catch(Exception e){}
        return false;
    }

    static boolean isLong(String in){
        try {
            Long.parseLong(in);
            return true;
        }catch(Exception e){}
        return false;
    }

    static <T> LambdaReflection.SerializableFunction<T, Boolean> hasChangedFilter() {
        return new HasChanged()::objChanged;
    }

    static <K, V> SerializableFunction<Map<K, V>, Boolean> hasMapChanged() {
        return new MapHasChanged()::checkMapChanged;
    }

    static SerializableIntFunction<Boolean> hasIntChanged() {
        return new HasChanged()::intChanged;
    }

    static SerializableDoubleFunction<Boolean> hasDoubleChanged() {
        return new HasChanged()::doubleChanged;
    }

    static SerializableLongFunction<Boolean> hasLongChanged() {
        return new HasChanged()::longChanged;
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
            if(Double.isNaN(newValue) && Double.isNaN(doublePrevious)){
                return false;
            }
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

    class MapHasChanged {
        private final Map<Object, Object> oldMap = new HashMap<>();

        public <K, V> Boolean checkMapChanged(Map<K, V> map) {
            boolean changed = !map.equals(oldMap);
            oldMap.clear();
            oldMap.putAll(map);
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

    class AllUpdatedPredicate extends Stateful.StatefulWrapper {

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
