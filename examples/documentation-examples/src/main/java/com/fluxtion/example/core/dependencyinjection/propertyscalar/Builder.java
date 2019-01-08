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
package com.fluxtion.example.core.dependencyinjection.propertyscalar;

import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.example.shared.SampleEnum.*;

/**
 *
 * @author V12 Technology Ltd.
 */
public class Builder extends SEPConfig{

    @Override
    public void buildConfig() {
        PropertyHandler handler = addNode(new PropertyHandler(true, (byte)0,'a',(short)0,0.0f,0,0.0d,0L,"0", MONDAY));
        //boolean
        handler.booleanPublicProp = true;
        handler.booleanTransientProp = true;
        handler.setBooleanBeanProp(true);
        //bytes
        handler.bytePublicProp = (byte)1;
        handler.byteTransientProp = (byte)2;
        handler.setByteBeanProp((byte)3);
        //char
        handler.charPublicProp = 'b';
        handler.charTransientProp = 'c';
        handler.setCharBeanProp('d');
        //short
        handler.shortPublicProp = (short)1;
        handler.shortTransientProp = (short)2;
        handler.setShortBeanProp((short)3);
        //float
        handler.floatPublicProp = (float)1.1;
        handler.floatTransientProp = (float)2.2;
        handler.setFloatBeanProp((float)3.3);
        //int
        handler.intPublicProp = (int)1;
        handler.intTransientProp = (int)2;
        handler.setIntBeanProp((int)3);
        //double
        handler.doublePublicProp = (double)1.1;
        handler.doubleTransientProp = (double)2.2;
        handler.setDoubleBeanProp((double)3.3);
        //long
        handler.longPublicProp = (long)1l;
        handler.longTransientProp = (long)2l;
        handler.setLongBeanProp((long)3l);
        //enum
        handler.enumPublicProp = TUESDAY;
        handler.enumTransientProp = WEDNESDAY;
        handler.setEnumBeanProp(THURSDAY);

    }
    
}
