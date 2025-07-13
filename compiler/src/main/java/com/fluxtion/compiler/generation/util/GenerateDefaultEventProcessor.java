/*
 * SPDX-FileCopyrightText: © 2025 Gregory Higgins <greg.higgins@v12technology.com>
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package com.fluxtion.compiler.generation.util;

import com.fluxtion.compiler.Fluxtion;
import com.fluxtion.runtime.node.ObjectEventHandlerNode;

public class GenerateDefaultEventProcessor {

    public static void main(String[] args) {
        Fluxtion.compileAot(c -> {
                    c.addNode(new ObjectEventHandlerNode());
                },
                "com.fluxtion.runtime",
                "DefaultEventProcessorPrototype");
    }
}
