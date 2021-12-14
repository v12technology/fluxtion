package com.fluxtion.runtim.stream.helpers;

import com.fluxtion.runtim.partition.LambdaReflection;
import lombok.Value;

import static com.fluxtion.runtim.partition.LambdaReflection.*;

public interface Predicates {



    SerializableIntFunction<Boolean> HAS_CHANGED_INT = new HasChanged()::intChanged;
    SerializableDoubleFunction<Boolean> HAS_CHANGED_DOUBLE = new HasChanged()::doubleChanged;
    SerializableLongFunction<Boolean> HAS_CHANGED_LONG = new HasChanged()::longChanged;

    static <T> LambdaReflection.SerializableFunction<T, Boolean> hasChanged() {
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
}
