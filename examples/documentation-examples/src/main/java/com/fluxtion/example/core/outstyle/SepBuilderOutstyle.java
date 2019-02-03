/*
 * Copyright (C) 2019 gregp
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
package com.fluxtion.example.core.outstyle;

import com.fluxtion.builder.annotation.Disabled;
import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.example.core.outstyle.naming.DataHandler;
import com.fluxtion.example.core.outstyle.naming.NamingStrategy;

/**
 *
 * @author gregp
 */
//@Disabled
public class SepBuilderOutstyle {
    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.outstyle.mappeddispatch.generated",
            outputDir = "src/main/java"
    )
    public void buildMappeddispatch(SEPConfig cfg) {
        cfg.addNode(new DataHandler("FX"));
        cfg.addNode(new DataHandler("EQUITIES"));
        cfg.addNode(new DataHandler("BONDS"));
        cfg.filterDescriptionProducer = new NamingStrategy();
        //force mapped dispatch
        cfg.maxFiltersInline = 1;
    }
    
    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.outstyle.naming.generated",
            outputDir = "src/main/java"
    )
    public void buildNaming(SEPConfig cfg) {
        cfg.addNode(new DataHandler("FX"));
        cfg.addNode(new DataHandler("EQUITIES"));
        cfg.addNode(new DataHandler("BONDS"));
        cfg.filterDescriptionProducer = new NamingStrategy();
        //prevent mapped dispatch
        cfg.maxFiltersInline = 4;
    }
}
