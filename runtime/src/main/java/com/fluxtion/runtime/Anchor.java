package com.fluxtion.runtime;

import com.fluxtion.runtime.annotations.NoTriggerReference;
import com.fluxtion.runtime.annotations.PushReference;
import com.fluxtion.runtime.annotations.builder.ExcludeNode;
import lombok.Value;

/**
 * Creates a happens before relationship between two nodes even if there is no dependency relationship between the
 * nodes.
 *
 * @author V12 Technology Ltd.
 */
@Value
@ExcludeNode
public class Anchor {

    @NoTriggerReference
    Object anchor;
    @PushReference
    Object afterAnchor;

    /**
     * Creates a happens before relationship between two nodes even though there is no dependency relationship between
     * the nodes. This can happen if both nodes are siblings depending upon a common parent.
     *
     * @param <S>         The type of the happens after node
     * @param <T>         The type of the happens before node
     * @param anchor      The anchor node that will be invoked first
     * @param afterAnchor The node that will be notified after the anchor node
     * @return The after anchor node
     */
    public static <S, T> S anchor(T anchor, S afterAnchor) {
        EventProcessorConfigService.service().addOrReuse(
                new Anchor(
                        EventProcessorConfigService.service().addOrReuse(anchor),
                        EventProcessorConfigService.service().addOrReuse(afterAnchor)
                )
        );
        return afterAnchor;
    }

    /**
     * Anchor multiple instances to a single anchor, {@link Anchor#anchor}
     *
     * @param anchor       The anchor node that will be invoked first
     * @param afterAnchors The list of nodes that will be notified after the anchor node
     */
    public static void anchor(Object anchor, Object... afterAnchors) {
        for (Object afterAnchor : afterAnchors) {
            anchor(anchor, afterAnchor);
            anchor = afterAnchor;
        }
    }
}
