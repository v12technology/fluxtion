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
package com.fluxtion.ext.futext.builder.push;

import com.fluxtion.builder.node.SEPConfig;
import com.fluxtion.ext.futext.builder.ascii.AsciiValuePushHelper;
import com.fluxtion.ext.futext.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepTest;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class ArtificalPushTest extends BaseSepTest {

    @Test
    public void generateProcessor() throws Exception {
        buildAndInitSep(Builder.class);
        StringDriver.streamChars("quantity:250 nothing x", sep, true);
        SaleItem saleItem = getField("saleItem");
        Assert.assertThat(saleItem.getQuantity(), is(250));
    }

    public static class Builder extends SEPConfig {

        public Builder() throws Exception {
            SaleItem saleItem = addPublicNode(new SaleItem(), "saleItem");
            AsciiValuePushHelper.setInt("quantity", saleItem, SaleItem::setQuantity);
        }

    }

}
