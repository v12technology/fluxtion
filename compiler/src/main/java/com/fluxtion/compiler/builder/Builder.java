package com.fluxtion.compiler.builder;

import com.fluxtion.runtime.Anchor;
import com.fluxtion.runtime.EventProcessorConfigService;

public interface Builder {

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
    static <S, T> S anchor(T anchor, S afterAnchor) {
        EventProcessorConfigService.service().addOrReuse(
                new Anchor(
                        EventProcessorConfigService.service().addOrReuse(anchor),
                        EventProcessorConfigService.service().addOrReuse(afterAnchor)
                )
        );
        return afterAnchor;
    }


    /**
     * Anchor multiple instances to a single anchor, {@link Builder#anchor}
     *
     * @param anchor       The anchor node that will be invoked first
     * @param afterAnchors The list of nodes that will be notified after the anchor node
     */
    static void anchor(Object anchor, Object... afterAnchors) {
        for (Object afterAnchor : afterAnchors) {
            anchor(anchor, afterAnchor);
            anchor = afterAnchor;
        }
    }
}
