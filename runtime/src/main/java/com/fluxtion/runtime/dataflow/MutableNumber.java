package com.fluxtion.runtime.dataflow;

public class MutableNumber extends Number {

    private int intValue;
    private double doubleValue;
    private long longValue;

    @Override
    public int intValue() {
        return intValue;
    }

    @Override
    public long longValue() {
        return longValue;
    }

    @Override
    public float floatValue() {
        return (float) doubleValue;
    }

    @Override
    public double doubleValue() {
        return doubleValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public MutableNumber reset() {
        intValue = 0;
        doubleValue = 0;
        longValue = 0;
        return this;
    }
}
