package com.fluxtion.ext.declarative.api.stream;

import com.fluxtion.ext.declarative.api.FilterWrapper;
import com.fluxtion.ext.declarative.api.Wrapper;

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
    
    @Override
    public Wrapper<T> notifyOnChange(boolean notifyOnChange) {
        this.notifyOnChangeOnly = notifyOnChange;
        return this;
    }
    
    @Override
    public Wrapper<T> immediateReset(boolean immediateReset) {
        this.resetImmediate = immediateReset;
        return this;
    }
    
    @Override
    public Wrapper<T> alwaysReset(boolean alwaysReset) {
        this.alwaysReset = alwaysReset;
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
    
}
