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
package com.fluxtion.example.core.dependencyinjection;

import com.fluxtion.builder.annotation.SepBuilder;
import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.example.core.dependencyinjection.lifecycle.CleanListener;
import com.fluxtion.example.core.dependencyinjection.lifecycle.ConditioningHandler;
import com.fluxtion.example.core.dependencyinjection.lifecycle.DirtyCleanCombiner;
import com.fluxtion.example.core.dependencyinjection.lifecycle.DirtyListener;
import com.fluxtion.example.core.dependencyinjection.propertyscalar.PropertyHandler;
import com.fluxtion.example.core.dependencyinjection.reflection.FactoryNode;
import com.fluxtion.example.shared.MyEventHandler;
import com.fluxtion.example.shared.SampleEnum;
import static com.fluxtion.example.shared.SampleEnum.MONDAY;
import static com.fluxtion.example.shared.SampleEnum.SATURDAY;
import static com.fluxtion.example.shared.SampleEnum.SUNDAY;
import static com.fluxtion.example.shared.SampleEnum.THURSDAY;
import static com.fluxtion.example.shared.SampleEnum.TUESDAY;
import static com.fluxtion.example.shared.SampleEnum.WEDNESDAY;
import java.util.Arrays;
import java.util.List;

/**
 * Builders for dependency injection examples
 *
 * @author gregp
 */
public class SepBuilderDI {

//    @Disabled
    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.dependencyinjection.lifecycle.generated",
            outputDir = "src/main/java"
    )
    public void buildInjected(SEPConfig cfg) {
        ConditioningHandler myEventHandler = cfg.addNode(new ConditioningHandler());
        CleanListener cleanNode = cfg.addNode(new CleanListener(myEventHandler));
        DirtyListener dirtyNode = cfg.addNode(new DirtyListener(myEventHandler));
        cfg.addNode(new DirtyCleanCombiner(cleanNode, cleanNode));
    }

//    @Disabled
    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.dependencyinjection.propertyscalar.generated",
            outputDir = "src/main/java"
    )
    public void buildPropertyScalar(SEPConfig cfg) {
        PropertyHandler handler = cfg.addNode(new PropertyHandler(true, (byte) 0, 'a', (short) 0, 0.0f, 0, 0.0d, 0L, "0", MONDAY));
        //boolean
        handler.booleanPublicProp = true;
        handler.booleanTransientProp = true;
        handler.setBooleanBeanProp(true);
        //bytes
        handler.bytePublicProp = (byte) 1;
        handler.byteTransientProp = (byte) 2;
        handler.setByteBeanProp((byte) 3);
        //char
        handler.charPublicProp = 'b';
        handler.charTransientProp = 'c';
        handler.setCharBeanProp('d');
        //short
        handler.shortPublicProp = (short) 1;
        handler.shortTransientProp = (short) 2;
        handler.setShortBeanProp((short) 3);
        //float
        handler.floatPublicProp = (float) 1.1;
        handler.floatTransientProp = (float) 2.2;
        handler.setFloatBeanProp((float) 3.3);
        //int
        handler.intPublicProp = (int) 1;
        handler.intTransientProp = (int) 2;
        handler.setIntBeanProp((int) 3);
        //double
        handler.doublePublicProp = (double) 1.1;
        handler.doubleTransientProp = (double) 2.2;
        handler.setDoubleBeanProp((double) 3.3);
        //long
        handler.longPublicProp = (long) 1l;
        handler.longTransientProp = (long) 2l;
        handler.setLongBeanProp((long) 3l);
        //enum
        handler.enumPublicProp = TUESDAY;
        handler.enumTransientProp = WEDNESDAY;
        handler.setEnumBeanProp(THURSDAY);
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.dependencyinjection.propertyvector.generated",
            outputDir = "src/main/java"
    )
    public void buildPropertyVector(SEPConfig cfg) {
        com.fluxtion.example.core.dependencyinjection.propertyvector.PropertyHandler handler = cfg.addNode(new com.fluxtion.example.core.dependencyinjection.propertyvector.PropertyHandler(
                new boolean[]{true, true, false},
                Arrays.asList(1, 2, 3, 4, 5),
                new String[]{"one", "two"}
        ));

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
        handler.setIntBeanProp(ints);
        handler.setBooleanBeanProp(new boolean[]{false, false, false, false});
        handler.setStringBeanProp(Arrays.asList("AA", "BB", "CC"));
        handler.setEnumBeanProp(new SampleEnum[]{TUESDAY, THURSDAY, SATURDAY});

        handler.booleanPublicProp = Arrays.asList(true, true);
        handler.intPublicProp = new int[]{100, 200, 300, 400};
        handler.stringPublicProp = Arrays.asList("1", "2", "3", "4");
        handler.enumPublioProp = Arrays.asList(SUNDAY, MONDAY);
    }

    @SepBuilder(name = "SampleProcessor", packageName = "com.fluxtion.example.core.dependencyinjection.reflection.generated",
            outputDir = "src/main/java"
    )
    public void buildReflection(SEPConfig cfg) {
        cfg.addNode(FactoryNode.build(cfg.addNode(new MyEventHandler()), 10000));
        cfg.assignPrivateMembers = true; 
    }
}
