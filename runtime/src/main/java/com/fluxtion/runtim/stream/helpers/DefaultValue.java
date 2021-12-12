package com.fluxtion.runtim.stream.helpers;

public class DefaultValue<T> {

    private final T defaultValue;

    public DefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getOrDefault(T input) {
        return input == null ? defaultValue : input;
    }

    public static class DefaultInt {
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

    public static class DefaultDouble {
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

    public static class DefaultLong {
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
