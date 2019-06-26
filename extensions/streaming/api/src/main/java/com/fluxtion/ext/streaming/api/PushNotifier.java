package com.fluxtion.ext.streaming.api;

import com.fluxtion.api.annotations.OnEvent;
import com.fluxtion.api.annotations.PushReference;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Pushes event notifications from a source to a target. Can be useful for
 * gathering operations where many sources notify a single target of an upstream
 * change. 
 *
 * @author V12 Technology Ltd.
 */
@NoArgsConstructor
@AllArgsConstructor
public class PushNotifier {

    public Object eventSource;
    @PushReference
    public Object eventTarget;

    @OnEvent
    public boolean push() {
        return true;
    }
}
