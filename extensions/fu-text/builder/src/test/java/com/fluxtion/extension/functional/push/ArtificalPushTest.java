/* 
 * Copyright (C) 2018 V12 Technology Ltd.
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
package com.fluxtion.extension.functional.push;

import com.fluxtion.api.node.SEPConfig;
import com.fluxtion.extension.declarative.funclib.builder.ascii.AsciiValuePushHelper;
import com.fluxtion.generator.compiler.SepCompilerConfig;
import com.fluxtion.generator.targets.JavaTestGeneratorHelper;
import com.fluxtion.runtime.lifecycle.EventHandler;
import com.fluxtion.extension.declarative.funclib.builder.util.StringDriver;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class ArtificalPushTest {

    @Test
    public void generateProcessor() throws Exception {
        SepCompilerConfig compileCfg = JavaTestGeneratorHelper.getTestSepCompileConfig(
                "com.fluxtion.extension.functional.push.generated",
                "SalesLogProcessor");
        compileCfg.setConfigClass(Builder.class.getName());
        compileCfg.setSupportDirtyFiltering(true);


        EventHandler newInstance = JavaTestGeneratorHelper.generateAndInstantiate(compileCfg);
        SaleItem saleItem = (SaleItem) newInstance.getClass().getField("saleItem").get(newInstance);

        StringDriver.streamChars("quantity:250 nothing x", newInstance, true);
        Assert.assertThat(saleItem.getQuantity(), is(250));

    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            SaleItem saleItem = addPublicNode(new SaleItem(), "saleItem");
            AsciiValuePushHelper.setInt("quantity", saleItem, SaleItem::setQuantity);
        }

    }


}
