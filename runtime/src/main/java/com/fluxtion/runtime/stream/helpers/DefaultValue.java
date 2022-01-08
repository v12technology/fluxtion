package com.fluxtion.runtime.stream.helpers;

import com.fluxtion.runtime.stream.DefaultValueSupplier;
import com.fluxtion.runtime.stream.Stateful;

public class DefaultValue<T> implements DefaultValueSupplier, Stateful<T> {

    private final T defaultValue;

    public DefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getOrDefault(T input) {
        return input == null ? defaultValue : input;
    }

    @Override
    public T reset() {
        return defaultValue;
    }

    public static class DefaultInt implements DefaultValueSupplier, Stateful<Integer>{
        private final int defaultValue;
        private boolean inputUpdatedAtLeastOnce;

        public DefaultInt(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        public int getOrDefault(int input) {
            inputUpdatedAtLeastOnce |= input != 0;
            if(inputUpdatedAtLeastOnce) {
                return input;
            }
            return defaultValue;
        }

        @Override
        public Integer reset() {
            inputUpdatedAtLeastOnce = false;
            return defaultValue;
        }
    }

    public static class DefaultDouble implements DefaultValueSupplier, Stateful<Double>{
        private final double defaultValue;
        private boolean inputUpdatedAtLeastOnce;

        public DefaultDouble(double defaultValue) {
            this.defaultValue = defaultValue;
        }

        public double getOrDefault(double input) {
            inputUpdatedAtLeastOnce |= input != 0;
            if(inputUpdatedAtLeastOnce) {
                return input;
            }
            return defaultValue;
        }

        @Override
        public Double reset() {
            inputUpdatedAtLeastOnce = false;
            return defaultValue;
        }
    }

    public static class DefaultLong implements DefaultValueSupplier, Stateful<Long>{
        private final long defaultValue;
        private boolean inputUpdatedAtLeastOnce;

        public DefaultLong(long defaultValue) {
            this.defaultValue = defaultValue;
        }

        public long getOrDefault(long input) {
            inputUpdatedAtLeastOnce |= input != 0;
            if(inputUpdatedAtLeastOnce) {
                return input;
            }
            return defaultValue;
        }

        @Override
        public Long reset() {
            inputUpdatedAtLeastOnce = false;
            return defaultValue;
        }
    }
}
