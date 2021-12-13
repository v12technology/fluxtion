package com.fluxtion.runtim.stream.helpers;

import com.fluxtion.runtim.stream.DefaultValueSupplier;

public class DefaultValue<T> implements DefaultValueSupplier {

    private final T defaultValue;

    public DefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getOrDefault(T input) {
        return input == null ? defaultValue : input;
    }

    public static class DefaultInt implements DefaultValueSupplier{
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
    }

    public static class DefaultDouble implements DefaultValueSupplier{
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
    }

    public static class DefaultLong implements DefaultValueSupplier{
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
    }
}
