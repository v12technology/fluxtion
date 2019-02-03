/*
 * Copyright (C) 2018 V12 Technology Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.example.core.audit;

import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.example.shared.ChildNode;
import com.fluxtion.example.shared.DataEventHandler;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.shared.PipelineNode;

/**
 *
 * @author V12 Technology Ltd.
 */
public class SepBuilderAudit {

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.audit.generated",
            outputDir = "src/main/java"
    )
    public void buildInjected(SEPConfig cfg) {
        cfg.addNode(new Combiner(
                cfg.addNode(new ChildNode(cfg.addNode(new MyEventHandler()))),
                cfg.addNode(new PipelineNode(cfg.addNode(new DataEventHandler())))
        ));

        cfg.addAuditor(new NodeAuditor(), "nodeAuditor");
    }
}
