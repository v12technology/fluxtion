/* 
 * Copyright (c) 2019, V12 Technology Ltd.
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Server Side Public License, version 1,
 * as published by MongoDB, Inc.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Server Side Public License for more details.
 *
 * You should have received a copy of the Server Side Public License
 * along with this program.  If not, see 
 * <http://www.mongodb.com/licensing/server-side-public-license>.
 */
package com.fluxtion.generator.util;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.api.lifecycle.Lifecycle;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepTestInstance;
import java.util.function.Consumer;
import net.vidageek.mirror.dsl.Mirror;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * Test class utility for building a SEP in process
 *
 * @author Greg Higgins greg.higgins@v12technology.com
 */
public class BaseSepInprocessTest {

    protected EventHandler sep;
    protected boolean fixedPkg = false;
    @Rule
    public TestName testName = new TestName();

    @Before
    public void beforeTest() {
        fixedPkg = false;
    }

    protected EventHandler sep(Class<? extends EventHandler> handlerClass) {
        try {
            sep = handlerClass.newInstance();
            if (sep instanceof Lifecycle) {
                ((Lifecycle) sep).init();
            }
            return sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected EventHandler sep(Consumer<SEPConfig> cfgBuilder) {
        try {
            sep = sepTestInstance(cfgBuilder, pckName(), sepClassName());
            return sep;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected String pckName() {
        String pckg = this.getClass().getCanonicalName() + "_" + testName.getMethodName();
        pckg = pckg.toLowerCase();
        if (!fixedPkg) {
            pckg += "_" + System.currentTimeMillis();
        }
        return pckg;
    }

    protected String sepClassName() {
        return "TestSep_" + testName.getMethodName();
    }

    protected <T> T getField(String name) {
        return (T) new Mirror().on(sep).get().field(name);
    }

    protected void onEvent(Event e) {
        sep.onEvent(e);
    }

}
