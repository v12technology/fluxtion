package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.ext.streaming.api.FilterWrapper;

/**
 * A base class for stream functions. 
 * 
 * @author V12 Technology Ltd.
 * @param <T>
 */
public abstract class AbstractFilterWrapper<T> implements FilterWrapper<T> {

    protected boolean notifyOnChangeOnly = false;
    protected boolean resetImmediate = true;
    protected boolean alwaysReset = false;
    protected boolean validOnStart = false;
    protected boolean result;
    
    @Override
    public FilterWrapper<T> notifyOnChange(boolean notifyOnChange) {
        this.notifyOnChangeOnly = notifyOnChange;
        return this;
    }
    
    @Override
    public FilterWrapper<T> immediateReset(boolean immediateReset) {
        this.resetImmediate = immediateReset;
        return this;
    }
    
    @Override
    public FilterWrapper<T> alwaysReset(boolean alwaysReset) {
        this.alwaysReset = alwaysReset;
        return this;
    }
    
    @Override
    public FilterWrapper<T> validOnStart(boolean validOnStart) {
        this.validOnStart = validOnStart;
        return this;
    }

    public boolean isNotifyOnChangeOnly() {
        return notifyOnChangeOnly;
    }

    public void setNotifyOnChangeOnly(boolean notifyOnChangeOnly) {
        this.notifyOnChangeOnly = notifyOnChangeOnly;
    }
    
    public boolean isResetImmediate() {
        return resetImmediate;
    }

    public void setResetImmediate(boolean resetImmediate) {
        this.resetImmediate = resetImmediate;
    }

    public boolean isAlwaysReset() {
        return alwaysReset;
    }

    public void setAlwaysReset(boolean alwaysReset) {
        this.alwaysReset = alwaysReset;
    }

    public boolean isValidOnStart() {
        return validOnStart;
    }

    public void setValidOnStart(boolean validOnStart) {
        this.validOnStart = validOnStart;
    }

    @Override
    public boolean passed() {
        return result;
    }
    
}
