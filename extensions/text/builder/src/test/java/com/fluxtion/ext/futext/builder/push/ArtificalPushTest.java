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

import static com.fluxtion.ext.streaming.builder.stream.StreamOperatorService.stream;
import com.fluxtion.ext.text.builder.ascii.AsciiHelper;
import com.fluxtion.ext.text.builder.util.StringDriver;
import com.fluxtion.generator.util.BaseSepInprocessTest;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author greg
 */
public class ArtificalPushTest extends BaseSepInprocessTest {

    @Test
    public void generateProcessor() throws Exception {
        sep((c) -> {
            SaleItem saleItem = c.addPublicNode(new SaleItem(), "saleItem");
            stream(AsciiHelper.readInt("quantity")).push(Number::intValue, saleItem::setQuantity);
        });
        StringDriver.streamChars("quantity:250 nothing x", sep, true);
        SaleItem saleItem = getField("saleItem");
        Assert.assertThat(saleItem.getQuantity(), is(250));
    }

}
