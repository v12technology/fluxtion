/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.generator.util;

import com.fluxtion.api.event.Event;
import com.fluxtion.api.lifecycle.EventHandler;
import com.fluxtion.builder.node.SEPConfig;
import static com.fluxtion.generator.compiler.InprocessSepCompiler.sepTestInstance;
import java.util.function.Consumer;
import net.vidageek.mirror.dsl.Mirror;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
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
