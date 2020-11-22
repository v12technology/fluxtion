package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.NoEventReference;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.FilterWrapper;

/**
 * A base class for stream functions. 
 * 
 * @author V12 Technology Ltd.
 * @param <T>
 */
public abstract class AbstractFilterWrapper<T> implements FilterWrapper<T> {

    protected boolean notifyOnChangeOnly = false;
    protected boolean validOnStart = false;
    protected boolean result;
    public Object publishThenResetNotifier;
    public Object resetThenPublishNotifier;
    @NoEventReference
    public Object resetNoPublishNotifier;
    protected boolean recalculate;
    protected boolean reset;
    
    @Override
    public FilterWrapper<T> publishAndReset(Object notifier) {
        publishThenResetNotifier = notifier;
        return this;
    }

    @Override
    public FilterWrapper<T> resetAndPublish(Object notifier) {
        resetThenPublishNotifier = notifier;
        return this;
    }
    
    @Override
    public FilterWrapper<T> resetNoPublish(Object notifier){
        resetNoPublishNotifier = notifier;
        return this;
    }

    @OnParentUpdate(value = "publishThenResetNotifier", guarded = true)
    public void publishThenResehNotification(Object publishThenResetNotifier){
        recalculate = false;
        reset = true;
    }
    
    @OnParentUpdate(value = "resetThenPublishNotifier", guarded = true)
    public void resetThenPublishNotification(Object resetThenPublishNotifier){
        reset();
        recalculate = false;
    }
    
    @OnParentUpdate(value = "resetNoPublishNotifier", guarded = true)
    public void resetNoPublishNotification(Object resetNoPublishNotifier){
        reset();
    }
    
    @Override
    public FilterWrapper<T> notifyOnChange(boolean notifyOnChange) {
        this.notifyOnChangeOnly = notifyOnChange;
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

    @Override
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
