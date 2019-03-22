package com.fluxtion.ext.declarative.api.stream;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.ext.declarative.api.Wrapper;
import java.util.Objects;

/**
 * simple wrapper that wraps any node.
 * 
 * @author V12 Technology Ltd.
 * @param <T> 
 */
public class NodeWrapper<T> implements Wrapper<T> {

    private final T node;

    public NodeWrapper(T node) {
        this.node = node;
    }
    
    @OnEvent
    public boolean onEvent(){
        return true;
    }

    @Override
    public T event() {
        return node;
    }

    @Override
    public Class<T> eventClass() {
        return (Class<T>) node.getClass();
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
        final NodeWrapper<?> other = (NodeWrapper<?>) obj;
        if (!Objects.equals(this.node, other.node)) {
            return false;
        }
        return true;
    }
    
}
