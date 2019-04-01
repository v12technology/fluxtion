package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.PushReference;
import java.util.Objects;

/**
 * Pushes event notifications from a source to a target. Can be useful for
 * gathering operations where many sources notify a single target of an upstream
 * change. 
 *
 * @author V12 Technology Ltd.
 */
public class PushNotifier {

    public Object eventSource;
    @PushReference
    public Object eventTarget;

    public PushNotifier(Object eventSource, Object eventTarget) {
        this.eventSource = eventSource;
        this.eventTarget = eventTarget;
    }

    public PushNotifier() {
    }
    
    @OnEvent
    public boolean push() {
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.eventSource);
        hash = 89 * hash + Objects.hashCode(this.eventTarget);
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
        final PushNotifier other = (PushNotifier) obj;
        if (!Objects.equals(this.eventSource, other.eventSource)) {
            return false;
        }
        if (!Objects.equals(this.eventTarget, other.eventTarget)) {
            return false;
        }
        return true;
    }
    
}
