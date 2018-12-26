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
package com.fluxtion.example.core.dependencyinjection.propertyvector;

import com.fluxtion.api.node.SEPConfig;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Builder extends SEPConfig {

    @Override
    public void buildConfig() {
        PropertyHandler handler = addNode(new PropertyHandler(
                new boolean[]{true, true, false},
                Arrays.asList(1, 2, 3, 4, 5),
                new String[]{"one", "two"}
        ));

        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
        handler.setIntBeanProp(ints);
        handler.setBooleanBeanProp(new boolean[]{false, false, false, false});
        handler.setStringBeanProp(Arrays.asList("AA", "BB", "CC"));

        handler.booleanPublicProp = Arrays.asList(true, true);
        handler.intPublicProp = new int[]{100, 200, 300, 400};
        handler.stringPublicProp = Arrays.asList("1", "2", "3", "4");

    }

}
