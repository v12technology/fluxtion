package com.fluxtion.ext.declarative.api.stream;

import com.fluxtion.ext.declarative.api.Wrapper;

/**
 *
 * @author V12 Technology Ltd.
 * @param <T>
 */
public abstract class AbstractFilterWrapper<T> implements Wrapper<T> {

    protected boolean notifyOnChangeOnly = false;
    protected boolean resetImmediate = true;
    
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
}
