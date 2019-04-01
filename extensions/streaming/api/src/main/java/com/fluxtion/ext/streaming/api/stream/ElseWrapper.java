package com.fluxtion.ext.streaming.api.stream;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.streaming.api.Wrapper;
import java.util.Objects;

/**
 * Wraps a Wrapper and provides an else execution path. This is useful when
 * wrapping a filtering node.
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 * @param <T>
 */
public class ElseWrapper<T> implements Wrapper<T> {

    private final Wrapper<T> node;

    public ElseWrapper(Wrapper<T> node) {
        this.node = node;
    }

    @OnEvent(dirty = false)
    public boolean onEvent() {
        return true;
    }

    @Override
    public T event() {
        return node.event();
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) node.eventClass();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.node);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ElseWrapper<?> other = (ElseWrapper<?>) obj;
        if (!Objects.equals(this.node, other.node)) {
            return false;
        }
        return true;
    }

}
