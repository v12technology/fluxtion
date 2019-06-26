package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.OnParentUpdate;
import com.fluxtion.ext.streaming.api.FilterWrapper;
import com.fluxtion.ext.streaming.api.Wrapper;
import lombok.EqualsAndHashCode;

/**
 * Wraps a Wrapper and provides an else execution path. This is useful when
 * wrapping a filtering node.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
@EqualsAndHashCode(of = {"node"})
public class ElseWrapper<T> implements Wrapper<T> {

    private final FilterWrapper<T> node;
    private boolean notifyOnChangeOnly;
    private boolean published = false;
    private boolean filtered = false;

    public ElseWrapper(FilterWrapper<T> node) {
        this.node = node;
    }
    
    @OnParentUpdate(guarded = false)
    public void filterUpdated(FilterWrapper filter){
        filtered = filter.passed();
        if(filtered){
            published = false;
        }
    }
    
    @OnEvent(dirty = false)
    public boolean onEvent() {
        if(!notifyOnChangeOnly){
            return true;
        }
        if(!filtered & !published){
            published = true;
            return true;
        }
        return false;
    }

    public boolean isNotifyOnChangeOnly() {
        return notifyOnChangeOnly;
    }

    public void setNotifyOnChangeOnly(boolean notifyOnChangeOnly) {
        this.notifyOnChangeOnly = notifyOnChangeOnly;
    }

    @Override
    public Wrapper<T> notifyOnChange(boolean notifyOnChange) {
        setNotifyOnChangeOnly(notifyOnChange);
        return this;
    }

    @Override
    public T event() {
        return node.event();
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) node.eventClass();
    }

}
